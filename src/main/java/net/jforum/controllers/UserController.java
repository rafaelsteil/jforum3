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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
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
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.USER)
public class UserController {
	private UserRepository userRepository;
	private UserService userService;
	private UserSession userSession;
	private SessionManager sessionManager;
	private LostPasswordService lostPasswordService;
	private JForumConfig config;
	private AvatarService avatarService;
	private RankingRepository rankingRepository;
	private final Result result;
	private final HttpServletRequest request;

	public UserController(UserRepository userRepository, UserSession userSession, UserService userService,
		SessionManager sessionFacade, JForumConfig config, LostPasswordService lostPasswordService,
		AvatarService avatarService, RankingRepository rankingRepository, Result result,
		HttpServletRequest request) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.sessionManager = sessionFacade;
		this.userSession = userSession;
		this.config = config;
		this.lostPasswordService = lostPasswordService;
		this.avatarService = avatarService;
		this.rankingRepository = rankingRepository;
		this.result = result;
		this.request = request;
	}

	/**
	 * Shows the page with all registered users
	 * @param page the pagination first record to start showing
	 */
	public void list(int page) {
		RoleManager roleManager = this.userSession.getRoleManager();

		if (!roleManager.isUserListingEnabled()) {
			this.result.include("users", new ArrayList<User>());
		}
		else {
			Pagination pagination = new Pagination(this.config, page)
				.forUsers(this.userRepository.getTotalUsers());

			if (roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
				this.result.include("users", this.userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage()));
			}
			else {
				User currentUser = this.userSession.getUser();
				this.result.include("users", this.userRepository.getAllUsers(pagination.getStart(),
					pagination.getRecordsPerPage(), currentUser.getGroups()));
			}

			this.result.include("pagination", pagination);
		}
	}

	/**
	 * Logout an authenticated user
	 */
	public void logout() {
		UserSession us = this.userSession;
		this.sessionManager.storeSession(us.getSessionId());

		us.becomeAnonymous(this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID));

		this.sessionManager.remove(us.getSessionId());
		this.sessionManager.add(us);
		this.removeAutoLoginCookies(us);

		this.result.redirectTo(ForumController.class).list();
	}

	/**
	 * Shows the form to log in
	 */
	public void login(String returnPath, boolean failed) {
		if (StringUtils.isEmpty(returnPath) && !this.config.getBoolean(ConfigKeys.LOGIN_IGNORE_REFERER)) {
			returnPath = this.request.getHeader("Referer");
		}

		result.include("invalidLogin", failed);

		if (!StringUtils.isEmpty(returnPath)) {
			this.result.include("returnPath", returnPath);
		}
	}

	/**
	 * Called from {@link #login(String)}, to validate the user credentials
	 *
	 * @param username the username
	 * @param password the password
	 * @param autoLogin autoLogin
	 */
	@br.com.caelum.vraptor.Post
	public void authenticateUser(String username,
		String password, boolean autoLogin,
		String returnPath) {
		User user = this.userService.validateLogin(username, password);

		if (user == null) {
			result.redirectTo(this).login(returnPath, true);
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
				this.result.redirectTo(returnPath);
			}
			else {
				this.result.redirectTo(ForumController.class).list();
			}
		}
	}

	/**
	 * Shows the page to edit the user profile
	 * @param userId the user id
	 */
	@SecurityConstraint(EditUserRule.class)
	@Path("/edit/{userId}")
	public void edit(int userId) {
		User userToEdit = this.userRepository.get(userId);
		this.result.include("user", userToEdit);
		this.result.include("rankings", this.rankingRepository.getAllRankings());
		this.result.include("avatars", this.avatarService.getAvatarGallery());
	}

	/**
	 * Updates an existing user
	 * @param user the user to update
	 */
	@SecurityConstraint(EditUserRule.class)
	public void editSave(User user, Integer avatarId, UploadedFile image, Integer rankingId) {

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
		this.result.redirectTo(this).edit(user.getId());
	}

	/**
	 * Shows the page to create a new user
	 */
	//@InterceptedBy(ExternalUserManagementInterceptor.class)
	public void insert() {

	}

	/**
	 * Adds a new user
	 * @param user the user to add
	 */
	@br.com.caelum.vraptor.Post
	public void insertSave(User user) {
		boolean error = false;

		if (!error && user.getUsername().length() > this.config.getInt(ConfigKeys.USERNAME_MAX_LENGTH)) {
			this.result.include("error", "User.usernameTooBig");
			error = true;
		}

		if (!error && user.getUsername().indexOf('<') > -1 || user.getUsername().indexOf('>') > -1) {
			this.result.include("error", "User.usernameInvalidChars");
			error = true;
		}

		if (!error && !this.userRepository.isUsernameAvailable(user.getUsername(), user.getEmail())) {
			this.result.include("error", "User.usernameNotAvailable");
			error = true;
		}

		if (error) {
			this.result.forwardTo(this).insert();
			return;
		}

		this.userService.add(user);
		this.registerUserInSession(user);
		this.result.redirectTo(this).registrationCompleted();
	}

	/**
	 * Shows the profile of some user
	 * @param userId the user to show
	 */
	@Path("/profile/{userId}")
	public void profile(int userId) {
		if (!this.userSession.getRoleManager().getCanViewProfile()) {
			this.result.redirectTo(MessageController.class).accessDenied();
		}
		else {
			User userToEdit = this.userRepository.get(userId);
			this.result.include("user", userToEdit);
			this.result.include("userTotalTopics", this.userRepository.getTotalTopics(userId));
			this.result.include("rankings", this.rankingRepository.getAllRankings());
			this.result.include("isAnonymousUser", userId == this.config.getInt(ConfigKeys.ANONYMOUS_USER_ID));

			boolean canEdit = userSession.getRoleManager().getCanEditUser(userToEdit, userSession.getUser().getGroups());
			this.result.include("canEdit", canEdit);
		}
	}

	/**
	 * Shows a nice message after a sucessful registration
	 */
	public void registrationCompleted() {
		if (!this.userSession.isLogged()) {
			this.result.redirectTo(this).insert();
		}
		else {
			this.result.include("user", this.userSession.getUser());
		}
	}

	public void lostPassword() {

	}

	public void lostPasswordSend(String username,
		String email) {

		boolean success = this.lostPasswordService.send(username, email);
		this.result.include("success", success);
	}

	/**
	 * Shows the page asking the user a new password
	 * @param hash the validation hash
	 */
	public void recoverPassword(String hash) {
		this.result.include("hash", hash);
	}

	/**
	 * Validate the new password hash
	 * @param hash the hash received by email
	 * @param username the username associated with the hash
	 * @param newPassword the new password to set
	 */
	public void recoverPasswordValidate(String hash,
		String username, String newPassword) {
		User user = this.userRepository.validateLostPasswordHash(username, hash);

		if (user == null) {
			this.result.include("error", true);
			this.result.include("message", "PasswordRecovery.invalidData");
		}
		else {
			user.setPassword(newPassword);
			this.result.include("message", "PasswordRecovery.ok");
		}
	}

	/**
	 * Lists all the posts made by an user
	 * @param userId the user id
	 */
	public void posts(int userId, int page) {
		User user = userRepository.get(userId);

		Pagination pagination = new Pagination(this.config, page).forUserPosts(user);

		List<Post> posts = userRepository.getPosts(user, pagination.getStart(), pagination.getRecordsPerPage());

		this.result.include("pagination", pagination);
		this.result.include("posts", posts);
		this.result.include("user", user);
	}

	/**
	 * Lists all the topics made by an user
	 * @param userId the user id
	 */
	public void topics(int userId, int page) {
		User user = userRepository.get(userId);

		Pagination pagination = new Pagination(this.config, page).forUserTopics(user, userRepository.getTotalTopics(userId));

		List<Topic> topics = userRepository.getTopics(user, pagination.getStart(), pagination.getRecordsPerPage());

		this.result.include("pagination", pagination);
		this.result.include("topics", topics);
		this.result.include("user", user);
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
