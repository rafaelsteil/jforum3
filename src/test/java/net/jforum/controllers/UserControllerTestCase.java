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
package net.jforum.controllers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Avatar;
import net.jforum.entities.Group;
import net.jforum.entities.Ranking;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.EditUserRule;
import net.jforum.security.RoleManager;
import net.jforum.services.AvatarService;
import net.jforum.services.LostPasswordService;
import net.jforum.services.UserService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class UserControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserRepository userRepository = context.mock(UserRepository.class);
	private UserSession userSession = context.mock(UserSession.class);
	private UserService userService = context.mock(UserService.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private LostPasswordService lostPasswordService = context.mock(LostPasswordService.class);
	private AvatarService avatarService = context.mock(AvatarService.class);
	private User user = new User();
	private RankingRepository rankingRepository = context.mock(RankingRepository.class);
	private Result mockResult = context.mock(MockResult.class);
	private HttpServletRequest mockRequest = context.mock(HttpServletRequest.class);
	private UserController mockUserController = context.mock(UserController.class);
	private ForumController mockForumController = context.mock(ForumController.class);
	private UserController userController = new UserController(userRepository, userSession, userService, sessionManager,
		config, lostPasswordService, avatarService, rankingRepository, mockResult, mockRequest);

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			User user = new User();
			one(userRepository).get(1); will(returnValue(user));
			one(rankingRepository).getAllRankings(); will(returnValue(new ArrayList<Ranking>()));
			one(avatarService).getAvatarGallery(); will(returnValue(new ArrayList<Avatar>()));
			one(mockResult).include("user", user);
			one(mockResult).include("rankings", new ArrayList<Ranking>());
			one(mockResult).include("avatars", new ArrayList<Avatar>());
		}});

		userController.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final User user = new User();
		user.setId(1);

		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(roleManager).isCoAdministrator(); will(returnValue(false));
			one(userService).update(user, false);
			one(config).getValue(ConfigKeys.AUTHENTICATION_TYPE);
			one(mockResult).redirectTo(userController);
			will(returnValue(mockUserController));
			one(mockUserController).edit(user.getId());
		}});

		userController.editSave(user,null, null, null);
		context.assertIsSatisfied();
	}

	@Test
	public void recoverPassword() {
		context.checking(new Expectations() {{
			one(mockResult).include("hash", "123");
		}});

		userController.recoverPassword("123");
		context.assertIsSatisfied();
	}

	@Test
	public void recoverPasswordValidateUsingBadDataExpectFail() {
		context.checking(new Expectations() {{
			one(userRepository).validateLostPasswordHash("user", "hash"); will(returnValue(null));
			one(mockResult).include("error", true);
			one(mockResult).include("message", "PasswordRecovery.invalidData");
		}});

		userController.recoverPasswordValidate("hash", "user", "123");
		context.assertIsSatisfied();
	}

	@Test
	public void recoverPasswordValidateUsingGoodDataExpectSuccess() {
		context.checking(new Expectations() {{
			one(userRepository).validateLostPasswordHash("user", "hash"); will(returnValue(new User()));
			one(mockResult).include("message", "PasswordRecovery.ok");
		}});

		userController.recoverPasswordValidate("hash", "user", "123");
		context.assertIsSatisfied();
	}

	@Test
	public void lostPasswordSend() {
		context.checking(new Expectations() {{
			one(lostPasswordService).send("username", "email"); will(returnValue(true));
			one(mockResult).include("success", true);
		}});

		userController.lostPasswordSend("username", "email");
		context.assertIsSatisfied();
	}

	@Test
	public void loginWithReferer() {
		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER); will(returnValue(false));
			one(mockResult).include("returnPath", "some referer");
		}});

		userController.login(null);
		context.assertIsSatisfied();
	}

	@Test
	public void loginWithReturnPath() {
		context.checking(new Expectations() {{
			one(mockResult).include("returnPath", "some return path");
		}});

		userController.login("some return path");
		context.assertIsSatisfied();
	}

	@Test
	public void loginWithoutReturnPathAndIgnoringReferer() {
		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER); will(returnValue(true));
		}});

		userController.login(null);
		context.assertIsSatisfied();
	}

	@Test
	public void editShouldHaveEditUserRule() throws Exception {
		Method method = userController.getClass().getMethod("edit", int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(EditUserRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void editSaveShouldHaveEditUserRule() throws Exception {
		Method method = userController.getClass().getMethod("editSave", User.class, Integer.class,
			UploadedFile.class, Integer.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(EditUserRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void listUsingListingIsDisabledShouldForceEmptyList() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isUserListingEnabled(); will(returnValue(false));
			one(mockResult).include("users", new ArrayList<User>());
		}});

		userController.list(0);
		context.assertIsSatisfied();
	}

	@Test
	public void listCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));

			one(roleManager).isUserListingEnabled(); will(returnValue(true));
			one(userRepository).getTotalUsers(); will(returnValue(100));
			one(config).getInt(ConfigKeys.USERS_PER_PAGE); will(returnValue(10));
			one(userRepository).getAllUsers(0, 10); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("pagination", new Pagination(0, 0, 0, "", 0));
		}});

		userController.list(0);
		context.assertIsSatisfied();
	}

	@Test
	public void listCannotInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User user = new User();
			one(userSession).getUser(); will(returnValue(user));

			one(roleManager).isUserListingEnabled(); will(returnValue(true));
			one(userRepository).getTotalUsers(); will(returnValue(100));
			one(config).getInt(ConfigKeys.USERS_PER_PAGE); will(returnValue(10));
			one(userRepository).getAllUsers(0, 10, user.getGroups()); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("pagination", new Pagination(0, 0, 0, "", 0));
		}});

		userController.list(0);
		context.assertIsSatisfied();
	}

	@Test
	public void logout() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.ANONYMOUS_USER_ID); will(returnValue(1));
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).becomeAnonymous(1);
			allowing(userSession).getSessionId(); will(returnValue("123"));
			one(sessionManager).storeSession("123");
			one(sessionManager).remove("123");
			one(sessionManager).add(with(aNonNull(UserSession.class)));
			one(config).getValue(ConfigKeys.COOKIE_AUTO_LOGIN); will(returnValue("x"));
			one(config).getValue(ConfigKeys.COOKIE_USER_HASH); will(returnValue("y"));
			one(userSession).removeCookie("x");
			one(userSession).removeCookie("y");
			
			one(mockResult).redirectTo(ForumController.class);
			will(returnValue(mockForumController));
			one(mockForumController).list();
		}});

		userController.logout();
		context.assertIsSatisfied();
	}

	@Test
	public void authenticateUserUsingInvalidCredentialsExpectsInvalidLogin() {
		context.checking(new Expectations() {{
			one(userService).validateLogin("user", "passwd"); will(returnValue(null));
			one(mockResult).include("invalidLogin", true);
			one(mockResult).forwardTo(Actions.LOGIN);
		}});

		userController.authenticateUser("user", "passwd", false, null);
		context.assertIsSatisfied();
	}

	@Test
	public void authenticateUserUsingGoodCredentialsAndAutoLoginEnabledExpectsSuccess() {
		final User user = new User(); user.setId(1);

		context.checking(new Expectations() {{
			one(userService).validateLogin("user", "passwd"); will(returnValue(user));
			one(userSession).setUser(user);
			one(userSession).becomeLogged();
			one(userService).generateAutoLoginSecurityHash(user.getId()); will(returnValue("456"));
			one(userService).generateAutoLoginUserHash("456"); will(returnValue("789"));
			one(config).getValue(ConfigKeys.COOKIE_AUTO_LOGIN); will(returnValue("x"));
			one(config).getValue(ConfigKeys.COOKIE_USER_HASH); will(returnValue("y"));
			one(userSession).addCookie("x", "1");
			one(userSession).addCookie("y", "789");
			one(config).getValue(ConfigKeys.COOKIE_USER_ID); will(returnValue("z"));
			one(userSession).addCookie("z", Integer.toString(user.getId()));
			one(sessionManager).add(userSession);
			one(mockResult).redirectTo(ForumController.class);
			will(returnValue(mockForumController));
			one(mockForumController).list();
		}});

		userController.authenticateUser("user", "passwd", true, null);
		context.assertIsSatisfied();
		Assert.assertEquals("456", user.getSecurityHash());
	}

	@Test
	public void authenticateUserUsingGoodCredentialsWithoutAutoLoginExpectsSuccess() {
		final User user = new User(); user.setId(1);

		context.checking(new Expectations() {{
			one(userService).validateLogin("user", "passwd"); will(returnValue(user));
			one(userSession).setUser(user);
			one(userSession).becomeLogged();
			one(config).getValue(ConfigKeys.COOKIE_AUTO_LOGIN); will(returnValue("x"));
			one(config).getValue(ConfigKeys.COOKIE_USER_HASH); will(returnValue("y"));
			one(userSession).removeCookie("x");
			one(userSession).removeCookie("y");
			one(sessionManager).add(userSession);
			one(mockResult).redirectTo(ForumController.class);
			will(returnValue(mockForumController));
			one(mockForumController).list();
		}});

		userController.authenticateUser("user", "passwd", false, null);
		context.assertIsSatisfied();
	}

	@Test
	public void authenticateUserWithReturnPath() {
		context.checking(new Expectations() {{
			one(userService).validateLogin("user1", "pass1"); will(returnValue(new User()));
			ignoring(userSession); ignoring(config); ignoring(sessionManager);
			one(mockResult).redirectTo("return path");
		}});

		userController.authenticateUser("user1", "pass1", false, "return path");
		context.assertIsSatisfied();
	}

	@Test
	public void registrationCompletedWithAnonymousUserExpectRedirect() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(false));
			one(mockResult).redirectTo(userController);
			will(returnValue(mockUserController));
			one(mockUserController).insert();
		}});

		userController.registrationCompleted();
		context.assertIsSatisfied();
	}

	@Test
	public void registrationCompletedWithValidUserExpectsPropertyBagWithUser() {
		context.checking(new Expectations() {{
			one(userSession).isLogged(); will(returnValue(true));
			one(userSession).getUser(); will(returnValue(new User()));
			one(mockResult).include("user", new User());
		}});

		userController.registrationCompleted();
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void insertSaveUsernameTooBig() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.USERNAME_MAX_LENGTH); will(returnValue(1));
			one(mockResult).include("error", "User.usernameTooBig");
			one(mockResult).redirectTo(userController);
			will(returnValue(mockUserController));
			one(mockUserController).insert();
		}});

		userController.insertSave(new User() {{ setUsername("username1"); }});
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void insertSaveUsernameContainsInvalidChars() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.USERNAME_MAX_LENGTH); will(returnValue(20));
			one(mockResult).include("error", "User.usernameInvalidChars");
			one(mockResult).redirectTo(UserController.class).insert();
		}});

		userController.insertSave(new User() {{ setUsername("<username"); }});
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void insertSaveUsernameContainsInvalidChars2() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.USERNAME_MAX_LENGTH); will(returnValue(20));
			one(mockResult).include("error", "User.usernameInvalidChars");
			one(mockResult).redirectTo(UserController.class).insert();
		}});

		userController.insertSave(new User() {{ setUsername(">username"); }});
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void insertSaveUsernameNotAvailable() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.USERNAME_MAX_LENGTH); will(returnValue(20));
			one(userRepository).isUsernameAvailable("username", null); will(returnValue(false));
			one(mockResult).include("error", "User.usernameNotAvailable");
			one(mockResult).redirectTo(UserController.class).insert();
		}});

		userController.insertSave(new User() {{ setUsername("username"); }});
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void insertSave() {
		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.USERNAME_MAX_LENGTH); will(returnValue(20));
			one(userService).add(with(aNonNull(User.class)));
			one(userSession).setUser(with(aNonNull(User.class)));
			one(userRepository).isUsernameAvailable("username", null); will(returnValue(true));
			one(userSession).becomeLogged();
			one(sessionManager).add(userSession);
			one(mockResult).redirectTo(userController);
			will(returnValue(mockUserController));
			one(mockUserController).registrationCompleted();
		}});

		userController.insertSave(new User() {{ setId(1); setUsername("username"); }});

		context.assertIsSatisfied();
	}

	@Test
	public void profileHasAccessRightsShouldAllow() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager); will(returnValue(true));

			List<Ranking> rankings = new ArrayList<Ranking>();

			one(rankingRepository).getAllRankings(); will(returnValue(rankings));
			one(userRepository).get(1); will(returnValue(new User()));
			one(mockResult).include("user", new User());
			one(userRepository).getTotalTopics(1); will(returnValue(0));
			one(mockResult).include("userTotalTopics", 0);
			one(mockResult).include("rankings", rankings);
			one(config).getInt("anonymousUserId"); will(returnValue(0));
			one(mockResult).include("isAnonymousUser", false);
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(userSession).getUser(); will(returnValue(user));
			one(roleManager).getCanEditUser(user, new ArrayList<Group>()); will(returnValue(true));
			one(mockResult).include("canEdit", true);
		}});

		userController.profile(1);
		context.assertIsSatisfied();
	}

	@Test
	public void profileDoesNotHaveAccessRightShouldDeny() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager); will(returnValue(false));
			one(mockResult).redirectTo(MessageController.class).accessDenied();
		}});

		userController.profile(1);
		context.assertIsSatisfied();
	}
}
