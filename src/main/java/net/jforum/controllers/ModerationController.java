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

import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
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
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.MODERATION)
// @InterceptedBy(MethodSecurityInterceptor.class)
public class ModerationController {
	private final RoleManager roleManager;
	private final ModerationService moderationService;
	private final CategoryRepository categoryRepository;
	private final TopicRepository topicRepository;
	private final JForumConfig config;
	private final ModerationLogRepository logRepository;
	private final UserSession userSession;
	private final Result result;

	public ModerationController(Result result, RoleManager roleManager,
			ModerationService moderationService,
			CategoryRepository categoryRepository,
			TopicRepository topicRepository, JForumConfig config,
			ModerationLogRepository logRepository, UserSession userSession) {
		this.result = result;
		this.roleManager = roleManager;
		this.moderationService = moderationService;
		this.categoryRepository = categoryRepository;
		this.topicRepository = topicRepository;
		this.config = config;
		this.logRepository = logRepository;
		this.userSession = userSession;
	}

	public void showActivityLog(int page) {
		if (!roleManager.roleExists(SecurityConstants.VIEW_MODERATION_LOG)) {
			this.result.redirectTo(MessageController.class).accessDenied();
			return;
		}

		Pagination pagination = new Pagination(this.config, page)
				.forModerationLog(this.logRepository.getTotalRecords());
		List<ModerationLog> logs = this.logRepository.getAll(
				pagination.getStart(), pagination.getRecordsPerPage());

		this.result.include("logs", logs);
		this.result.include("pagination", pagination);
	}

	/**
	 * Move a set of topics to another forum
	 * 
	 * @param toForumId
	 *            the destination forum
	 * @param returnUrl
	 *            the url to redirect after the operation completes
	 * @param topicIds
	 *            the id of the topics to move
	 */
	@SecurityConstraint(ModerationRule.class)
	public void moveTopics(int toForumId, String returnUrl,
			ModerationLog moderationLog, int... topicIds) {

		if (this.roleManager.getCanMoveTopics()) {
			if (moderationLog != null) {
				moderationLog.setUser(this.userSession.getUser());
			}

			this.moderationService.moveTopics(toForumId, moderationLog,
					topicIds);
		}

		this.result.redirectTo(returnUrl);
	}

	/**
	 * Shows the page to ask for the destination forum for a set of topics to
	 * move
	 * 
	 * @param returnUrl
	 *            the return url to redirect after the operation is done
	 * @param topicIds
	 *            the id of the topics to move
	 */
	@SecurityConstraint(ModerationRule.class)
	public void askMoveDestination(String returnUrl, int forumId,
			int... topicIds) {
		if (!this.roleManager.getCanMoveTopics()) {
			this.result.redirectTo(returnUrl);
		} else {
			this.result.include("fromForumId", forumId);
			this.result.include("topicIds", topicIds);
			this.result.include("returnUrl", returnUrl);
			this.result.include("categories",
					this.categoryRepository.getAllCategories());
		}
	}

	/**
	 * Lock or unlock a set of topics
	 * 
	 * @param forumId
	 *            the forum
	 * @param returnUrl
	 *            the return url, if any
	 * @param topicIds
	 *            the id of the topics to lock or unlock
	 */
	@SecurityConstraint(ModerationRule.class)
	public void lockUnlock(int forumId, String returnUrl,
			ModerationLog moderationLog, int[] topicIds) {

		if (this.roleManager.getCanLockUnlockTopics()) {
			if (moderationLog != null) {
				moderationLog.setUser(this.userSession.getUser());
			}

			this.moderationService.lockUnlock(topicIds, moderationLog);
		}

		if (!StringUtils.isEmpty(returnUrl)) {
			this.result.forwardTo(returnUrl);
		} else {
			// TODO pass zero?
			this.result.redirectTo(ForumController.class).show(forumId, 0);
		}
	}

	/**
	 * Delete a set of topics
	 * 
	 * @param forumId
	 *            the forum
	 * @param topicIds
	 *            the id of the topics to delete
	 */
	@SecurityConstraint(ModerationRule.class)
	public void deleteTopics(int forumId, String returnUrl, int[] topicIds,
			ModerationLog moderationLog) {

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
			this.result.redirectTo(returnUrl);
		} else {
			// TODO pass zero?
			this.result.redirectTo(ForumController.class).show(forumId, 0);
		}
	}

	/**
	 * Approves ou denies currently moderated messages
	 * 
	 * @param forumId
	 *            the forum
	 * @param info
	 *            the set of posts to approve or deny, and the respective status
	 *            of each one
	 */
	@SecurityConstraint(ModerationRule.class)
	public void approve(int forumId, List<ApproveInfo> info) {
		if (this.roleManager.getCanApproveMessages()) {
			this.moderationService.doApproval(forumId, info);
		}

		// TODO pass zero?
		this.result.redirectTo(ForumController.class).show(forumId, 0);
	}
}
