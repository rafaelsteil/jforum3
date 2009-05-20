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
package net.jforum.actions;

import java.util.ArrayList;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ExternalUserManagementInterceptor;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
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
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.interceptor.MultipartRequestInterceptor;
import org.vraptor.interceptor.UploadedFileInformation;
import org.vraptor.plugin.interceptor.MethodInterceptorInterceptor;

/**
 * @author Rafael Steil
 */
@Component(Domain.USER)
@InterceptedBy( {MethodInterceptorInterceptor.class,MultipartRequestInterceptor.class, MethodSecurityInterceptor.class })
public class UserActions {
	private UserRepository userRepository;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private UserService userService;
	private UserSession userSession;
	private SessionManager sessionManager;
	private LostPasswordService lostPasswordService;
	private JForumConfig config;
	private AvatarService avatarService;
	private RankingRepository rankingRepository;

	public UserActions(UserRepository userRepository, ViewPropertyBag propertyBag,
		ViewService viewService, UserSession userSession, UserService userService,
		SessionManager sessionFacade, JForumConfig config, LostPasswordService lostPasswordService,
		AvatarService avatarService, RankingRepository rankingRepository) {
		this.userRepository = userRepository;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
		this.userService = userService;
		sessionManager = sessionFacade;
		this.userSession = userSession;
		this.config = config;
		this.lostPasswordService = lostPasswordService;
		this.avatarService = avatarService;
		this.rankingRepository = rankingRepository;
	}

	/**
	 * Shows the page with all registered users
	 * @param page the pagination first record to start showing
	 */
	public void list(@Parameter(key = "page") int page) {
		RoleManager roleManager = sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isUserListingEnabled()) {
			propertyBag.put("users", new ArrayList<User>());
		}
		else {
			Pagination pagination = new Pagination(config, page)
				.forUsers(userRepository.getTotalUsers());

			if (roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
				propertyBag.put("users", userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage()));
			}
			else {
				User currentUser = sessionManager.getUserSession().getUser();
				propertyBag.put("users", userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage(), currentUser.getGroups()));
			}

			propertyBag.put("pagination", pagination);
		}
	}

	/**
	 * Logout an authenticated user
	 */
	public void logout() {
		UserSession us = sessionManager.getUserSession();
		sessionManager.storeSession(us.getSessionId());

		us.becomeAnonymous(config.getInt(ConfigKeys.ANONYMOUS_USER_ID));

		sessionManager.remove(us.getSessionId());
		sessionManager.add(us);
		this.removeAutoLoginCookies(us);

		viewService.redirectToAction(Domain.FORUMS, Actions.LIST);
	}

	/**
	 * Shows the form to log in
	 */
	public void login(@Parameter(key = "returnPath") String returnPath) {
		if (StringUtils.isEmpty(returnPath) && !config.getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER)) {
			returnPath = viewService.getReferer();
		}

		if (!StringUtils.isEmpty(returnPath)) {
			propertyBag.put("returnPath", returnPath);
		}
	}

	/**
	 * Called from {@link #login()}, to validate the user credentials
	 * @param username the username
	 * @param password the password
	 * @param autoLogin autoLogin
	 */
	public void authenticateUser(@Parameter(key = "username") String username,
		@Parameter(key = "password") String password, @Parameter(key = "autoLogin") boolean autoLogin,
		@Parameter(key = "returnPath") String returnPath) {
		User user = userService.validateLogin(username, password);

		if (user == null) {
			propertyBag.put("invalidLogin", true);
			viewService.renderView(Actions.LOGIN);
		}
		else {
			userSession.setUser(user);
			userSession.becomeLogged();

			if (autoLogin) {
				this.activateAutoLogin(user);
			}
			else {
				this.removeAutoLoginCookies(userSession);
			}

			sessionManager.add(userSession);

			if (!StringUtils.isEmpty(returnPath)) {
				viewService.redirect(returnPath);
			}
			else {
				viewService.redirectToAction(Domain.FORUMS, Actions.LIST);
			}
		}
	}

	/**
	 * Shows the page to edit the user profile
	 * @param userId the user id
	 */
	@SecurityConstraint(EditUserRule.class)
	public void edit(@Parameter(key = "userId") int userId) {
		propertyBag.put("user", userRepository.get(userId));
		propertyBag.put("rankings", rankingRepository.getAllRankings());
		propertyBag.put("avatars", avatarService.getGalleryAvatar());
	}

	/**
	 * Updates an existing user
	 * @param user the user to update
	 */
	@SecurityConstraint(EditUserRule.class)
	public void editSave(@Parameter(key = "user") User user, @Parameter(key = "avatarId") Integer avatarId,
			@Parameter(key = "uploadfile") UploadedFileInformation image, @Parameter(key = "rankingId") Integer rankingId) {

		Avatar avatar = null;

		if (avatarId != null){
			avatar = new Avatar();
			avatar.setId(avatarId);
			avatar.setAvatarType(AvatarType.AVATAR_GALLERY);
		}
		else if (image != null) {
			avatar = new Avatar();
			avatar.setAvatarType(AvatarType.AVATAR_UPLOAD);
			avatarService.add(avatar, image);
		}

		user.setAvatar(avatar);

		if (rankingId == null) {
			user.setRanking(null);
		}
		else {
			Ranking ranking = new Ranking();
			ranking.setId(rankingId);
			user.setRanking(ranking);
		}

		RoleManager roleManager = userSession.getRoleManager();
		boolean canChangeUserName = roleManager.isAdministrator() || roleManager.isCoAdministrator();

		boolean isSSOAuthentication = ConfigKeys.TYPE_SSO.equals(config.getValue(ConfigKeys.AUTHENTICATION_TYPE));
		canChangeUserName = canChangeUserName && !isSSOAuthentication;

		userService.update(user, canChangeUserName);
		viewService.redirectToAction(Actions.EDIT, user.getId());
	}

	/**
	 * Shows the page to create a new user
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void insert() {

	}

	/**
	 * Adds a new user
	 * @param user the user to add
	 */
	@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void insertSave(@Parameter(key = "user") User user) {
		boolean error = false;

		if (!error && user.getUsername().length() > config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)) {
			propertyBag.put("error", "User.usernameTooBig");
			error = true;
		}

		if (!error && user.getUsername().indexOf('<') > -1 || user.getUsername().indexOf('>') > -1) {
			propertyBag.put("error", "User.usernameInvalidChars");
			error = true;
		}

		if (!error && !userRepository.isUsernameAvailable(user.getUsername(), user.getEmail())) {
			propertyBag.put("error", "User.usernameNotAvailable");
			error = true;
		}

		if (error) {
			viewService.renderView(Actions.INSERT);
			return;
		}

		userService.add(user);
		this.registerUserInSession(user);
		viewService.redirectToAction(Actions.REGISTRATION_COMPLETED);
	}

	/**
	 * Shows the profile of some user
	 * @param userId the user to show
	 */
	public void profile(@Parameter(key = "userId") int userId) {
		if (!userSession.getRoleManager().getCanViewProfile()) {
			viewService.accessDenied();
		}
		else {
			propertyBag.put("user", userRepository.get(userId));
			propertyBag.put("rankings", rankingRepository.getAllRankings());
		}
	}

	/**
	 * Shows a nice message after a sucessful registration
	 */
	public void registrationCompleted() {
		if (!userSession.isLogged()) {
			viewService.redirectToAction(Actions.INSERT);
		}
		else {
			propertyBag.put("user", userSession.getUser());
		}
	}

	public void lostPassword() {

	}

	public void lostPasswordSend(@Parameter(key = "username") String username,
		@Parameter(key = "email") String email) {

		boolean success = lostPasswordService.send(username, email);
		propertyBag.put("success", success);
	}

	/**
	 * Shows the page asking the user a new password
	 * @param hash the validation hash
	 */
	public void recoverPassword(@Parameter(key = "hash") String hash) {
		propertyBag.put("hash", hash);
	}

	/**
	 * Validate the new password hash
	 * @param hash the hash received by email
	 * @param username the username associated with the hash
	 * @param newPassword the new password to set
	 */
	public void recoverPasswordValidate(@Parameter(key = "hash") String hash,
		@Parameter(key = "username") String username, @Parameter(key = "newPassword") String newPassword) {
		User user = userRepository.validateLostPasswordHash(username, hash);

		if (user == null) {
			propertyBag.put("error", true);
			propertyBag.put("message", "PasswordRecovery.invalidData");
		}
		else {
			user.setPassword(newPassword);
			propertyBag.put("message", "PasswordRecovery.ok");
		}
	}

	private void registerUserInSession(User user) {
		userSession.setUser(user);
		userSession.becomeLogged();
		sessionManager.add(userSession);
	}

	private void removeAutoLoginCookies(UserSession us) {
		us.removeCookie(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN));
		us.removeCookie(config.getValue(ConfigKeys.COOKIE_USER_HASH));
	}

	private void activateAutoLogin(User user) {
		String securityHash = userService.generateAutoLoginSecurityHash(user.getId());
		user.setSecurityHash(securityHash);

		String userHash = userService.generateAutoLoginUserHash(securityHash);

		userSession.addCookie(config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN), "1");
		userSession.addCookie(config.getValue(ConfigKeys.COOKIE_USER_HASH), userHash);
		userSession.addCookie(config.getValue(ConfigKeys.COOKIE_USER_ID), Integer.toString(user.getId()));
	}
}
