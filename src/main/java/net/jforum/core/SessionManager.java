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
package net.jforum.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Session;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.SessionRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.sso.SSO;
import net.jforum.sso.SSOUtils;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Rafael Steil
 */
public class SessionManager {
	private static final Logger logger = Logger.getLogger(SessionManager.class);
	private Map<String, UserSession> loggedSessions = new HashMap<String, UserSession>();
	private Map<String, UserSession> anonymousSessions = new HashMap<String, UserSession>();
	private UserRepository userRepository;
	private SessionRepository sessionRepository;
	private JForumConfig config;
	private int moderatorsOnline;

	public SessionManager(JForumConfig config, UserRepository userRepository, SessionRepository sessionRepository) {
		this.config = config;
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
	}

	/**
	 * Registers a new {@link UserSession}.
	 *
	 * @param userSession The user session to add
	 */
	public synchronized void add(UserSession userSession) {
		if (StringUtils.isEmpty(userSession.getSessionId())) {
			throw new ForumException("An UserSession instance must have a session ID");
		}

		if (!userSession.isBot()) {
			this.preventDuplicates(userSession);

			if (userSession.getUser().getId() == config.getInt(ConfigKeys.ANONYMOUS_USER_ID)) {
				anonymousSessions.put(userSession.getSessionId(), userSession);
			}
			else {
				UserSession existing = this.isUserInSession(userSession.getUser().getId());

				if (existing != null) {
					userSession.setLastVisit(existing.getLastVisit());
					this.remove(existing.getSessionId());
				}
				else {
					Session session = sessionRepository.get(userSession.getUser().getId());

					if (session != null && session.getLastVisit() != null) {
						userSession.setLastVisit(session.getLastVisit().getTime());
					}
				}

				this.checkIfIsModerator(userSession);

				loggedSessions.put(userSession.getSessionId(), userSession);
			}
		}
	}

	private void checkIfIsModerator(UserSession userSession) {
		RoleManager roleManager = new RoleManager();
		roleManager.setGroups(userSession.getUser().getGroups());

		if (roleManager.isModerator()) {
			moderatorsOnline++;
		}
	}

	public void computeAllOnlineModerators() {
		moderatorsOnline = 0;
		Collection<UserSession> sessions = loggedSessions.values();

		for (UserSession session : sessions) {
			this.checkIfIsModerator(session);
		}
	}

	public boolean isModeratorOnline() {
		return moderatorsOnline > 0;
	}

	/**
	 * Make sure we'll not add a session that was already registered
	 * @param us
	 */
	private void preventDuplicates(UserSession us) {
		if (this.getUserSession(us.getSessionId()) != null) {
			this.remove(us.getSessionId());
		}
	}

	/**
	 * Remove an entry fro the session map
	 *
	 * @param sessionId The session id to remove
	 */
	public synchronized void remove(String sessionId) {
		if (loggedSessions.containsKey(sessionId)) {
			UserSession userSession = this.getUserSession(sessionId);

			if (userSession.getRoleManager() != null
				&& userSession.getRoleManager().isModerator() && moderatorsOnline > 0) {
				moderatorsOnline--;
			}

			loggedSessions.remove(sessionId);
		}
		else {
			anonymousSessions.remove(sessionId);
		}
	}

	/**
	 * Get all registered sessions
	 *
	 * @return <code>ArrayList</code> with the sessions. Each entry is an <code>UserSession</code> object.
	 */
	public List<UserSession> getAllSessions() {
		List<UserSession> list = new ArrayList<UserSession>(loggedSessions.values());
		list.addAll(anonymousSessions.values());

		return list;
	}

	/**
	 * Gets the {@link UserSession} instance of all logged users
	 *
	 * @return A list with the user sessions
	 */
	public Collection<UserSession> getLoggedSessions() {
		return loggedSessions.values();
	}

	/**
	 * Get the number of logged users
	 *
	 * @return the number of logged users
	 */
	public int getTotalLoggedUsers() {
		return loggedSessions.size();
	}

	/**
	 * Get the number of anonymous users
	 *
	 * @return the nuber of anonymous users
	 */
	public int getTotalAnonymousUsers() {
		return anonymousSessions.size();
	}

	/**
	 * Gets an {@link UserSession} by the session id.
	 *
	 * @param sessionId the session's id
	 * @return the user session
	 */
	public UserSession getUserSession(String sessionId) {
		UserSession us = anonymousSessions.get(sessionId);
		return us != null ? us : loggedSessions.get(sessionId);
	}

	/**
	 * Return the {@link UserSession} associated to the current thread
	 * @return the user session
	 */
	public UserSession getUserSession() {
		return this.getUserSession(RequestContextHolder.currentRequestAttributes().getSessionId());
	}

	/**
	 * Gets the number of session elements.
	 *
	 * @return The number of session elements currently online (without bots)
	 */
	public int getTotalUsers() {
		return anonymousSessions.size() + loggedSessions.size();
	}

	/**
	 * Check if a given user in in the session
	 *
	 * @param userId The user id to check for existance in the session
	 * @return The respective {@link UserSession} if the user is already registered, or <code>null</code> otherwise.
	 */
	public UserSession isUserInSession(int userId) {
		for (UserSession us : loggedSessions.values()) {
			if (us.getUser().getId() == userId) {
				return us;
			}
		}

		return null;
	}

	/**
	 * Do a refresh in the user's session. This method will update the
	 * last visit time for the current user, as well checking for
	 * authentication if the session is new or the SSO user has changed
	 * @throws IOException
	 */
	public UserSession refreshSession(HttpServletRequest request, HttpServletResponse response) {
		boolean isSSOAuthentication = ConfigKeys.TYPE_SSO.equals(config.getValue(ConfigKeys.AUTHENTICATION_TYPE));
		request.setAttribute("sso", isSSOAuthentication);
		request.setAttribute("ssoLogout", config.getValue(ConfigKeys.SSO_LOGOUT));

		UserSession userSession = this.getUserSession(request.getSession().getId());
		int anonymousUserId = config.getInt(ConfigKeys.ANONYMOUS_USER_ID);

		if (userSession == null) {
			userSession = new UserSession();
			userSession.setSessionId(request.getSession().getId());
			userSession.setCreationTime(System.currentTimeMillis());

			//if (!JForumExecutionContext.getForumContext().isBot()) {
			if (true) {
				if (isSSOAuthentication) {
					this.checkSSO(userSession, request);
				}
				else {
					boolean autoLoginEnabled = config.getBoolean(ConfigKeys.AUTO_LOGIN_ENABLED);
					boolean autoLoginSuccess = autoLoginEnabled && this.checkAutoLogin(userSession);

					if (!autoLoginSuccess) {
						userSession.becomeAnonymous(anonymousUserId);
						userSession.setUser(userRepository.get(anonymousUserId));
					}
				}
			}

			this.add(userSession);

			logger.info("Registered new userSession: " + request.getSession().getId());
		}
		else if (isSSOAuthentication) {
			SSO sso;

			try {
				sso = (SSO) Class.forName(config.getValue(ConfigKeys.SSO_IMPLEMENTATION)).newInstance();
			}
			catch (Exception e) {
				throw new ForumException(e);
			}

			// Check if the session is valid
			if (!sso.isSessionValid(userSession, request)) {
				User user = userSession.getUser();

				logger.info("sso session is no longer valid. Forcing a refresh. username is " + (user != null ? user.getUsername() : "returned null")
					+ ", jforumUserId is " + (user != null ? user.getId() : "returned null") + ". Session ID: " + request.getSession().getId());

				// If the session is not valid, create a new one
				this.remove(userSession.getSessionId());
				return this.refreshSession(request, response);
			}
			else {
				if (userSession.getUser().getId() == 0) {
					logger.warn("isSSOAuthentication -> get user from repository using user.id equals to zero. ");
				}

				// FIXME: Force a reload of the user instance, because if it's kept in the usersession,
				// changes made to the group (like permissions) won't be seen.
				User user = userRepository.get(userSession.getUser().getId());

				if (user == null) {
					// FIXME: now what? we didn't find the user, so something must be wrong
					logger.warn(String.format("refreshSession did not find an user that should be registered. jforumUserId is %d, session ID is %s",
						userSession.getUser().getId(), request.getSession().getId()));
				}
				else {
					userSession.setUser(user);
				}
			}
		}
		else {
			// FIXME: Force a reload of the user instance, because if it's kept in the usersession,
			// changes made to the group (like permissions) won't be seen.
			userSession.setUser(userRepository.get(userSession.getUser().getId()));
		}

		userSession.ping();

		if (userSession.getUser() == null || userSession.getUser().getId() == 0) {
			logger.warn("After userSession.ping() -> userSession.getUser returned null or user.id is zero. " +
				"User is null? " + ( userSession.getUser() == null ) + ". user.id is: "
					+ (userSession.getUser() == null ? "getUser() returned null" : userSession.getUser().getId())
					+ ". As we have a problem, will force the user to become anonymous. Session ID: " + request.getSession().getId());
			userSession.becomeAnonymous(anonymousUserId);

			User anonymousUser = userRepository.get(userSession.getUser().getId());

			if (anonymousUser == null) {
				logger.warn("Could not find the anonymous user in the database. Tried using id " + anonymousUserId);
			}
			else {
				userSession.setUser(anonymousUser);
			}
		}

		RoleManager roleManager = new RoleManager();

		if (userSession.getUser() != null) {
			roleManager.setGroups(userSession.getUser().getGroups());
		}
		else {
			logger.warn("At last step userSession.getUser() still returned null. Ignoring the roles. Session ID: " + request.getSession().getId());
		}

		userSession.setRoleManager(roleManager);

		return userSession;
	}

	/**
	 * Persist the user session to the database
	 * @param sessionId the id of the session to persist
	 */
	public void storeSession(String sessionId) {
		UserSession userSession = this.getUserSession(sessionId);

		if (userSession != null && userSession.getUser().getId() != config.getInt(ConfigKeys.ANONYMOUS_USER_ID)) {
			Session session = userSession.asSession();
			session.setLastVisit(session.getLastAccessed());
			sessionRepository.add(session);
		}
	}

	/**
	 * Checks user credentials / automatic login.
	 *
	 * @param userSession The UserSession instance associated to the user's session
	 * @return <code>true</code> if auto login was enabled and the user was sucessfuly logged in.
	 */
	private boolean checkAutoLogin(UserSession userSession) {
		Cookie userIdCookie = userSession.getCookie(config.getValue(ConfigKeys.COOKIE_USER_ID));
		Cookie hashCookie = userSession.getCookie(config.getValue(ConfigKeys.COOKIE_USER_HASH));
		Cookie autoLoginCookie = userSession.getCookie(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN));

		if (hashCookie != null && userIdCookie != null
				&& !userIdCookie.getValue().equals(config.getValue(ConfigKeys.ANONYMOUS_USER_ID))
				&& autoLoginCookie != null && "1".equals(autoLoginCookie.getValue())) {
			String userId = userIdCookie.getValue();
			String uidHash = hashCookie.getValue();

			User user = userRepository.get(Integer.parseInt(userId));

			if (user == null || user.isDeleted() || StringUtils.isEmpty(user.getSecurityHash())) {
				userSession.becomeAnonymous(config.getInt(ConfigKeys.ANONYMOUS_USER_ID));
				return false;
			}

			String securityHash = MD5.hash(user.getSecurityHash());

			if (!securityHash.equals(uidHash)) {
				userSession.becomeAnonymous(config.getInt(ConfigKeys.ANONYMOUS_USER_ID));
				return false;
			}
			else {
				userSession.setUser(user);
				this.configureUserSession(userSession, user);
				return true;
			}
		}

		return false;
	}

	/**
	 * Setup optios and values for the user's session if authentication was ok.
	 *
	 * @param userSession The UserSession instance of the user
	 * @param user The User instance of the authenticated user
	 */
	private void configureUserSession(UserSession userSession, User user) {
		userSession.setUser(user);
		userSession.becomeLogged();
	}

	/**
	 * Checks for user authentication using some SSO implementation
	 *
	 * @param userSession UserSession
	 * @param request TODO
	 */
	private void checkSSO(UserSession userSession, HttpServletRequest request) {
		try {
			SSO sso = (SSO)Class.forName(config.getValue(ConfigKeys.SSO_IMPLEMENTATION)).newInstance();
			sso.setConfig(config);
			String username = sso.authenticateUser(request);

			logger.info(String.format("SSO authenticated an user with username %s. Session ID %s", username, request.getSession().getId()));

			if (StringUtils.isEmpty(username)) {
				logger.warn(String.format("checkSSO found an empty / null username. Going anonymous. Session ID %s", request.getSession().getId()));
				userSession.becomeAnonymous(config.getInt(ConfigKeys.ANONYMOUS_USER_ID));
			}
			else {
				SSOUtils utils = new SSOUtils(userRepository);
				boolean userExists = utils.userExists(username);

				logger.info(String.format("SSO user %s exists? %s", username, userExists));

				if (!userExists) {
					String email = (String)userSession.getAttribute(
						config.getValue(ConfigKeys.SSO_EMAIL_ATTRIBUTE));

					String password = (String)userSession.getAttribute(
						config.getValue(ConfigKeys.SSO_PASSWORD_ATTRIBUTE));

					if (email == null) {
						email = config.getValue(ConfigKeys.SSO_DEFAULT_EMAIL);
					}

					if (password == null) {
						password = config.getValue(ConfigKeys.SSO_DEFAULT_PASSWORD);
					}

					utils.register(password, email);
				}

				User user = utils.getUser();

				logger.info(String.format("g: username=%s, jforumUserId=%s",
					user != null ? user.getUsername() : "returned null",
					user != null ? user.getId() : "returned null"));

				this.configureUserSession(userSession, user);

				if (user == null || user.getId() == 0) {
					logger.warn("checkSSO -> utils.getUser() returned null or user.id is zero");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ForumException("Error while executing SSO actions: " + e);
		}
	}
}
