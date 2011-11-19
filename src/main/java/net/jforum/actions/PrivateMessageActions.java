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

import net.jforum.actions.helpers.ActionUtils;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.Role;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AuthenticatedRule;
import net.jforum.security.PrivateMessageEnabledRule;
import net.jforum.security.PrivateMessageOwnerRule;
import net.jforum.security.RoleManager;
import net.jforum.services.PrivateMessageService;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.PRIVATE_MESSAGES)
// @InterceptedBy({ MultipartRequestInterceptor.class,
// ActionSecurityInterceptor.class, MethodSecurityInterceptor.class })
@SecurityConstraint(multiRoles = {
		@Role(value = AuthenticatedRule.class, displayLogin = true),
		@Role(PrivateMessageEnabledRule.class) })
public class PrivateMessageActions {
	private PrivateMessageRepository repository;
	private UserRepository userRepository;
	private SmilieRepository smilieRepository;
	private PrivateMessageService service;
	private SessionManager sessionManager;
	private final Result result;

	public PrivateMessageActions(PrivateMessageRepository repository,
			SmilieRepository smilieRepository, UserRepository userRepository,
			PrivateMessageService service, SessionManager sessionManager,
			Result result) {
		this.repository = repository;
		this.smilieRepository = smilieRepository;
		this.userRepository = userRepository;
		this.service = service;
		this.sessionManager = sessionManager;
		this.result = result;
	}

	/**
	 * Delete a set of private message
	 * 
	 * @param ids
	 *            the id of the messages to delete
	 */
	public void delete(int... ids) {
		this.service
				.delete(this.sessionManager.getUserSession().getUser(), ids);
		this.result.redirectTo(Actions.INBOX);
	}

	/**
	 * Shows the page to review a private message while writing a reply
	 * 
	 * @param id
	 *            the id of the message being replied
	 */
	@SecurityConstraint(PrivateMessageOwnerRule.class)
	public void review(int id) {
		PrivateMessage pm = this.repository.get(id);
		this.result.include("pm", pm);
		this.result.include("post", pm.asPost());
	}

	/**
	 * Shows the page to quote a private message
	 * 
	 * @param id
	 *            the id of the message
	 */
	@SecurityConstraint(PrivateMessageOwnerRule.class)
	public void quote(int id) {
		PrivateMessage pm = this.repository.get(id);

		this.send();

		this.result.include("pm", pm);
		this.result.include("isPrivateMessageQuote", true);
	}

	/**
	 * Shows the page to reply a private message
	 * 
	 * @param id
	 *            the id of the message to reply
	 */
	@SecurityConstraint(PrivateMessageOwnerRule.class)
	public void reply(int id) {
		PrivateMessage pm = this.repository.get(id);

		this.send();

		this.result.include("pm", pm);
		this.result.include("isPrivateMessageReply", true);
	}

	/**
	 * Shows the page to read a specific message
	 * 
	 * @param id
	 *            the message id
	 */
	@SecurityConstraint(PrivateMessageOwnerRule.class)
	public void read(int id) {
		PrivateMessage pm = this.repository.get(id);

		if (pm.isNew()) {
			pm.markAsRead();
		}

		this.result.include("pm", pm);
		this.result.include("post", pm.asPost());
	}

	/**
	 * Shows the page ot sent messages
	 */
	public void sent() {
		User user = this.sessionManager.getUserSession().getUser();
		this.result.include("privateMessages",
				this.repository.getFromSentBox(user));
		this.result.include("sentbox", true);
		this.result.forwardTo(Actions.MESSAGES);
	}

	/**
	 * Send a private message to some user
	 * 
	 * @param post
	 *            the subject and the text
	 * @param options
	 *            formatting options
	 * @param toUsername
	 *            recipient username, only necessary if <code>toUserId</code>
	 *            not set
	 * @param toUserId
	 *            recipient id, only necessary if <code>toUsername</code> not
	 *            set
	 */
	public void sendSave(Post post, PostFormOptions options, String toUsername,
			int toUserId) {
		User toUser = this.findToUser(toUserId, toUsername);

		if (toUser == null || !this.canSendMessageTo(toUser)) {
			// TODO Show a nice message
			throw new ForumException("User not found");
		}

		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(this.sessionManager.getUserSession().getUser());
		pm.setToUser(toUser);
		pm.setSubject(post.getSubject());
		pm.setText(post.getText());
		pm.setIp(this.sessionManager.getUserSession().getIp());

		ActionUtils.definePrivateMessageOptions(pm, options);

		this.service.send(pm);

		this.result.redirectTo(Actions.INBOX);
	}

	/**
	 * Shows the page to search for users
	 * 
	 * @param username
	 *            if set, search for this username
	 */
	public void findUser(String username) {
		if (!StringUtils.isEmpty(username)) {
			RoleManager roleManager = this.sessionManager.getUserSession()
					.getRoleManager();

			if (roleManager.getCanOnlyContactModerators()) {
				List<User> users = this.userRepository.findByUserName(username);
				List<User> result = new ArrayList<User>();

				for (User user : users) {
					RoleManager roles = new RoleManager();
					roles.setGroups(user.getGroups());

					if (roles.isModerator() || roles.isAdministrator()
							|| roles.isCoAdministrator()) {
						result.add(user);
					}
				}

				this.result.include("users", result);
			} else {
				if (roleManager
						.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
					this.result.include("users",
							this.userRepository.findByUserName(username));
				} else {
					User currentUser = this.sessionManager.getUserSession()
							.getUser();
					this.result.include("users", this.userRepository
							.findByUserName(username, currentUser.getGroups()));
				}
			}
		}

		this.result.include("username", username);
	}

	/**
	 * Shows the page to send a new private message
	 */
	public void send() {
		this.result.include("post", new Post());
		this.result.include("isPrivateMessage", true);
		this.result.include("attachmentsEnabled", false);
		this.result.include("user", this.sessionManager.getUserSession()
				.getUser());
		this.result.include("smilies", this.smilieRepository.getAllSmilies());
		// this.viewService.renderView(Domain.TOPICS, Actions.ADD);

		// TODO pass zero?
		this.result.forwardTo(TopicActions.class).add(0);
	}

	/**
	 * Send a private message to a specific user
	 * 
	 * @param userId
	 */
	public void sendTo(int userId) {
		User recipient = this.userRepository.get(userId);

		if (this.canSendMessageTo(recipient)) {
			this.result.include("pmRecipient", recipient);
			this.send();
		} else {
			this.result.forwardTo("sendToDenied");
		}
	}

	private boolean canSendMessageTo(User toUser) {
		UserSession userSession = this.sessionManager.getUserSession();
		RoleManager roleManager = userSession.getRoleManager();

		if (roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)) {
			return true;
		}

		User currentUser = userSession.getUser();

		for (Group group : toUser.getGroups()) {
			if (currentUser.getGroups().contains(group)) {
				if (roleManager.getCanOnlyContactModerators()) {
					RoleManager roles = new RoleManager();
					roles.setGroups(toUser.getGroups());

					return roles.isModerator() || roles.isAdministrator()
							|| roles.isCoAdministrator();
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Shows the inbox of the current logged user
	 */
	public void inbox() {
		User user = this.sessionManager.getUserSession().getUser();
		this.result.include("inbox", true);
		this.result.include("privateMessages",
				this.repository.getFromInbox(user));
		this.result.forwardTo(Actions.MESSAGES);
	}

	private User findToUser(int userId, String username) {
		return userId == 0 ? this.userRepository.getByUsername(username)
				: this.userRepository.get(userId);
	}
}
