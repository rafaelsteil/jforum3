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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.entities.Session;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.SessionRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;
import net.jforum.util.SecurityConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTestCase {
	
	@Mock private JForumConfig config;
	@Mock private SessionRepository sessionRepository;
	@Mock private UserRepository userRepository;
	
	@Mock private HttpServletRequest request;
	@Mock private HttpServletResponse response;
	@Mock private HttpSession httpSession;
	
	@InjectMocks private SessionManager manager;
	
	Cookie[] goodCookie;

	@Before
	public void setup() {
		goodCookie = new Cookie[] {
			new Cookie("cookieNameData", "2"),
			new Cookie("cookieUserHash", MD5.hash("123")),
			new Cookie("cookieAutoLogin", "1")
		};
		
		
		when(config.getInt(ConfigKeys.ANONYMOUS_USER_ID)).thenReturn(1);
		when(config.getValue(ConfigKeys.ANONYMOUS_USER_ID)).thenReturn("1");
		when(config.getValue(ConfigKeys.COOKIE_USER_ID)).thenReturn("cookieNameData");
		when(config.getValue(ConfigKeys.COOKIE_USER_HASH)).thenReturn("cookieUserHash");
		when(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN)).thenReturn("cookieAutoLogin");
		when(config.getValue(ConfigKeys.AUTHENTICATION_TYPE)).thenReturn("x");
		when(request.getSession()).thenReturn(httpSession);
	}
	
	@After
	public void tearDown() {
		// FIXME: that shouldn't be needed...
		manager.reinitialiseAllSessions();
	}
	
	@Test
	public void loginAfterTimeoutShouldFetchFromSessionRepositoryExpectLastVisitCorrect() {
		UserSession us = this.newUserSession("123");
		us.getUser().setId(2);
		us.setCreationTime(1);
		us.setLastAccessedTime(2);
		us.setLastVisit(3);
		
		Session session = new Session();
		session.setLastVisit(new Date(7));
		
		when(sessionRepository.get(2)).thenReturn(session);
		
		manager.add(us);
		
		assertEquals(7, us.getLastVisit());
	}

	@Test
	public void loginBackBeforeExpireExpectLastVisitCorrect() {
		UserSession us = this.newUserSession("123"); us.getUser().setId(2);
		us.setCreationTime(1); us.setLastVisit(9);

		manager.add(us);
		assertEquals(1, manager.getTotalUsers());

		UserSession us2 = this.newUserSession("456"); us2.getUser().setId(2);
		us2.setCreationTime(1); us2.setLastVisit(5);

		manager.add(us2);
		
		assertEquals(9, us2.getLastVisit());
		assertEquals(1, manager.getTotalUsers());
	}

	@Test
	public void storeSessionNotRegisteredShouldIgnore() {
		manager.storeSession("invalid");
	}

	@Test
	public void storeSessionExpectSuccess() {
		when(httpSession.getId()).thenReturn("123");
	
		UserSession us = new UserSession();
		us.setSessionId("123");
		us.setRequest(request);
		us.setResponse(response);
		us.getUser().setId(2);

		manager.add(us);
		manager.storeSession("123");
		
		verify(sessionRepository).add(notNull(Session.class));
	}

	@Test
	public void storeSessionIsAnonymousShouldIgnore() {
		when(httpSession.getId()).thenReturn("123");
	
		UserSession us = new UserSession();
		us.setSessionId("123");
		us.getUser().setId(1);

		manager.add(us);
		manager.storeSession("123");
	}

	@Test
	public void autoLoginAllInformationIsGoodShouldAccept() {
		commonAutoLoginMockAction();
		User user = new User();
		user.setId(2);
		user.setSecurityHash("123");
		
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		when(request.getCookies()).thenReturn(goodCookie);
		when(userRepository.get(2)).thenReturn(user);
		
		manager.refreshSession(userSession);

		verify(httpSession).setAttribute(ConfigKeys.LOGGED, "1");
	}

	@Test
	public void autoLoginValidUserInvalidSecurityHashShouldDeny() {
		this.commonAutoLoginMockAction();
		User user = new User();
		user.setId(2);
		user.setSecurityHash("abc");

		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		when(request.getCookies()).thenReturn(goodCookie);
		when(userRepository.get(2)).thenReturn(user);
		when(userRepository.get(1)).thenReturn(new User());
		
		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginValidUserEmptySecurityHashShouldDeny() {
		this.commonAutoLoginMockAction();
		User user = new User();
		user.setId(2);
		user.setSecurityHash(null);
		
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		when(request.getCookies()).thenReturn(goodCookie);
		when(userRepository.get(2)).thenReturn(user);
		when(userRepository.get(1)).thenReturn(new User());

		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginValidUserHasDeletedFlagShouldDeny() {
		this.commonAutoLoginMockAction();
		User user = new User();
		user.setId(2);
		user.setDeleted(true);
		
		when(request.getCookies()).thenReturn(goodCookie);
		when(userRepository.get(2)).thenReturn(user);
		when(userRepository.get(1)).thenReturn(new User());
		
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginValidUserNotfoundInRepositoryShouldDeny() {
		this.commonAutoLoginMockAction();

		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);
		
		when(request.getCookies()).thenReturn(goodCookie);
		when(userRepository.get(2)).thenReturn(null);
		when(userRepository.get(1)).thenReturn(new User());
		
		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginValidUserCookieValueNot1ShouldDeny() {
		this.commonAutoLoginMockAction();
		
		Cookie[] cookies = new Cookie[] {
			new Cookie("cookieNameData", "2"),
			new Cookie("cookieUserHash", "a"),
			new Cookie("cookieAutoLogin", "0")
		};
	
		when(request.getCookies()).thenReturn(cookies);
		when(userRepository.get(1)).thenReturn(new User());
		
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginAnonymousUserShouldDeny() {
		this.commonAutoLoginMockAction();

		Cookie[] cookies = new Cookie[] {
			new Cookie("cookieNameData", "1"),
			new Cookie("cookieUserHash", "a"),
			new Cookie("cookieAutoLogin", "1")
		};
		when(request.getCookies()).thenReturn(cookies);
		when(userRepository.get(1)).thenReturn(new User());
		
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		manager.refreshSession(userSession);
	}

	@Test
	public void autoLoginDoestNotHaveCookiesShouldDeny() {
		this.commonAutoLoginMockAction();
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);
		
		when(userRepository.get(1)).thenReturn(new User());
		
		manager.refreshSession(userSession);
	}

	@Test
	public void refreshExistingSessionShouldFetchUserAndRoleManager() {
		User user = new User();
		user.setId(1);
		UserSession us = this.newUserSession("123");
		us.setRequest(request);
		us.setResponse(response);
		us.setUser(user);
		
		when(httpSession.getId()).thenReturn("123");
		when(userRepository.get(user.getId())).thenReturn(user);
		
		manager.add(us);
		manager.refreshSession(us);
		
		assertNotNull(us.getRoleManager());
	}

	@Test
	public void refreshSessionIsNewShouldCreateAutoLoginDisabledUsingAnonymousUser() {
		User anonymousUser = new User();
		anonymousUser.setId(1);
		UserSession userSession = new UserSession();
		userSession.setRequest(request);
		userSession.setResponse(response);

		when(httpSession.getId()).thenReturn("123");
		when(config.getBoolean(ConfigKeys.AUTO_LOGIN_ENABLED)).thenReturn(false);
		when(userRepository.get(1)).thenReturn(anonymousUser);
		when(config.getValue(ConfigKeys.SSO_LOGOUT)).thenReturn("x");
		
		UserSession us = manager.refreshSession(userSession);
		
		assertNotNull(us);
		assertEquals(anonymousUser, us.getUser());
		assertNotNull(us.getRoleManager());
		assertEquals("123", us.getSessionId());
		
		verify(request).setAttribute("sso", false);
		verify(request).setAttribute("ssoLogout", "x");
	}

	@Test
	public void isUserInSessionExpectNull() {
		assertNull(manager.isUserInSession(10));
	}

	@Test
	public void isUserInSessionExpectMatch() {
		UserSession us1 = this.newUserSession("1");
		us1.getUser().setId(2);
		
		manager.add(us1);
		
		UserSession expected = manager.isUserInSession(2);
		
		assertNotNull(expected);
		assertEquals("1", expected.getSessionId());
	}

	@Test
	public void getUserSessionShouldAlwaysFind() {
		UserSession us1 = this.newUserSession("1");
		us1.getUser().setId(2);
		manager.add(us1);
		UserSession us2 = this.newUserSession("2");
		us2.getUser().setId(3);
		manager.add(us2);
		UserSession us3 = this.newUserSession("3");
		manager.add(us3);

		assertNotNull(manager.getUserSession("1"));
		assertNotNull(manager.getUserSession("2"));
		assertNotNull(manager.getUserSession("3"));
		assertNull(manager.getUserSession("4"));
	}

	@Test
	public void getLoggedSessions() {
		UserSession us1 = this.newUserSession("1");
		us1.getUser().setId(2);
		manager.add(us1);
		UserSession us2 = this.newUserSession("2");
		us2.getUser().setId(3);
		manager.add(us2);
		UserSession us3 = this.newUserSession("3");
		manager.add(us3);

		Collection<UserSession> sessions = manager.getLoggedSessions();
		
		assertTrue(sessions.contains(us1));
		assertTrue(sessions.contains(us2));
		assertFalse(sessions.contains(us3));
	}

	@Test
	public void getAllSessions() {
		UserSession us1 = this.newUserSession("1");
		manager.add(us1);
		UserSession us2 = this.newUserSession("2");
		manager.add(us2);

		List<UserSession> sessions = manager.getAllSessions();
		
		assertEquals(2, sessions.size());
		assertTrue(sessions.contains(us1));
		assertTrue(sessions.contains(us2));
	}

	@Test
	public void getTotalAnonymousUsers() {
		manager.add(this.newUserSession("1"));
		manager.add(this.newUserSession("2"));
		manager.add(this.newUserSession("3"));

		assertEquals(3, manager.getTotalAnonymousUsers());
		assertEquals(0, manager.getTotalLoggedUsers());
	}

	@Test
	public void getTotalLoggedUsers() {
		UserSession us1 = this.newUserSession("1");
		us1.getUser().setId(3);
		manager.add(us1);
		UserSession us2 = this.newUserSession("2");
		us2.getUser().setId(4);
		
		manager.add(us2);

		assertEquals(2, manager.getTotalLoggedUsers());
		assertEquals(0, manager.getTotalAnonymousUsers());
	}

	@Test
	public void getTotalUsers() {
		manager.add(this.newUserSession("1"));
		manager.add(this.newUserSession("2"));
		UserSession us3 = this.newUserSession("3");
		us3.getUser().setId(3);
		manager.add(us3);

		assertEquals(2, manager.getTotalAnonymousUsers());
		assertEquals(1, manager.getTotalLoggedUsers());
		assertEquals(3, manager.getTotalUsers());
	}

	@Test
	public void removeAnonymousUser() {
		UserSession us = this.newUserSession("1");
		manager.add(us);

		manager.remove(us.getSessionId());
		
		assertEquals(0, manager.getTotalAnonymousUsers());
		assertEquals(0, manager.getTotalLoggedUsers());
	}

	@Test
	public void removeLoggedUser() {
		UserSession us = this.newUserSession("1");
		us.getUser().setId(2);
		manager.add(us);
		assertEquals(1, manager.getTotalLoggedUsers());

		when(httpSession.getAttribute(ConfigKeys.LOGGED)).thenReturn("1");
		
		manager.remove(us.getSessionId());
		
		assertEquals(0, manager.getTotalLoggedUsers());
		assertEquals(0, manager.getTotalAnonymousUsers());
		
	}

	@Test
	public void addDuplicatedSessionIdShouldReplace() {
		// First session
		UserSession session1 = this.newUserSession("1");
		session1.getUser().setUsername("user1");
		
		manager.add(session1);
		
		assertEquals(1, manager.getTotalAnonymousUsers());
		assertEquals("user1", manager.getUserSession("1").getUser().getUsername());

		// Duplicated session
		UserSession session2 = this.newUserSession("1");
		session2.getUser().setUsername("user2");
		
		manager.add(session2);
		
		assertEquals(1, manager.getTotalAnonymousUsers());
		assertEquals("user2", manager.getUserSession("1").getUser().getUsername());
	}

	@Test
	public void addModeratorShouldIncrementTotalModeratorsOnline() {
		UserSession us = this.newUserSession("1");
		Group g = new Group();
		Role role = new Role(); role.setName(SecurityConstants.MODERATOR);
		g.addRole(role);
		us.getUser().addGroup(g);
		us.getUser().setId(2);

		assertFalse(manager.isModeratorOnline());

		manager.add(us);

		assertTrue(manager.isModeratorOnline());
	}

	@Test
	public void removeModeratorShouldDecrementModeratorsOnline() {
		UserSession us = this.newUserSession("1");
		Group g = new Group();
		Role role = new Role(); role.setName(SecurityConstants.MODERATOR);
		g.addRole(role);
		us.getUser().addGroup(g);
		us.getUser().setId(2);

		RoleManager roleManager = new RoleManager(); roleManager.setGroups(Arrays.asList(g));
		us.setRoleManager(roleManager);

		manager.add(us);
		assertTrue(manager.isModeratorOnline());

		manager.remove(us.getSessionId());
		assertFalse(manager.isModeratorOnline());
	}

	@Test
	public void addBotShouldIgnore() {
		UserSession us = mock(UserSession.class);
		when(us.isBot()).thenReturn(true);
		when(us.getSessionId()).thenReturn("123");
		
		assertEquals(0, manager.getTotalUsers());
		manager.add(us);
		assertEquals(0, manager.getTotalUsers());
	}

	@Test
	public void addLoggedUser() {
		UserSession us = this.newUserSession("1");
		us.getUser().setId(2);
		manager.add(us);
		assertEquals(1, manager.getTotalLoggedUsers());
	}

	@Test
	public void addAnonymousUser() {
		UserSession us = this.newUserSession("1");
		manager.add(us);
		assertEquals(1, manager.getTotalAnonymousUsers());
	}

	@Test(expected = ForumException.class)
	public void addUsingEmptySessionIdExpectException() {
		manager.add(this.newUserSession(""));
	}

	@Test(expected = ForumException.class)
	public void addUsingNullSessionIdExpectException() {
		manager.add(this.newUserSession(null));
	}

	private void commonAutoLoginMockAction() {
		when(httpSession.getId()).thenReturn("123");
		when(config.getBoolean(ConfigKeys.AUTO_LOGIN_ENABLED)).thenReturn(true);
	}

	private UserSession newUserSession(String sessionId) {
		UserSession us = new UserSession();

		us.setSessionId(sessionId);
		us.getUser().setId(1);

		return us;
	}
}
