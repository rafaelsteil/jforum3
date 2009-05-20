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
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ModerationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ModerationService;
import net.jforum.services.ViewService;

import org.apache.commons.lang.StringUtils;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.MODERATION)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(ModerationRule.class)
public class ModerationActions {
	private ViewService viewService;
	private RoleManager roleManager;
	private ModerationService moderationService;
	private CategoryRepository categoryRepository;
	private TopicRepository topicRepository;
	private ViewPropertyBag propertyBag;

	public ModerationActions(ViewService viewService, RoleManager roleManager,
		ModerationService moderationService, CategoryRepository categoryRepository,
		ViewPropertyBag propertyBag, TopicRepository topicRepository) {
		this.viewService = viewService;
		this.roleManager = roleManager;
		this.moderationService = moderationService;
		this.categoryRepository = categoryRepository;
		this.propertyBag = propertyBag;
		this.topicRepository = topicRepository;
	}

	/**
	 * Move a set of topics to another forum
	 * @param toForumId the destination forum
	 * @param returnUrl the url to redirect after the operation completes
	 * @param topicIds the id of the topics to move
	 */
	public void moveTopics(@Parameter(key = "toForumId") int toForumId,
		@Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "topicIds") int... topicIds) {
		if (roleManager.getCanMoveTopics()) {
			moderationService.moveTopics(toForumId, topicIds);
		}

		viewService.redirect(returnUrl);
	}

	/**
	 * Shows the page to ask for the destination forum for a set of topics to move
	 * @param returnUrl the return url to redirect after the operation is done
	 * @param topicIds the id of the topics to move
	 */
	public void askMoveDestination( @Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "forumId") int forumId, @Parameter(key = "topicIds") int... topicIds) {
		if (!roleManager.getCanMoveTopics()) {
			viewService.redirect(returnUrl);
		}
		else {
			propertyBag.put("fromForumId", forumId);
			propertyBag.put("topicIds", topicIds);
			propertyBag.put("returnUrl", returnUrl);
			propertyBag.put("categories", categoryRepository.getAllCategories());
		}
	}

	/**
	 * Lock or unlock a set of topics
	 * @param forumId the forum
	 * @param returnUrl the return url, if any
	 * @param topicIds the id of the topics to lock or unlock
	 */
	public void lockUnlock(@Parameter(key = "forumId") int forumId,
		@Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "topicIds") int... topicIds) {
		if (roleManager.getCanLockUnlockTopics()) {
			moderationService.lockUnlock(topicIds);
		}

		if (!StringUtils.isEmpty(returnUrl)) {
			viewService.redirect(returnUrl);
		}
		else {
			viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
		}
	}

	/**
	 * Delete a set of topics
	 * @param forumId the forum
	 * @param topicIds the id of the topics to delete
	 */
	public void deleteTopics(@Parameter(key = "forumId") int forumId, @Parameter(key = "returnUrl") String returnUrl,
		@Parameter(key = "topicIds") int... topicIds) {
		if (roleManager.getCanDeletePosts()) {
			List<Topic> topics = new ArrayList<Topic>();

			for (int topicId : topicIds) {
				Topic topic = topicRepository.get(topicId);
				topics.add(topic);
			}

			moderationService.deleteTopics(topics);
		}

		if (!StringUtils.isEmpty(returnUrl)) {
			viewService.redirect(returnUrl);
		}
		else {
			viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
		}
	}

	/**
	 * Approves ou denies currently moderated messages
	 * @param forumId the forum
	 * @param info the set of posts to approve or deny, and the respective status of each one
	 */
	public void approve(@Parameter(key = "forumId") int forumId, @Parameter(key = "info", create = true) List<ApproveInfo> info) {
		if (roleManager.getCanApproveMessages()) {
			moderationService.doApproval(forumId, info);
		}

		viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, forumId);
	}
}
