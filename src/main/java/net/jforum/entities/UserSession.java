/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Stores information about an user's session.
 * @author Rafael Steil
 */
public class UserSession  {
	private static final Logger logger = Logger.getLogger(UserSession.class);
	private User user = new User();
	private RoleManager roleManager;
	private Map<Integer, Long> topicReadTime = new HashMap<Integer, Long>();
	private long lastAccessedTime;
	private long creationTime;
	private long lastVisit;
	private String sessionId;

	/**
	 * Flag a specific topic as "read" by the user
	 * It will be ignored if the user is not logged
	 * @param topicId the id of the topic to mark as read
	 */
	public void markTopicAsRead(int topicId) {
		if (this.isLogged()) {
			topicReadTime.put(topicId, System.currentTimeMillis());
		}
	}

	/**
	 * Check if the user has read a specific topic.
	 * @param topic the topic. Check will be made against <code>topic.lastPost.date</code>
	 * @return  true if the topic is read or if the user is not logged.
	 */
	public boolean isTopicRead(Topic topic) {
		if (!this.isLogged()) {
			return true;
		}

		long lastVisit = this.getLastVisit();
		long postTime = topic.getLastPost().getDate().getTime();

		if (postTime <= lastVisit) {
			return true;
		}

		Long readTime = topicReadTime.get(topic.getId());
		return readTime != null && postTime <= readTime;
	}

	/**
	 * Check if there are unread messages in a specific forum
	 * FIXME this currently only checks for the time of the last message in the forum.
	 * A correct implementation should check all posts in the forum (while not hurting performance)
	 * @param forum the forum to check
	 * @return true if there are no unread messages in the forum, or if the user is not logged
	 */
	public boolean isForumRead(Forum forum) {
		if (!this.isLogged() || forum.getTotalPosts() == 0 || forum.getLastPost() == null) {
			return true;
		}

		long lastVisit = this.getLastVisit();
		long postTime = forum.getLastPost().getDate().getTime();

		if (postTime <= lastVisit) {
			return true;
		}

		Long readTime = topicReadTime.get(forum.getLastPost().getTopic().getId());
		return readTime != null && postTime <= readTime;
	}

	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public RoleManager getRoleManager() {
		return roleManager;
	}

	public String getIp() {
		return this.getRequest().getRemoteAddr();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;

		if (user == null) {
			try {
				throw new RuntimeException("userSession.setUser with null value. See the stack trace for more information about the call stack. Session ID: "
					+ sessionId);
			}
			catch (RuntimeException e) {
				Writer writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter(writer);
				e.printStackTrace(printWriter);
				logger.warn(writer.toString());
			}
		}
	}

	/**
	 * Gets user's session start time
	 *
	 * @return Start time in miliseconds
	 */
	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long start) {
		creationTime = start;
		lastAccessedTime = start;
		lastVisit = start;
	}

	/**
	 * Gets user's last visit time
	 *
	 * @return Time in miliseconds
	 */
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public Date getLastAccessedDate() {
		return new Date(this.getLastAccessedTime());
	}

	/**
	 * @return the lastVisit
	 */
	public long getLastVisit() {
		return lastVisit;
	}

	/**
	 * @return the lastVisit as a date
	 */
	public Date getLastVisitDate() {
		return new Date(lastVisit);
	}

	/**
	 * @param lastVisit the lastVisit to set
	 */
	public void setLastVisit(long lastVisit) {
		this.lastVisit = lastVisit;
	}

	/**
	 * Updates this instance with the last accessed time of the session
	 */
	public void ping() {
		lastAccessedTime = System.currentTimeMillis();
	}

	/**
	 * Gets the session id related to this user session
	 *
	 * @return A string with the session id
	 */
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isBot() {
		return false;
	}

	/**
	 * Makes the user session anonymous
	 */
	public void becomeAnonymous(int anonymousUserId) {
		this.clearAllAttributes();

		User user = new User();
		user.setId(anonymousUserId);
		this.setUser(user);
	}

	public void becomeLogged() {
		this.setAttribute(ConfigKeys.LOGGED, "1");
	}

	public boolean isLogged() {
		return "1".equals(this.getAttribute(ConfigKeys.LOGGED));
	}
	/**
	 * Gets a cookie by its name.
	 *
	 * @param name The cookie name to retrieve
	 * @return The <code>Cookie</code> object if found, or <code>null</code> oterwhise
	 */
	public Cookie getCookie(String name) {
		Cookie[] cookies = this.getRequest().getCookies();

		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(name)) {
					return c;
				}
			}
		}

		return null;
	}

	/**
	 * Add or update a cookie. This method adds a cookie, serializing its value using XML.
	 *
	 * @param name The cookie name.
	 * @param value The cookie value
	 */
	public void addCookie(String name, String value) {
		int maxAge = 3600 * 24 * 365;

		if (value == null) {
			maxAge = 0;
			value = "";
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");

		this.getResponse().addCookie(cookie);
	}

	/**
	 * Removes a cookie
	 * @param name the name of the cookie to remove
	 */
	public void removeCookie(String name) {
		this.addCookie(name, null);
	}

	public void setAttribute(String name, Object value) {
		this.getRequest().getSession().setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return this.getRequest().getSession().getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public void clearAllAttributes() {
		HttpSession session = this.getRequest().getSession();

		for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); ) {
			String key = (String)e.nextElement();
			session.removeAttribute(key);
		}
	}

	/**
	 * Convert this instance to a {@link Session}
	 * @return
	 */
	public Session asSession() {
		Session session = new Session();

		session.setUserId(user.getId());
		session.setIp(this.getIp());
		session.setStart(new Date(this.getCreationTime()));
		session.setLastAccessed(new Date(this.getLastAccessedTime()));
		session.setLastVisit(new Date(this.getLastVisit()));

		return session;
	}

	private HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		return ((ServletRequestAttributes)attributes).getRequest();
	}

	private HttpServletResponse getResponse() {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		return (HttpServletResponse)attributes.getAttribute(ConfigKeys.HTTP_SERVLET_RESPONSE, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof UserSession)) {
			return false;
		}

		return this.getSessionId().equals(((UserSession)o).getSessionId());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getSessionId().hashCode();
	}

	/**
	 * @param time
	 */
	public void setLastAccessedTime(long time) {
		lastAccessedTime = time;
	}
}
