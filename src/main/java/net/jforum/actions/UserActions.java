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
import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ExternalUserManagementInterceptor;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.entities.Post;
import net.jforum.entities.Ranking;
import net.jforum.entities.Topic;
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
		this.sessionManager = sessionFacade;
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
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isUserListingEnabled()) {
			this.propertyBag.put("users", new ArrayList<User>());
		}
		else {
			Pagination pagination = new Pagination(this.config, page)
				.forUsers(this.userRepository.getTotalUsers());

			if (roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
				this.propertyBag.put("users", this.userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage()));
			}
			else {
				User currentUser = this.sessionManager.getUserSession().getUser();
				this.propertyBag.put("users", this.userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage(), currentUser.getGroups()));
			}

			this.propertyBag.put("pagination", pagination);
		}
	}

	/**
	 * Logout an authenticated user
	 */
	public void logout() {
		UserSession us = this.sessionManager.getUserSession();
		this.sessionManager.storeSession(us.getSessionId());

		us.becomeAnonymous(this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID));

		this.sessionManager.remove(us.getSessionId());
		this.sessionManager.add(us);
		this.removeAutoLoginCookies(us);

		this.viewService.redirectToAction(Domain.FORUMS, Actions.LIST);
	}

	/**
	 * Shows the form to log in
	 */
	public void login(@Parameter(key = "returnPath") String returnPath) {
		if (StringUtils.isEmpty(returnPath) && !this.config.getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER)) {
			returnPath = this.viewService.getReferer();
		}

		if (!StringUtils.isEmpty(returnPath)) {
			this.propertyBag.put("returnPath", returnPath);
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
		User user = this.userService.validateLogin(username, password);

		if (user == null) {
			this.propertyBag.put("invalidLogin", true);
			this.viewService.renderView(Actions.LOGIN);
		}
		else {
			this.userSession.setUser(user);
			this.userSession.becomeLogged();

			if (autoLogin) {
				this.activateAutoLogin(user);
			}
			else {
				this.removeAutoLoginCookies(this.userSession);
			}

			this.sessionManager.add(this.userSession);

			if (!StringUtils.isEmpty(returnPath)) {
				this.viewService.redirect(returnPath);
			}
			else {
				this.viewService.redirectToAction(Domain.FORUMS, Actions.LIST);
			}
		}
	}

	/**
	 * Shows the page to edit the user profile
	 * @param userId the user id
	 */
	@SecurityConstraint(EditUserRule.class)
	public void edit(@Parameter(key = "userId") int userId) {
		User userToEdit = this.userRepository.get(userId);
		this.propertyBag.put("user", userToEdit);
		this.propertyBag.put("rankings", this.rankingRepository.getAllRankings());
		this.propertyBag.put("avatars", this.avatarService.getGalleryAvatar());
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
			this.avatarService.add(avatar, image);
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

		RoleManager roleManager = this.userSession.getRoleManager();
		boolean canChangeUserName = roleManager.isAdministrator() || roleManager.isCoAdministrator();

		boolean isSSOAuthentication = ConfigKeys.TYPE_SSO.equals(this.config.getValue(ConfigKeys.AUTHENTICATION_TYPE));
		canChangeUserName = canChangeUserName && !isSSOAuthentication;

		this.userService.update(user, canChangeUserName);
		this.viewService.redirectToAction(Actions.EDIT, user.getId());
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

		if (!error && user.getUsername().length() > this.config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)) {
			this.propertyBag.put("error", "User.usernameTooBig");
			error = true;
		}

		if (!error && user.getUsername().indexOf('<') > -1 || user.getUsername().indexOf('>') > -1) {
			this.propertyBag.put("error", "User.usernameInvalidChars");
			error = true;
		}

		if (!error && !this.userRepository.isUsernameAvailable(user.getUsername(), user.getEmail())) {
			this.propertyBag.put("error", "User.usernameNotAvailable");
			error = true;
		}

		if (error) {
			this.viewService.renderView(Actions.INSERT);
			return;
		}

		this.userService.add(user);
		this.registerUserInSession(user);
		this.viewService.redirectToAction(Actions.REGISTRATION_COMPLETED);
	}

	/**
	 * Shows the profile of some user
	 * @param userId the user to show
	 */
	public void profile(@Parameter(key = "userId") int userId) {
		if (!this.userSession.getRoleManager().getCanViewProfile()) {
			this.viewService.accessDenied();
		}
		else {
			User userToEdit = this.userRepository.get(userId);
			this.propertyBag.put("user", userToEdit);
			this.propertyBag.put("userTotalTopics", this.userRepository.getTotalTopics(userId));
			this.propertyBag.put("rankings", this.rankingRepository.getAllRankings());
			this.propertyBag.put("isAnonnimousUser", userId == this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID));

			boolean canEdit = userSession.getRoleManager().getCanEditUser(userToEdit, userSession.getUser().getGroups());
			this.propertyBag.put("canEdit", canEdit);
		}
	}

	/**
	 * Shows a nice message after a sucessful registration
	 */
	public void registrationCompleted() {
		if (!this.userSession.isLogged()) {
			this.viewService.redirectToAction(Actions.INSERT);
		}
		else {
			this.propertyBag.put("user", this.userSession.getUser());
		}
	}

	public void lostPassword() {

	}

	public void lostPasswordSend(@Parameter(key = "username") String username,
		@Parameter(key = "email") String email) {

		boolean success = this.lostPasswordService.send(username, email);
		this.propertyBag.put("success", success);
	}

	/**
	 * Shows the page asking the user a new password
	 * @param hash the validation hash
	 */
	public void recoverPassword(@Parameter(key = "hash") String hash) {
		this.propertyBag.put("hash", hash);
	}

	/**
	 * Validate the new password hash
	 * @param hash the hash received by email
	 * @param username the username associated with the hash
	 * @param newPassword the new password to set
	 */
	public void recoverPasswordValidate(@Parameter(key = "hash") String hash,
		@Parameter(key = "username") String username, @Parameter(key = "newPassword") String newPassword) {
		User user = this.userRepository.validateLostPasswordHash(username, hash);

		if (user == null) {
			this.propertyBag.put("error", true);
			this.propertyBag.put("message", "PasswordRecovery.invalidData");
		}
		else {
			user.setPassword(newPassword);
			this.propertyBag.put("message", "PasswordRecovery.ok");
		}
	}

	/**
	 * Lists all the posts made by an user
	 * @param userId the user id
	 */
	public void posts(@Parameter(key = "userId") int userId, @Parameter(key = "page") int page) {
		User user = userRepository.get(userId);

		Pagination pagination = new Pagination(this.config, page).forUserPosts(user);

		List<Post> posts = userRepository.getPosts(user, pagination.getStart(), pagination.getRecordsPerPage());

		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("posts", posts);
		this.propertyBag.put("user", user);
	}

	/**
	 * Lists all the topics made by an user
	 * @param userId the user id
	 */
	public void topics(@Parameter(key = "userId") int userId, @Parameter(key = "page") int page) {
		User user = userRepository.get(userId);

		Pagination pagination = new Pagination(this.config, page).forUserTopics(user, userRepository.getTotalTopics(userId));

		List<Topic> topics = userRepository.getTopics(user, pagination.getStart(), pagination.getRecordsPerPage());

		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("topics", topics);
		this.propertyBag.put("user", user);
	}

	private void registerUserInSession(User user) {
		this.userSession.setUser(user);
		this.userSession.becomeLogged();
		this.sessionManager.add(this.userSession);
	}

	private void removeAutoLoginCookies(UserSession us) {
		us.removeCookie(this.config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN));
		us.removeCookie(this.config.getValue(ConfigKeys.COOKIE_USER_HASH));
	}

	private void activateAutoLogin(User user) {
		String securityHash = this.userService.generateAutoLoginSecurityHash(user.getId());
		user.setSecurityHash(securityHash);

		String userHash = this.userService.generateAutoLoginUserHash(securityHash);

		this.userSession.addCookie(this.config.getValue(ConfigKeys.COOKIE_AUTO_LOGIN), "1");
		this.userSession.addCookie(this.config.getValue(ConfigKeys.COOKIE_USER_HASH), userHash);
		this.userSession.addCookie(this.config.getValue(ConfigKeys.COOKIE_USER_ID), Integer.toString(user.getId()));
	}
}
