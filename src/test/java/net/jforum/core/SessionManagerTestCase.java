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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;
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
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SessionManagerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserRepository userRepository = context.mock(UserRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private HttpServletResponse response = context.mock(HttpServletResponse.class);
	private HttpSession httpSession = context.mock(HttpSession.class);
	private SessionRepository sessionRepository = context.mock(SessionRepository.class);
	private SessionManager manager = new SessionManager(config, userRepository, sessionRepository);
	private States state = context.states("state");
	private States refreshState = context.states("refresh");

	@Test
	public void loginAfterTimeoutShouldFetchFromSessionRepositoryExpectLastVisitCorrect() {
		UserSession us = this.newUserSession("123"); us.getUser().setId(2);
		us.setCreationTime(1); us.setLastAccessedTime(2); us.setLastVisit(3);

		state.become("back");
		context.checking(new Expectations() {{
			Session session = new Session(); session.setLastVisit(new Date(7));
			one(sessionRepository).get(2); will(returnValue(session));
		}});

		manager.add(us);
		context.assertIsSatisfied();
		Assert.assertEquals(7, us.getLastVisit());
	}

	@Test
	public void loginBackBeforeExpireExpectLastVisitCorrect() {
		UserSession us = this.newUserSession("123"); us.getUser().setId(2);
		us.setCreationTime(1); us.setLastVisit(9);

		manager.add(us);
		Assert.assertEquals(1, manager.getTotalUsers());

		UserSession us2 = this.newUserSession("456"); us2.getUser().setId(2);
		us2.setCreationTime(1); us2.setLastVisit(5);

		manager.add(us2);
		context.assertIsSatisfied();

		Assert.assertEquals(9, us2.getLastVisit());
		Assert.assertEquals(1, manager.getTotalUsers());
	}

	@Test
	public void storeSessionNotRegisteredShouldIgnore() {
		context.checking(new Expectations() {{}});
		manager.storeSession("invalid");
	}

	@Test
	public void storeSessionExpectSuccess() {
		context.checking(new Expectations() {{
			allowing(httpSession).getId(); will(returnValue("123"));
			ignoring(request); ignoring(httpSession);
			one(sessionRepository).add(with(aNonNull(Session.class)));
		}});

		UserSession us = new UserSession(null);
		us.setSessionId("123");
		us.getUser().setId(2);

		manager.add(us);
		manager.storeSession("123");
		context.assertIsSatisfied();
	}

	@Test
	public void storeSessionIsAnonymousShouldIgnore() {
		context.checking(new Expectations() {{
			allowing(httpSession).getId(); will(returnValue("123"));
		}});

		UserSession us = new UserSession(null);
		us.setSessionId("123");
		us.getUser().setId(1);

		manager.add(us);
		manager.storeSession("123");
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginAllInformationIsGoodShouldAccept() {
		state.become("good-cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			User user = new User(); user.setId(2); user.setSecurityHash("123");
			one(userRepository).get(2); will(returnValue(user));
			one(httpSession).setAttribute(ConfigKeys.LOGGED, "1");
			one(request).setAttribute("sso", false);
			one(config).getValue("sso.logout");
			one(request).setAttribute("ssoLogout", "");
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginValidUserInvalidSecurityHashShouldDeny() {
		state.become("good-cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			User user = new User(); user.setId(2); user.setSecurityHash("abc");
			one(userRepository).get(2); will(returnValue(user));
			one(httpSession).getAttributeNames();
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginValidUserEmptySecurityHashShouldDeny() {
		state.become("good-cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			User user = new User(); user.setId(2); user.setSecurityHash(null);
			one(userRepository).get(2); will(returnValue(user));
			one(httpSession).getAttributeNames();
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginValidUserHasDeletedFlagShouldDeny() {
		state.become("good-cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			User user = new User(); user.setId(2); user.setDeleted(true);
			one(userRepository).get(2); will(returnValue(user));
			one(httpSession).getAttributeNames();
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginValidUserNotfoundInRepositoryShouldDeny() {
		state.become("good-cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			one(userRepository).get(2); will(returnValue(null));
			one(httpSession).getAttributeNames();
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginValidUserCookieValueNot1ShouldDeny() {
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			allowing(request).getCookies(); will(returnValue(new Cookie[] {
				new Cookie("cookieNameData", "2"),
				new Cookie("cookieUserHash", "a"),
				new Cookie("cookieAutoLogin", "0")
			}));
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginAnonymousUserShouldDeny() {
		state.become("cookies");
		refreshState.become("on");
		this.commonAutoLoginExpectations();

		context.checking(new Expectations() {{
			allowing(request).getCookies(); will(returnValue(new Cookie[] {
				new Cookie("cookieNameData", "1"),
				new Cookie("cookieUserHash", "a"),
				new Cookie("cookieAutoLogin", "1")
			}));
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void autoLoginDoestNotHaveCookiesShouldDeny() {
		this.commonAutoLoginExpectations();
		refreshState.become("on");

		context.checking(new Expectations() {{
			atLeast(1).of(request).getCookies(); will(returnValue(null));
			one(userRepository).get(1); will(returnValue(new User()));
		}});

		manager.refreshSession(request, response);
		context.assertIsSatisfied();
	}

	@Test
	public void refreshExistingSessionShouldFetchUserAndRoleManager() {
		refreshState.become("on");
		final User user = new User(); user.setId(1);
		UserSession us = this.newUserSession("123");
		us.setUser(user);

		context.checking(new Expectations() {{
			atLeast(1).of(httpSession).getId(); will(returnValue("123"));
			one(userRepository).get(user.getId()); will(returnValue(user));
		}});

		manager.add(us);
		manager.refreshSession(request, response);
		Assert.assertNotNull(us.getRoleManager());
	}

	@Test
	public void refreshSessionIsNewShouldCreateAutoLoginDisabledUsingAnonymousUser() {
		final User anonymousUser = new User(); anonymousUser.setId(1);
		refreshState.become("on");

		context.checking(new Expectations() {{
			atLeast(1).of(httpSession).getId(); will(returnValue("123"));
			one(httpSession).getAttributeNames();
			one(config).getBoolean(ConfigKeys.AUTO_LOGIN_ENABLED); will(returnValue(false));
			one(userRepository).get(1); will(returnValue(anonymousUser));
			one(request).setAttribute("sso", false);
			one(config).getValue(ConfigKeys.SSO_LOGOUT); will(returnValue("x"));
			one(request).setAttribute("ssoLogout", "x");
		}});

		UserSession us = manager.refreshSession(request, response);
		Assert.assertNotNull(us);
		Assert.assertEquals(anonymousUser, us.getUser());
		Assert.assertNotNull(us.getRoleManager());
		Assert.assertEquals("123", us.getSessionId());
		context.assertIsSatisfied();
	}

	@Test
	public void isUserInSessionExpectNull() {
		Assert.assertNull(manager.isUserInSession(10));
	}

	@Test
	public void isUserInSessionExpectMatch() {
		UserSession us1 = this.newUserSession("1"); us1.getUser().setId(2); manager.add(us1);
		UserSession expected = manager.isUserInSession(2);
		Assert.assertNotNull(expected);
		Assert.assertEquals("1", expected.getSessionId());
	}

	@Test
	public void getUserSessionShouldAlwaysFind() {
		UserSession us1 = this.newUserSession("1"); us1.getUser().setId(2); manager.add(us1);
		UserSession us2 = this.newUserSession("2"); us2.getUser().setId(3); manager.add(us2);
		UserSession us3 = this.newUserSession("3"); manager.add(us3);

		Assert.assertNotNull(manager.getUserSession("1"));
		Assert.assertNotNull(manager.getUserSession("2"));
		Assert.assertNotNull(manager.getUserSession("3"));
		Assert.assertNull(manager.getUserSession("4"));
	}

	@Test
	public void getLoggedSessions() {
		UserSession us1 = this.newUserSession("1"); us1.getUser().setId(2); manager.add(us1);
		UserSession us2 = this.newUserSession("2"); us2.getUser().setId(3); manager.add(us2);
		UserSession us3 = this.newUserSession("3"); manager.add(us3);

		Collection<UserSession> sessions = manager.getLoggedSessions();
		Assert.assertTrue(sessions.contains(us1));
		Assert.assertTrue(sessions.contains(us2));
		Assert.assertFalse(sessions.contains(us3));
	}

	@Test
	public void getAllSessions() {
		UserSession us1 = this.newUserSession("1"); manager.add(us1);
		UserSession us2 = this.newUserSession("2"); manager.add(us2);

		List<UserSession> sessions = manager.getAllSessions();
		Assert.assertTrue(sessions.contains(us1));
		Assert.assertTrue(sessions.contains(us2));
	}

	@Test
	public void getTotalAnonymousUsers() {
		manager.add(this.newUserSession("1"));
		manager.add(this.newUserSession("2"));
		manager.add(this.newUserSession("3"));

		Assert.assertEquals(3, manager.getTotalAnonymousUsers());
		Assert.assertEquals(0, manager.getTotalLoggedUsers());
	}

	@Test
	public void getTotalLoggedUsers() {
		UserSession us1 = this.newUserSession("1"); us1.getUser().setId(3); manager.add(us1);
		UserSession us2 = this.newUserSession("2"); us2.getUser().setId(4); manager.add(us2);

		Assert.assertEquals(2, manager.getTotalLoggedUsers());
		Assert.assertEquals(0, manager.getTotalAnonymousUsers());
	}

	@Test
	public void getTotalUsers() {
		manager.add(this.newUserSession("1"));
		manager.add(this.newUserSession("2"));
		UserSession us3 = this.newUserSession("3"); us3.getUser().setId(3); manager.add(us3);

		Assert.assertEquals(2, manager.getTotalAnonymousUsers());
		Assert.assertEquals(1, manager.getTotalLoggedUsers());
		Assert.assertEquals(3, manager.getTotalUsers());
	}

	@Test
	public void removeAnonymousUser() {
		UserSession us = this.newUserSession("1");
		manager.add(us);

		manager.remove(us.getSessionId());
		Assert.assertEquals(0, manager.getTotalAnonymousUsers());
		Assert.assertEquals(0, manager.getTotalLoggedUsers());
		context.assertIsSatisfied();
	}

	@Test
	public void removeLoggedUser() {
		UserSession us = this.newUserSession("1");
		us.getUser().setId(2);
		manager.add(us);
		Assert.assertEquals(1, manager.getTotalLoggedUsers());

		state.become("logged");

		manager.remove(us.getSessionId());
		Assert.assertEquals(0, manager.getTotalLoggedUsers());
		Assert.assertEquals(0, manager.getTotalAnonymousUsers());
		context.assertIsSatisfied();
	}

	@Test
	public void addDuplicatedSessionIdShouldReplace() {
		// First session
		UserSession session1 = this.newUserSession("1");
		session1.getUser().setUsername("user1");
		manager.add(session1);
		Assert.assertEquals(1, manager.getTotalAnonymousUsers());
		Assert.assertEquals("user1", manager.getUserSession("1").getUser().getUsername());

		// Duplicated session
		UserSession session2 = this.newUserSession("1");
		session2.getUser().setUsername("user2");
		manager.add(session2);
		Assert.assertEquals(1, manager.getTotalAnonymousUsers());
		Assert.assertEquals("user2", manager.getUserSession("1").getUser().getUsername());
	}

	@Test
	public void addModeratorShouldIncrementTotalModeratorsOnline() {
		UserSession us = this.newUserSession("1");
		Group g = new Group();
		Role role = new Role(); role.setName(SecurityConstants.MODERATOR);
		g.addRole(role);
		us.getUser().addGroup(g);
		us.getUser().setId(2);

		Assert.assertFalse(manager.isModeratorOnline());

		manager.add(us);

		Assert.assertTrue(manager.isModeratorOnline());
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
		Assert.assertTrue(manager.isModeratorOnline());

		manager.remove(us.getSessionId());
		Assert.assertFalse(manager.isModeratorOnline());
	}

	@Test
	public void addBotShouldIgnore() {
		UserSession us = new UserSession(null) {
			@Override
			public boolean isBot() { return true; }

			@Override
			public String getSessionId() { return "123"; }
		};

		Assert.assertEquals(0, manager.getTotalUsers());
		manager.add(us);
		Assert.assertEquals(0, manager.getTotalUsers());
	}

	@Test
	public void addLoggedUser() {
		UserSession us = this.newUserSession("1");
		us.getUser().setId(2);
		manager.add(us);
		Assert.assertEquals(1, manager.getTotalLoggedUsers());
	}

	@Test
	public void addAnonymousUser() {
		UserSession us = this.newUserSession("1");
		manager.add(us);
		Assert.assertEquals(1, manager.getTotalAnonymousUsers());
	}

	@Test(expected = ForumException.class)
	public void addUsingEmptySessionIdExpectException() {
		manager.add(this.newUserSession(""));
	}

	@Test(expected = ForumException.class)
	public void addUsingNullSessionIdExpectException() {
		manager.add(this.newUserSession(null));
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(config).getInt(ConfigKeys.ANONYMOUS_USER_ID); will(returnValue(1));
			allowing(config).getValue(ConfigKeys.ANONYMOUS_USER_ID); will(returnValue("1"));
			allowing(config).getValue(ConfigKeys.COOKIE_USER_ID); will(returnValue("cookieNameData"));
			allowing(config).getValue(ConfigKeys.COOKIE_USER_HASH); will(returnValue("cookieUserHash"));
			allowing(config).getValue(ConfigKeys.COOKIE_AUTO_LOGIN); will(returnValue("cookieAutoLogin"));
			allowing(config).getValue(ConfigKeys.AUTHENTICATION_TYPE); will(returnValue("x"));
			allowing(request).getSession(); will(returnValue(httpSession));
			allowing(httpSession).getAttribute(ConfigKeys.LOGGED); will(returnValue("1")); when(state.is("logged"));
			allowing(httpSession).getAttribute(ConfigKeys.LOGGED); when(state.isNot("logged"));
			allowing(sessionRepository).get(with(any(int.class))); will(returnValue(null)); when(state.isNot("back"));
		}});
	}

	private void commonAutoLoginExpectations() {
		context.checking(new Expectations() {{
			one(httpSession).getAttributeNames();
			atLeast(1).of(httpSession).getId(); will(returnValue("123"));
			one(config).getBoolean(ConfigKeys.AUTO_LOGIN_ENABLED); will(returnValue(true));

			allowing(request).getCookies(); will(returnValue(new Cookie[] {
				new Cookie("cookieNameData", "2"),
				new Cookie("cookieUserHash", MD5.hash("123")),
				new Cookie("cookieAutoLogin", "1")
			})); when(state.is("good-cookies"));
		}});
	}

	private UserSession newUserSession(String sessionId) {
		UserSession us = new UserSession(null);

		us.setSessionId(sessionId);
		us.getUser().setId(1);

		return us;
	}
}
