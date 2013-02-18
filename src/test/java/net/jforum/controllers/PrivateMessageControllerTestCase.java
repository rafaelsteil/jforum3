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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.Role;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.AuthenticatedRule;
import net.jforum.security.PrivateMessageOwnerRule;
import net.jforum.security.RoleManager;
import net.jforum.services.PrivateMessageService;
import net.jforum.util.SecurityConstants;

import org.junit.Assert;
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
public class PrivateMessageControllerTestCase {

	@Mock private PrivateMessageRepository repository;
	@Mock private UserRepository userRepository;
	@Mock private PrivateMessageService service;
	@Spy private MockResult mockResult;
	@Mock private UserSession userSession;

	@InjectMocks private PrivateMessageController controller;

	@Mock private RoleManager roleManager;
	@Mock private TopicController mockTopicController;

	@Test
	public void review() {
		PrivateMessage pm = new PrivateMessage();
		pm.setId(1);
		when(repository.get(1)).thenReturn(pm);

		controller.review(1);

		assertEquals(pm, mockResult.included("pm"));
		assertEquals(pm.asPost(), mockResult.included("post"));
	}

	@Test
	public void delete() {
		when(userSession.getUser()).thenReturn(new User());

		controller.delete(1, 2, 3);

		verify(service).delete(new User(), 1, 2, 3);
		verify(mockResult).redirectTo(Actions.INBOX);
	}

	@Test
	public void readExpectSuccess() {
		PrivateMessage pm = new PrivateMessage();
		User toUser = new User();
		toUser.setId(1);
		User fromUser = new User();
		fromUser.setId(3);

		pm.setToUser(toUser);
		pm.setFromUser(fromUser);

		when(repository.get(1)).thenReturn(pm);

		controller.read(1);

		assertEquals(pm, mockResult.included("pm"));
		assertEquals(new Post(), mockResult.included("post"));
	}

	@Test
	public void readStatusIsNewShouldMarkAsRead() {
		PrivateMessage pm = new PrivateMessage();
		User toUser = new User();
		toUser.setId(1);
		User fromUser = new User();
		fromUser.setId(3);

		pm.setToUser(toUser);
		pm.setFromUser(fromUser);
		pm.setType(PrivateMessageType.NEW);

		when(repository.get(1)).thenReturn(pm);

		controller.read(1);

		Assert.assertEquals(PrivateMessageType.READ, pm.getType());
	}

	@Test
	public void sent() {
		when(userSession.getUser()).thenReturn(new User());
		ArrayList<PrivateMessage> privateMessages = new ArrayList<PrivateMessage>();
		when(repository.getFromSentBox(new User())).thenReturn(privateMessages);

		controller.sent();

		assertEquals(privateMessages, mockResult.included("privateMessages"));
		assertEquals(true, mockResult.included("sentbox"));
	}

	@Test
	public void sendSaveExpectSuccess() {
		when(userSession.getIp()).thenReturn("0.0.0.0");
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(true);
		when(userRepository.get(1)).thenReturn(new User());
		when(userSession.getUser()).thenReturn(new User());

		controller.sendSave(new Post(), new PostFormOptions(), null, 1);

		verify(service).send(notNull(PrivateMessage.class));
		verify(mockResult).redirectTo(Actions.INBOX);
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithoutUserIdShouldTryInvalidUsernameExpectsException() {
		when(userRepository.getByUsername("invalid user")).thenReturn(null);

		controller.sendSave(null, null, "invalid user", 0);
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithInvalidUserIdExpectsException() {
		when(userRepository.get(1)).thenReturn(null);

		controller.sendSave(null, null, null, 1);
	}

	@Test
	public void sendToCanInteractWithOtherGroups() {
		User recipient = new User();
		when(userRepository.get(1)).thenReturn(recipient);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(userSession.getUser()).thenReturn(new User());
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(true);
		when(mockResult.forwardTo(TopicController.class)).thenReturn(mockTopicController);

		controller.sendTo(1);

		verify(mockTopicController).add(0);
		assertEquals(recipient, mockResult.included("pmRecipient"));
		assertEquals(true, mockResult.included("isPrivateMessage"));
		assertEquals(false, mockResult.included("attachmentsEnabled"));
		assertEquals(recipient, mockResult.included("user"));
		assertEquals(new Post(), mockResult.included("post"));
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsMatchingGroupFoundShouldFillPropertyBag() {
		Group g1 = new Group();
		g1.setId(1);
		Group g2 = new Group();
		g2.setId(2);
		
		User recipient = new User();
		recipient.addGroup(g1);

		User currentUser = new User();
		currentUser.addGroup(g2);
		currentUser.addGroup(g1);

		when(userRepository.get(1)).thenReturn(recipient);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(false);
		when(userSession.getUser()).thenReturn(currentUser);
		when(roleManager.getCanOnlyContactModerators()).thenReturn(true);

		controller.sendTo(1);

		verify(mockResult).forwardTo("sendToDenied");
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsNoMatchingGroupFoundShouldNotFillPropertyBag() {
		Group g1 = new Group();
		User recipient = new User();
		recipient.addGroup(g1);

		Group g2 = new Group();
		User currentUser = new User();
		currentUser.addGroup(g2);
		
		when(userRepository.get(1)).thenReturn(recipient);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.roleExists(SecurityConstants.INTERACT_OTHER_GROUPS)).thenReturn(false);
		when(userSession.getUser()).thenReturn(currentUser);
		when(roleManager.getCanOnlyContactModerators()).thenReturn(true);

		controller.sendTo(1);
		
		verify(mockResult).forwardTo("sendToDenied");
	}

	@Test
	public void findUserWithUsernameCanInteractWithOtherGroups() {
		ArrayList<User> users = new ArrayList<User>();
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanOnlyContactModerators()).thenReturn(true);
		when(userRepository.findByUserName("an user")).thenReturn(users);

		controller.findUser("an user");

		assertEquals(users, mockResult.included("users"));
		assertEquals("an user", mockResult.included("username"));
	}

	@Test
	public void findUserWithUsernameCannotInteractWithOtherGroups() {
		User user = new User();
		ArrayList<User> users = new ArrayList<User>();
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.getCanOnlyContactModerators()).thenReturn(false);
		when(roleManager.roleExists("interact_other_groups")).thenReturn(false);
		when(userSession.getUser()).thenReturn(user);
		when(userRepository.findByUserName("an user", user.getGroups())).thenReturn(users);

		controller.findUser("an user");

		assertEquals(users, mockResult.included("users"));
		assertEquals("an user", mockResult.included("username"));
	}

	@Test
	public void findUserWithoutUsername() {
		controller.findUser(null);
		
		assertEquals(null, mockResult.included("username"));
	}

	@Test
	public void send() {
		User user = new User();
		user.setId(1);
		
		when(userSession.getUser()).thenReturn(user);
		when(mockResult.forwardTo(TopicController.class)).thenReturn(mockTopicController);

		controller.send();

		verify(mockTopicController).add(0);
		assertEquals(new Post(), mockResult.included("post"));
		assertEquals(true, mockResult.included("isPrivateMessage"));
		assertEquals(false, mockResult.included("attachmentsEnabled"));
		assertEquals(user, mockResult.included("user"));
	}

	@Test
	public void inbox() {
		User user = new User();
		user.setId(1);
		ArrayList<PrivateMessage> privateMessages = new ArrayList<PrivateMessage>();
		
		when(userSession.getUser()).thenReturn(user);
		when(repository.getFromInbox(user)).thenReturn(privateMessages);
		
		controller.inbox();
		
		assertEquals(true, mockResult.included("inbox"));
		assertEquals(privateMessages, mockResult.included("privateMessages"));
	}

	@Test
	public void quoteShouldHaveOwnerConstraint() throws Exception {
		Method method = controller.getClass().getMethod("quote", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void replyShouldHaveOwnerConstraint() throws Exception {
		Method method = controller.getClass().getMethod("reply", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void readShouldHaveOwnerConstraint() throws Exception {
		Method method = controller.getClass().getMethod("read", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void reviewShouldHaveOwnerConstraint() throws Exception {
		Method method = controller.getClass().getMethod("review", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void shouldHaveAuthenticatedConstraintAndDisplayLogin() throws Exception {
		Assert.assertTrue(controller.getClass().isAnnotationPresent(SecurityConstraint.class));
		SecurityConstraint annotation = controller.getClass().getAnnotation(SecurityConstraint.class);
		Role[] roles = annotation.multiRoles();
		boolean found = false;

		for (Role role : roles) {
			if (role.value().equals(AuthenticatedRule.class)) {
				found = true;
				Assert.assertTrue(role.displayLogin());
			}
		}

		Assert.assertTrue(found);
	}
}
