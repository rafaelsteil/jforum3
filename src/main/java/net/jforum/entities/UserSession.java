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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * Stores information about an user's session.
 * @author Rafael Steil
 */
@Component
@SessionScoped
public class UserSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(UserSession.class);
	private User user = new User(null);
	private RoleManager roleManager;
	private Map<Integer, Long> topicReadTime = new HashMap<Integer, Long>();
	private long lastAccessedTime;
	private long creationTime;
	private long lastVisit;
	private String sessionId;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * Flag a specific topic as "read" by the user
	 * It will be ignored if the user is not logged
	 * @param topicId the id of the topic to mark as read
	 */
	public void markTopicAsRead(int topicId) {
		if (this.isLogged()) {
			this.topicReadTime.put(topicId, System.currentTimeMillis());
		}
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Check if the user has read a specific topic.o
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

		Long readTime = this.topicReadTime.get(topic.getId());
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

		Long readTime = this.topicReadTime.get(forum.getLastPost().getTopic().getId());
		return readTime != null && postTime <= readTime;
	}

	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public RoleManager getRoleManager() {
		return this.roleManager;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getIp() {
		/*
		if(new JForumConfig().getBoolean(ConfigKeys.BLOCK_IP)) {
			return null;
		}
		*/

		// We look if the request is forwarded
		// If it is not call the older function.
		String ip = request.getHeader("X-Pounded-For");

		if (ip != null) {
			return ip;
		}

        ip = request.getHeader("x-forwarded-for");

        if (ip == null) {
        	return request.getRemoteAddr();
        }
        else {
        	// Process the IP to keep the last IP (real ip of the computer on the net)
            StringTokenizer tokenizer = new StringTokenizer(ip, ",");

            // Ignore all tokens, except the last one
            for (int i = 0; i < tokenizer.countTokens() -1 ; i++) {
            	tokenizer.nextElement();
            }

            ip = tokenizer.nextToken().trim();

            if (ip.equals("")) {
            	ip = null;
            }
        }

        // If the ip is still null, we put 0.0.0.0 to avoid null values
        if (ip == null) {
        	ip = "0.0.0.0";
        }

        return ip;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;

		if (user == null) {
			try {
				throw new RuntimeException("userSession.setUser with null value. See the stack trace for more information about the call stack. Session ID: "
					+ this.sessionId);
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
		return this.creationTime;
	}

	public void setCreationTime(long start) {
		this.creationTime = start;
		this.lastAccessedTime = start;
		this.lastVisit = start;
	}

	/**
	 * Gets user's last visit time
	 *
	 * @return Time in miliseconds
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	public Date getLastAccessedDate() {
		return new Date(this.getLastAccessedTime());
	}

	/**
	 * @return the lastVisit
	 */
	public long getLastVisit() {
		return this.lastVisit;
	}

	/**
	 * @return the lastVisit as a date
	 */
	public Date getLastVisitDate() {
		return new Date(this.lastVisit);
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
		this.lastAccessedTime = System.currentTimeMillis();
	}

	/**
	 * Gets the session id related to this user session
	 *
	 * @return A string with the session id
	 */
	public String getSessionId() {
		return this.sessionId;
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
		User user = new User();
		user.setId(anonymousUserId);
		this.setUser(user);
		setAttribute(ConfigKeys.LOGGED, "0");
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
		Cookie[] cookies = request.getCookies();

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

		response.addCookie(cookie);
	}

	/**
	 * Removes a cookie
	 * @param name the name of the cookie to remove
	 */
	public void removeCookie(String name) {
		this.addCookie(name, null);
	}

	public void setAttribute(String name, Object value) {
		request.getSession().setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return request.getSession().getAttribute(name);
	}

	/**
	 * Convert this instance to a {@link Session}
	 * @return
	 */
	public Session asSession() {
		Session session = new Session();

		session.setUserId(this.user.getId());
		session.setIp(this.getIp());
		session.setStart(new Date(this.getCreationTime()));
		session.setLastAccessed(new Date(this.getLastAccessedTime()));
		session.setLastVisit(new Date(this.getLastVisit()));

		return session;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	//@Override
	public boolean eequals(Object o) {
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
		this.lastAccessedTime = time;
	}
}
