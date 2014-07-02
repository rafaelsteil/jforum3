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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import net.jforum.actions.helpers.ApproveInfo;
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
import net.jforum.util.JForumConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ModerationControllerTestCase {

	
	@Mock private JForumConfig jForumConfig;
	@Mock private RoleManager roleManager;
	@Mock private ModerationService service;
	private ModerationLog moderationLog = new ModerationLog();
	@Mock private CategoryRepository categoryRepository;
	@Mock private UserSession userSession;
	@Mock private TopicRepository topicRepository;
	@Mock private ModerationLogRepository moderationLogRepository;
	private User user = new User();
	@Spy private MockResult mockResult;
	@Mock private ForumController mockForumController;
	@InjectMocks private ModerationController controller;

	@Test
	public void moveTopics() {
		when(userSession.getUser()).thenReturn(user);
		when(roleManager.getCanMoveTopics()).thenReturn(true);
			
		controller.moveTopics(1, "return path", moderationLog, 2, 3, 4);
		
		verify(service).moveTopics(1, moderationLog, 2, 3, 4);
		verify(mockResult).redirectTo("return path");
	}

	@Test
	public void moveTopicsDoesNotHaveRoleShouldIgnore() {
		when(roleManager.getCanMoveTopics()).thenReturn(false);
			
		controller.moveTopics(1, "return path", moderationLog, 1, 2);
		
		verify(mockResult).redirectTo("return path");
	}

	@Test
	public void askMoveDestination() {
		when(roleManager.getCanMoveTopics()).thenReturn(true);
		when(categoryRepository.getAllCategories()).thenReturn(new ArrayList<Category>());
			
		controller.askMoveDestination("return path", 10, 1, 2, 3);
		
		assertArrayEquals(new int[] { 1, 2, 3 }, (int[])mockResult.included("topicIds"));
		assertEquals(10, mockResult.included("fromForumId"));
		assertEquals("return path", mockResult.included("returnUrl"));
		assertEquals(new ArrayList<Category>(), mockResult.included("categories"));
	}

	@Test
	public void askMoveDestinationDoesNotHaveRoleShouldIgnore() {
		when(roleManager.getCanMoveTopics()).thenReturn(false);
			
		controller.askMoveDestination("return path", 1, 2, 3);
		
		verify(mockResult).redirectTo("return path");
	}

	@Test
	public void lockUnlock() {
		when(userSession.getUser()).thenReturn(user);
		when(roleManager.getCanLockUnlockTopics()).thenReturn(true);
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);
	
		controller.lockUnlock(1, null, moderationLog, new int[] { 1, 2, 3 });

		verify(service).lockUnlock(new int[] { 1, 2, 3 }, moderationLog);
		verify(mockForumController).show(1, 0);
	}

	@Test
	public void lockUnlockDoesNotHaveRoleShouldIgnore() {
		when(roleManager.getCanLockUnlockTopics()).thenReturn(false);
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);
			
		controller.lockUnlock(1, null, moderationLog, new int[] { 1 });
		
		verify(mockForumController).show(1, 0);
	}

	@Test
	public void deleteTopicsExpectSuccess() {
		when(userSession.getUser()).thenReturn(user);
		when(roleManager.getCanDeletePosts()).thenReturn(true);
		when(topicRepository.get(4)).thenReturn(new Topic());
		when(topicRepository.get(5)).thenReturn(new Topic());
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);

		controller.deleteTopics(1, null, new int[] { 4, 5 }, moderationLog);
		
		verify(service).deleteTopics(Arrays.asList(new Topic(), new Topic()), moderationLog);
		// TODO pass zero?
		verify(mockForumController).show(1, 0);
	}

	@Test
	public void deleteTopicsDoesNotHaveRoleShouldIgnore() {
		when(roleManager.getCanDeletePosts()).thenReturn(false);
		// TODO pass zero?
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);
			
		controller.deleteTopics(1, null, new int[] { 4 }, moderationLog);
		
		verify(mockForumController).show(1, 0);
	}

	@Test
	public void approveExpectSuccess() {
		when(roleManager.getCanApproveMessages()).thenReturn(true);
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);
			
		controller.approve(1, Arrays.asList(new ApproveInfo[0]));
		
		verify(service).doApproval(1, Arrays.asList(new ApproveInfo[0]));
		// TODO pass zero?
		verify(mockForumController).show(1, 0);
	}

	@Test
	public void approveDoesNotHaveRequiredRoleShouldIgnore() {
		when(roleManager.getCanApproveMessages()).thenReturn(false);
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumController);
			
		controller.approve(1, Arrays.asList(new ApproveInfo[0]));
		
		verify(mockForumController).show(1, 0);
	}
}
