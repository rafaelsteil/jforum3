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
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class PrivateMessageControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private PrivateMessageRepository repository = context.mock(PrivateMessageRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private PrivateMessageService service = context.mock(PrivateMessageService.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private Result mockResult = context.mock(MockResult.class);
	private TopicController mockTopicController = context.mock(TopicController.class);
	private PrivateMessageController controller = new PrivateMessageController(repository,
		userRepository, service, mockResult, userSession);

	@Test
	public void review() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage(); pm.setId(1);
			one(repository).get(1); will(returnValue(pm));
			one(mockResult).include("pm", pm);
			one(mockResult).include("post", pm.asPost());
		}});

		controller.review(1);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(userSession).getUser(); will(returnValue(new User()));
			one(service).delete(new User(), 1, 2, 3);
			one(mockResult).redirectTo(Actions.INBOX);
		}});

		controller.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void readExpectSuccess() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage();
			pm.setToUser(new User() {{ setId(1); }});
			pm.setFromUser(new User() {{ setId(3); }});

			one(repository).get(1); will(returnValue(pm));
			one(mockResult).include("pm", pm);
			one(mockResult).include("post", new Post());
		}});

		controller.read(1);
		context.assertIsSatisfied();
	}

	@Test
	public void readStatusIsNewShouldMarkAsRead() {
		final PrivateMessage pm = new PrivateMessage();
		pm.setToUser(new User() {{ setId(1); }});
		pm.setFromUser(new User() {{ setId(3); }});
		pm.setType(PrivateMessageType.NEW);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(pm));
			ignoring(mockResult);
		}});

		controller.read(1);
		context.assertIsSatisfied();
		Assert.assertEquals(PrivateMessageType.READ, pm.getType());
	}

	@Test
	public void sent() {
		context.checking(new Expectations() {{
			one(userSession).getUser(); will(returnValue(new User()));
			one(repository).getFromSentBox(new User()); will(returnValue(new ArrayList<PrivateMessage>()));
			one(mockResult).include("privateMessages", new ArrayList<PrivateMessage>());
			one(mockResult).include("sentbox", true);
			one(mockResult).forwardTo(Actions.MESSAGES);
		}});

		controller.sent();
		context.assertIsSatisfied();
	}

	@Test
	public void sendSaveExpectSuccess() {
		context.checking(new Expectations() {{
			one(userSession).getIp(); will(returnValue("0.0.0.0"));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));
			one(userRepository).get(1); will(returnValue(new User()));
			one(userSession).getUser(); will(returnValue(new User()));
			one(service).send(with(aNonNull(PrivateMessage.class)));
			one(mockResult).redirectTo(Actions.INBOX);
		}});

		controller.sendSave(new Post(), new PostFormOptions(), null, 1);
		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithoutUserIdShouldTryInvalidUsernameExpectsException() {
		context.checking(new Expectations() {{
			one(userRepository).getByUsername("invalid user"); will(returnValue(null));
		}});

		controller.sendSave(null, null, "invalid user", 0);
		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithInvalidUserIdExpectsException() {
		context.checking(new Expectations() {{
			one(userRepository).get(1); will(returnValue(null));
		}});

		controller.sendSave(null, null, null, 1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			User recipient = new User();
			one(userRepository).get(1); will(returnValue(recipient));

			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(userSession).getUser(); will(returnValue(new User()));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));
			one(mockResult).include("pmRecipient", recipient);
			one(mockResult).include("isPrivateMessage", true);
			one(mockResult).include("attachmentsEnabled", false);
			one(mockResult).include("user", recipient);
			one(mockResult).include("post", new Post());

			one(mockResult).forwardTo(TopicController.class);
			will(returnValue(mockTopicController));

			one(mockTopicController).add(0);
		}});

		controller.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsMatchingGroupFoundShouldFillPropertyBag() {
		context.checking(new Expectations() {{
			User recipient = new User();
			Group g1 = new Group(); g1.setId(1);
			recipient.addGroup(g1);

			one(userRepository).get(1); will(returnValue(recipient));

			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User currentUser = new User();
			Group g2 = new Group(); g2.setId(2);

			currentUser.addGroup(g2);
			currentUser.addGroup(g1);

			allowing(userSession).getUser(); will(returnValue(currentUser));

			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));
			one(mockResult).forwardTo("sendToDenied");

			ignoring(mockResult);
		}});

		controller.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsNoMatchingGroupFoundShouldNotFillPropertyBag() {
		context.checking(new Expectations() {{
			User recipient = new User();
			Group g1 = new Group();
			recipient.addGroup(g1);

			one(userRepository).get(1); will(returnValue(recipient));

			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User currentUser = new User();
			Group g2 = new Group();
			currentUser.addGroup(g2);
			allowing(userSession).getUser(); will(returnValue(currentUser));

			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));
			one(mockResult).forwardTo("sendToDenied");

			ignoring(mockResult);
		}});

		controller.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));

			one(userRepository).findByUserName("an user"); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("username", "an user");
		}});

		controller.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCannotInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).getCanOnlyContactModerators(); will(returnValue(false));

			User user = new User();


			one(roleManager).roleExists("interact_other_groups"); will(returnValue(false));
			one(userSession).getUser(); will(returnValue(user));
			one(userRepository).findByUserName("an user", user.getGroups()); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("username", "an user");
		}});

		controller.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithoutUsername() {
		context.checking(new Expectations() {{
			one(mockResult).include("username", null);
		}});

		controller.findUser(null);
		context.assertIsSatisfied();
	}

	@Test
	public void send() {
		context.checking(new Expectations() {{
			User user = new User(); user.setId(1);
			one(userSession).getUser(); will(returnValue(user));
			one(mockResult).include("post", new Post());
			one(mockResult).include("isPrivateMessage", true);
			one(mockResult).include("attachmentsEnabled", false);
			one(mockResult).include("user", user);

			one(mockResult).forwardTo(TopicController.class);
			will(returnValue(mockTopicController));

			//TODO pass zero?
			one(mockTopicController).add(0);
		}});

		controller.send();
		context.assertIsSatisfied();
	}

	@Test
	public void inbox() {
		context.checking(new Expectations() {{
			User user = new User(); user.setId(1);
			one(userSession).getUser(); will(returnValue(user));
			one(repository).getFromInbox(user); will(returnValue(new ArrayList<PrivateMessage>()));
			one(mockResult).include("inbox", true);
			one(mockResult).include("privateMessages", new ArrayList<PrivateMessage>());
			one(mockResult).forwardTo(Actions.MESSAGES);
		}});
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
