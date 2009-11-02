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
import java.util.Arrays;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ModerationLogRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.ModerationService;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ModerationActionsTestCase {

	private Mockery context = TestCaseUtils.newMockery();
	private JForumConfig jForumConfig = context.mock(JForumConfig.class);
	private ViewService viewService = context.mock(ViewService.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private ModerationService service = context.mock(ModerationService.class);
	private ModerationLog moderationLog = new ModerationLog();
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private UserSession userSession = context.mock(UserSession.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private ModerationLogRepository moderationLogRepository = context.mock(ModerationLogRepository.class);
	private User user = new User();
	private ModerationActions action = new ModerationActions(viewService, roleManager, service,
		categoryRepository, propertyBag, topicRepository, jForumConfig, moderationLogRepository, userSession);

	@Test
	public void moveTopics() {


		context.checking(new Expectations() {{
			one(userSession).getUser(); will(returnValue(user));
			one(roleManager).getCanMoveTopics(); will(returnValue(true));
			one(service).moveTopics(1, moderationLog, 2, 3, 4);
			one(viewService).redirect("return path");
		}});

		action.moveTopics(1, "return path", moderationLog, 2, 3, 4);
		context.assertIsSatisfied();
	}

	@Test
	public void moveTopicsDoesNotHaveRoleShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).getCanMoveTopics(); will(returnValue(false));
			one(viewService).redirect("return path");
		}});

		action.moveTopics(1, "return path", moderationLog, 1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void askMoveDestination() {
		context.checking(new Expectations() {{
			one(roleManager).getCanMoveTopics(); will(returnValue(true));
			one(categoryRepository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(propertyBag).put("topicIds", new int[] { 1, 2, 3 });
			one(propertyBag).put("fromForumId", 10);
			one(propertyBag).put("returnUrl", "return path");
			one(propertyBag).put("categories", new ArrayList<Category>());
		}});

		action.askMoveDestination("return path", 10, 1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void askMoveDestinationDoesNotHaveRoleShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).getCanMoveTopics(); will(returnValue(false));
			one(viewService).redirect("return path");
		}});

		action.askMoveDestination("return path", 1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void lockUnlock() {
		context.checking(new Expectations() {{
			one(userSession).getUser(); will(returnValue(user));
			one(roleManager).getCanLockUnlockTopics(); will(returnValue(true));
			one(service).lockUnlock(new int[]{1, 2, 3}, moderationLog);
			ignoring(viewService);
		}});

		action.lockUnlock(1, null, moderationLog, new int[]{1, 2, 3});
		context.assertIsSatisfied();
	}

	@Test
	public void lockUnlockDoesNotHaveRoleShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).getCanLockUnlockTopics(); will(returnValue(false));
			ignoring(viewService);
		}});

		action.lockUnlock(1, null, moderationLog, new int[]{1});
		context.assertIsSatisfied();
	}

	@Test
	public void deleteTopicsExpectSuccess() {
		context.checking(new Expectations() {{
			one(userSession).getUser(); will(returnValue(user));
			one(roleManager).getCanDeletePosts(); will(returnValue(true));
			one(topicRepository).get(4); will(returnValue(new Topic()));
			one(topicRepository).get(5); will(returnValue(new Topic()));
			one(service).deleteTopics(Arrays.asList(new Topic(), new Topic()), moderationLog);
			one(viewService).redirectToAction(Domain.FORUMS, Actions.SHOW, 1);
		}});

		action.deleteTopics(1, null, new int[]{4, 5}, moderationLog);
		context.assertIsSatisfied();
	}

	@Test
	public void deleteTopicsDoesNotHaveRoleShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).getCanDeletePosts(); will(returnValue(false));
			one(viewService).redirectToAction(Domain.FORUMS, Actions.SHOW, 1);
		}});

		action.deleteTopics(1, null, new int[]{4}, moderationLog);
		context.assertIsSatisfied();
	}

	@Test
	public void approveExpectSuccess() {
		context.checking(new Expectations() {{
			one(roleManager).getCanApproveMessages(); will(returnValue(true));
			one(service).doApproval(1, Arrays.asList(new ApproveInfo[0]));
			one(viewService).redirectToAction(Domain.FORUMS, Actions.SHOW, 1);
		}});

		action.approve(1, Arrays.asList(new ApproveInfo[0]));
	}

	@Test
	public void approveDoesNotHaveRequiredRoleShouldIgnore() {
		context.checking(new Expectations() {{
			one(roleManager).getCanApproveMessages(); will(returnValue(false));
			one(viewService).redirectToAction(Domain.FORUMS, Actions.SHOW, 1);
		}});

		action.approve(1, Arrays.asList(new ApproveInfo[0]));
	}
}
