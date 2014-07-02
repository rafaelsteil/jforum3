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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;
/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTestCase {
	@Mock private UserRepository userRepository;
	@Mock private UserSession userSession;
	@Mock private UserService userService;
	@Mock private SessionManager sessionManager;
	@Mock private JForumConfig config;
	@Mock private RoleManager roleManager;
	@Mock private LostPasswordService lostPasswordService;
	@Mock private AvatarService avatarService;
	@Mock private RankingRepository rankingRepository;
	@Spy private MockResult mockResult;
	@Mock private HttpServletRequest mockRequest;
	
	@Mock private ForumController mockForumController;
	@Mock private MessageController mockMessageController;
	
	@Mock private UserController mockForwardControler;
	@Mock private UserController mockRedirectController;
	
	@InjectMocks private UserController userController;
	
	private User user = new User();
	private List<User> userList = new ArrayList<User>();
	
	@Before 
	public void setup() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(mockResult.forwardTo(userController)).thenReturn(mockForwardControler);
		when(mockResult.redirectTo(userController)).thenReturn(mockRedirectController);
	}
	
	@Test
	public void edit() {
		when(userRepository.get(1)).thenReturn(user);
		
		userController.edit(1);

		assertEquals(user, mockResult.included("user"));
	}

	@Test
	public void editSave() {
		user.setId(1);

		userController.editSave(user,null, null, null);

		verify(userService).update(user, false);
	}

	@Test
	public void recoverPassword() {
		userController.recoverPassword("123");
		
		assertEquals("123", mockResult.included("hash"));
	}

	@Test
	public void recoverPasswordValidateUsingBadDataExpectFail() {
		userController.recoverPasswordValidate("hash", "user", "123");

		assertEquals(true, mockResult.included("error"));
		assertEquals("PasswordRecovery.invalidData", mockResult.included("message"));
	}

	@Test
	public void recoverPasswordValidateUsingGoodDataExpectSuccess() {
		when(userRepository.validateLostPasswordHash("user", "hash")).thenReturn(user);
		
		userController.recoverPasswordValidate("hash", "user", "123");

		assertEquals("PasswordRecovery.ok", mockResult.included("message"));
	}

	@Test
	public void lostPasswordSend() {
		when(lostPasswordService.send("username", "email")).thenReturn(true);
		
		userController.lostPasswordSend("username", "email");

		assertEquals(true, mockResult.included("success"));
	}

	@Test
	public void loginWithReferer() {
		when(config.getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER)).thenReturn(false);
		when(mockRequest.getHeader("Referer")).thenReturn("some referer");
		
		userController.login(null, false);

		assertEquals("some referer", mockResult.included("returnPath"));
	}

	@Test
	public void loginWithReturnPath() {
		userController.login("some return path", false);

		assertEquals("some return path", mockResult.included("returnPath"));
	}

	@Test
	public void loginWithoutReturnPathAndIgnoringReferer() {
		when(config.getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER)).thenReturn(true);
		when(mockRequest.getHeader("Referer")).thenReturn("some referer");
		
		userController.login(null, false);
		
		assertNull(mockResult.included("returnPath"));
	}

	@Test
	public void editShouldHaveEditUserRule() throws Exception {
		Method method = userController.getClass().getMethod("edit", int.class);
		
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(EditUserRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void editSaveShouldHaveEditUserRule() throws Exception {
		Method method = userController.getClass().getMethod("editSave", User.class, Integer.class,
			UploadedFile.class, Integer.class);
		
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(EditUserRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void listUsingListingIsDisabledShouldForceEmptyList() {
		when(roleManager.isUserListingEnabled()).thenReturn(false);

		userController.list(0);
	
		assertEquals(userList, mockResult.included("users"));
	}

	@Test
	public void listCanInteractWithOtherGroups() {
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(true);
		when(roleManager.isUserListingEnabled()).thenReturn(true);
		
		userController.list(0);
		
		verify(userRepository).getAllUsers(0, 0);
	}

	@Test
	public void listCannotInteractWithOtherGroups() {
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(false);
		when(roleManager.isUserListingEnabled()).thenReturn(true);
		when(userSession.getUser()).thenReturn(user);
		
		userController.list(0);
		
		verify(userRepository, never()).getAllUsers(0, 0);
	}

	@Test
	public void logout() {
		when(config.getInt(ConfigKeys.ANONYMOUS_USER_ID)).thenReturn(1);
		when(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN)).thenReturn("x");
		when(config.getValue(ConfigKeys.COOKIE_USER_HASH)).thenReturn("y");
		
		userController.logout();
		
		verify(userSession).becomeAnonymous(1);
		verify(userSession).removeCookie("x");
		verify(userSession).removeCookie("y");
	}

	@Test
	public void authenticateUserUsingInvalidCredentialsExpectsInvalidLogin() {		
		when(userService.validateLogin("user", "passwd")).thenReturn(null);

		userController.authenticateUser("user", "passwd", false, null);
	
		verify(mockRedirectController).login(anyString(), anyBoolean());
	}

	@Test
	public void authenticateUserUsingGoodCredentialsAndAutoLoginEnabledExpectsSuccess() {
		user.setId(26);
		when(userService.validateLogin("user", "passwd")).thenReturn(user);
		when(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN)).thenReturn("x");
		when(config.getValue(ConfigKeys.COOKIE_USER_HASH)).thenReturn("y");
		when(config.getValue(ConfigKeys.COOKIE_USER_ID)).thenReturn("z");
		when(userService.generateAutoLoginSecurityHash(26)).thenReturn("456");
		when(userService.generateAutoLoginUserHash("456")).thenReturn("789");
		
		userController.authenticateUser("user", "passwd", true, null);

		verify(userSession).becomeLogged();
		verify(userSession).addCookie("x", "1");
		verify(userSession).addCookie("y", "789");
		verify(userSession).addCookie("z", "26");
		Assert.assertEquals("456", user.getSecurityHash());
	}

	@Test
	public void authenticateUserUsingGoodCredentialsWithoutAutoLoginExpectsSuccess() {
		when(userService.validateLogin("user", "passwd")).thenReturn(user);
		
		userController.authenticateUser("user", "passwd", false, null);

		verify(userSession).becomeLogged();
		verify(userSession, never()).addCookie(anyString(), anyString());
	}

	@Test
	public void authenticateUserWithReturnPath() {
		when(userService.validateLogin("user1", "pass1")).thenReturn(user);
		
		userController.authenticateUser("user1", "pass1", false, "return path");
	
		verify(mockResult).redirectTo("return path");
	}

	@Test
	public void registrationCompletedWithAnonymousUserExpectRedirect() {
		when(userSession.isLogged()).thenReturn(false);
		
		userController.registrationCompleted();
		
		verify(mockRedirectController).insert();
	}

	@Test
	public void registrationCompletedWithValidUserExpectsPropertyBagWithUser() {
		when(userSession.isLogged()).thenReturn(true);
		when(userSession.getUser()).thenReturn(user);
		
		userController.registrationCompleted();
		
		assertEquals(user, mockResult.included("user"));
	}

	@Test
	public void insertSaveUsernameTooBig() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(1);
		user.setUsername("username1");
		
		userController.insertSave(user);
		
		assertEquals("User.usernameTooBig", mockResult.included("error"));
		verify(mockForwardControler).insert();
	}

	@Test
	public void insertSaveUsernameContainsInvalidChars() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(20);
		user.setUsername("<username");
		
		userController.insertSave(user);
		
		assertEquals("User.usernameInvalidChars", mockResult.included("error"));
		verify(mockForwardControler).insert();
	}

	@Test
	public void insertSaveUsernameContainsInvalidChars2() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(20);
		user.setUsername(">username");
		
		userController.insertSave(user);
		
		assertEquals("User.usernameInvalidChars", mockResult.included("error"));
		verify(mockForwardControler).insert();
	}

	@Test
	public void insertSaveUsernameNotAvailable() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(20);
		when(userRepository.isUsernameAvailable("username", null)).thenReturn(false);
		user.setUsername("username");
		
		userController.insertSave(user);
		
		assertEquals("User.usernameNotAvailable", mockResult.included("error"));
		verify(mockForwardControler).insert();
	}

	@Test
	public void insertSaveUser() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(20);
		when(userRepository.isUsernameAvailable("username", null)).thenReturn(true);
		user.setUsername("username");
		
		userController.insertSave(user);
		
		verify(userService).add(user);
		verify(mockRedirectController).registrationCompleted();
	}

	@Test
	public void insertLoginUser() {
		when(config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)).thenReturn(20);
		when(userRepository.isUsernameAvailable("username", null)).thenReturn(true);
		user.setUsername("username");
		
		userController.insertSave(user);
		
		verify(userSession).becomeLogged();
	}

	@Test
	public void profileHasReadAccessRightsShouldAllowViewProfile() {
		when(roleManager.getCanViewProfile()).thenReturn(true);
		when(userSession.getUser()).thenReturn(user);
		
		userController.profile(1);
		
		verify(userRepository).get(1);
	}

	@Test
	public void profileHasWriteAccessRightsShouldAllowEditProfile() {
		when(roleManager.getCanViewProfile()).thenReturn(true);
		when(roleManager.getCanEditUser(any(User.class), anyListOf(Group.class))).thenReturn(true);
		when(userSession.getUser()).thenReturn(user);
		
		userController.profile(1);
		
		assertEquals(true, mockResult.included("canEdit"));
	}

	@Test
	public void profileDoesNotHaveReadAccessRightShouldDenyViewProfile() {
		when(roleManager.getCanViewProfile()).thenReturn(false);
		when(mockResult.redirectTo(MessageController.class)).thenReturn(mockMessageController);
		
		userController.profile(1);
		
		verify(mockMessageController).accessDenied();
	}

	@Test
	public void profileDoesNotHaveWriteAccessRightShouldDenyEditProfile() {
		when(roleManager.getCanViewProfile()).thenReturn(true);
		when(roleManager.getCanEditUser(any(User.class), anyListOf(Group.class))).thenReturn(false);
		when(userSession.getUser()).thenReturn(user);
		
		userController.profile(1);
		
		assertEquals(false, mockResult.included("canEdit"));
	}
}
