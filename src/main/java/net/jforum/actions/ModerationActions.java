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
import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ModerationLogRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ModerationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ModerationService;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.MODERATION)
@InterceptedBy(MethodSecurityInterceptor.class)
public class ModerationActions {
	private final ViewService viewService;
	private final RoleManager roleManager;
	private final ModerationService moderationService;
	private final CategoryRepository categoryRepository;
	private final TopicRepository topicRepository;
	private final ViewPropertyBag propertyBag;
	private final JForumConfig config;
	private final ModerationLogRepository logRepository;
	private final UserSession userSession;

	public ModerationActions(ViewService viewService, RoleManager roleManager, ModerationService moderationService,
			CategoryRepository categoryRepository, ViewPropertyBag propertyBag, TopicRepository topicRepository,
			JForumConfig config, ModerationLogRepository logRepository, UserSession userSession) {
		this.viewService = viewService;
		this.roleManager = roleManager;
		this.moderationService = moderationService;
		this.categoryRepository = categoryRepository;
		this.propertyBag = propertyBag;
		this.topicRepository = topicRepository;
		this.config = config;
		this.logRepository = logRepository;
		this.userSession = userSession;
	}

	public void showActivityLog(@Parameter(key = "page") int page) {
		if (!roleManager.roleExists(SecurityConstants.VIEW_MODERATION_LOG)) {
			this.viewService.accessDenied();
			return;
		}

		Pagination pagination = new Pagination(this.config, page).forModerationLog(this.logRepository.getTotalRecords());
		List<ModerationLog> logs = this.logRepository.getAll(pagination.getStart(), pagination.getRecordsPerPage());

		this.propertyBag.put("logs", logs);
		this.propertyBag.put("pagination", pagination);
	}

	/**
	 * Move a set of topics to another forum
	 * @param toForumId the destination forum
	 * @param returnUrl the url to redirect after the operation completes
	 * @param topicIds the id of the topics to move
	 */
	@SecurityConstraint(ModerationRule.class)
	public void moveTopics(@Parameter(key = "toForumId") int toForumId, @Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "moderationLog") ModerationLog moderationLog, @Parameter(key = "topicIds") int... topicIds) {

		if (this.roleManager.getCanMoveTopics()) {
			if (moderationLog != null) {
				moderationLog.setUser(this.userSession.getUser());
			}

			this.moderationService.moveTopics(toForumId, moderationLog, topicIds);
		}

		this.viewService.redirect(returnUrl);
	}

	/**
	 * Shows the page to ask for the destination forum for a set of topics to move
	 * @param returnUrl the return url to redirect after the operation is done
	 * @param topicIds the id of the topics to move
	 */
	@SecurityConstraint(ModerationRule.class)
	public void askMoveDestination( @Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "forumId") int forumId, @Parameter(key = "topicIds") int... topicIds) {
		if (!this.roleManager.getCanMoveTopics()) {
			this.viewService.redirect(returnUrl);
		}
		else {
			this.propertyBag.put("fromForumId", forumId);
			this.propertyBag.put("topicIds", topicIds);
			this.propertyBag.put("returnUrl", returnUrl);
			this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
		}
	}

	/**
	 * Lock or unlock a set of topics
	 * @param forumId the forum
	 * @param returnUrl the return url, if any
	 * @param topicIds the id of the topics to lock or unlock
	 */
	@SecurityConstraint(ModerationRule.class)
	public void lockUnlock(@Parameter(key = "forumId") int forumId, @Parameter(key = "returnUrl") String returnUrl,
			@Parameter(key = "moderationLog") ModerationLog moderationLog, @Parameter(key = "topicIds") int[] topicIds) {

		if (this.roleManager.getCanLockUnlockTopics()) {
			if (moderationLog != null) {
				moderationLog.setUser(this.userSession.getUser());
			}

			this.moderationService.lockUnlock(topicIds, moderationLog);
		}

		if (!StringUtils.isEmpty(returnUrl)) {
			this.viewService.redirect(returnUrl);
		}
		else {
			this.viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
		}
	}

	/**
	 * Delete a set of topics
	 * @param forumId the forum
	 * @param topicIds the id of the topics to delete
	 */
	@SecurityConstraint(ModerationRule.class)
	public void deleteTopics(@Parameter(key = "forumId") int forumId, @Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "topicIds") int[] topicIds, @Parameter(key = "moderationLog") ModerationLog moderationLog) {

		if (this.roleManager.getCanDeletePosts()) {
			List<Topic> topics = new ArrayList<Topic>();

			for (int topicId : topicIds) {
				Topic topic = this.topicRepository.get(topicId);
				topics.add(topic);
			}

			if (moderationLog != null) {
				moderationLog.setUser(this.userSession.getUser());
			}

			this.moderationService.deleteTopics(topics, moderationLog);
		}

		if (!StringUtils.isEmpty(returnUrl)) {
			this.viewService.redirect(returnUrl);
		}
		else {
			this.viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
		}
	}

	/**
	 * Approves ou denies currently moderated messages
	 * @param forumId the forum
	 * @param info the set of posts to approve or deny, and the respective status of each one
	 */
	@SecurityConstraint(ModerationRule.class)
	public void approve(@Parameter(key = "forumId") int forumId, @Parameter(key = "info", create = true) List<ApproveInfo> info) {
		if (this.roleManager.getCanApproveMessages()) {
			this.moderationService.doApproval(forumId, info);
		}

		this.viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
	}
}
