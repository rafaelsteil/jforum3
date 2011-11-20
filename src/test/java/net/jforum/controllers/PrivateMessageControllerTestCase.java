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
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.Smilie;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;
import net.jforum.repository.SmilieRepository;
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

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class PrivateMessageControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private PrivateMessageRepository repository = context.mock(PrivateMessageRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private SmilieRepository smilieRepository = context.mock(SmilieRepository.class);
	private PrivateMessageService service = context.mock(PrivateMessageService.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private MockResult mockResult = new MockResult();
	private PrivateMessageController action = new PrivateMessageController(repository,
		smilieRepository, userRepository, service, sessionManager, mockResult);

	@Test
	public void review() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage(); pm.setId(1);
			one(repository).get(1); will(returnValue(pm));
			one(mockResult).include("pm", pm);
			one(mockResult).include("post", pm.asPost());
		}});

		action.review(1);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getUser(); will(returnValue(new User()));
			one(service).delete(new User(), 1, 2, 3);
			one(mockResult).redirectTo(Actions.INBOX);
		}});

		action.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void readExpectSuccess() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage();
			pm.setToUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(1); }});
			pm.setFromUser(new User() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); }});

			one(repository).get(1); will(returnValue(pm));
			one(mockResult).include("pm", pm);
			one(mockResult).include("post", new Post());
		}});

		action.read(1);
		context.assertIsSatisfied();
	}

	@Test
	public void readStatusIsNewShouldMarkAsRead() {
		final PrivateMessage pm = new PrivateMessage();
		pm.setToUser(new User() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		pm.setFromUser(new User() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(3); }});
		pm.setType(PrivateMessageType.NEW);

		context.checking(new Expectations() {{

			one(repository).get(1); will(returnValue(pm));
			ignoring(mockResult);
		}});

		action.read(1);
		context.assertIsSatisfied();
		Assert.assertEquals(PrivateMessageType.READ, pm.getType());
	}

	@Test
	public void sent() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getUser(); will(returnValue(new User()));
			one(repository).getFromSentBox(new User()); will(returnValue(new ArrayList<PrivateMessage>()));
			one(mockResult).include("privateMessages", new ArrayList<PrivateMessage>());
			one(mockResult).include("sentbox", true);
			one(mockResult).forwardTo(Actions.MESSAGES);
		}});

		action.sent();
		context.assertIsSatisfied();
	}

	@Test
	public void sendSaveExpectSuccess() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getIp(); will(returnValue("0.0.0.0"));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));
			one(userRepository).get(1); will(returnValue(new User()));
			one(userSession).getUser(); will(returnValue(new User()));
			one(service).send(with(aNonNull(PrivateMessage.class)));
			one(mockResult).redirectTo(Actions.INBOX);
		}});

		action.sendSave(new Post(), new PostFormOptions(), null, 1);
		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithoutUserIdShouldTryInvalidUsernameExpectsException() {
		context.checking(new Expectations() {{
			one(userRepository).getByUsername("invalid user"); will(returnValue(null));
		}});

		action.sendSave(null, null, "invalid user", 0);
		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void sendSaveWithInvalidUserIdExpectsException() {
		context.checking(new Expectations() {{
			one(userRepository).get(1); will(returnValue(null));
		}});

		action.sendSave(null, null, null, 1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			User recipient = new User();
			one(userRepository).get(1); will(returnValue(recipient));

			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(userSession).getUser(); will(returnValue(new User()));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));
			one(mockResult).include("pmRecipient", recipient);

			ignoring(mockResult); ignoring(smilieRepository);

			//TODO pass zero?
			one(mockResult).forwardTo(TopicController.class).add(0);
		}});

		action.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsMatchingGroupFoundShouldFillPropertyBag() {
		context.checking(new Expectations() {{
			User recipient = new User();
			Group g1 = new Group(); g1.setId(1);
			recipient.addGroup(g1);

			one(userRepository).get(1); will(returnValue(recipient));

			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User currentUser = new User();
			Group g2 = new Group(); g2.setId(2);

			currentUser.addGroup(g2);
			currentUser.addGroup(g1);

			allowing(userSession).getUser(); will(returnValue(currentUser));

			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));
			one(mockResult).forwardTo("sendToDenied");

			ignoring(mockResult); ignoring(smilieRepository);
		}});

		action.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void sendToCanotInteractWithOtherGroupsNoMatchingGroupFoundShouldNotFillPropertyBag() {
		context.checking(new Expectations() {{
			User recipient = new User();
			Group g1 = new Group();
			recipient.addGroup(g1);

			one(userRepository).get(1); will(returnValue(recipient));

			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User currentUser = new User();
			Group g2 = new Group();
			currentUser.addGroup(g2);
			allowing(userSession).getUser(); will(returnValue(currentUser));

			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));
			one(mockResult).forwardTo("sendToDenied");

			ignoring(mockResult); ignoring(smilieRepository);
		}});

		action.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).getCanOnlyContactModerators(); will(returnValue(true));

			one(userRepository).findByUserName("an user"); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("username", "an user");
		}});

		action.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCannotInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).getCanOnlyContactModerators(); will(returnValue(false));

			User user = new User();


			one(roleManager).roleExists("interact_other_groups"); will(returnValue(false));
			one(userSession).getUser(); will(returnValue(user));
			one(userRepository).findByUserName("an user", user.getGroups()); will(returnValue(new ArrayList<User>()));
			one(mockResult).include("users", new ArrayList<User>());
			one(mockResult).include("username", "an user");
		}});

		action.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithoutUsername() {
		context.checking(new Expectations() {{
			one(mockResult).include("username", null);
		}});

		action.findUser(null);
		context.assertIsSatisfied();
	}

	@Test
	public void send() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			User user = new User(); user.setId(1);
			one(userSession).getUser(); will(returnValue(user));
			one(smilieRepository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));
			one(mockResult).include("post", new Post());
			one(mockResult).include("isPrivateMessage", true);
			one(mockResult).include("attachmentsEnabled", false);
			one(mockResult).include("user", user);
			one(mockResult).include("smilies", new ArrayList<Smilie>());

			//TODO pass zero?
			one(mockResult).forwardTo(TopicController.class).add(0);
		}});

		action.send();
		context.assertIsSatisfied();
	}

	@Test
	public void inbox() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
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
		Method method = action.getClass().getMethod("quote", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void replyShouldHaveOwnerConstraint() throws Exception {
		Method method = action.getClass().getMethod("reply", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void readShouldHaveOwnerConstraint() throws Exception {
		Method method = action.getClass().getMethod("read", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void reviewShouldHaveOwnerConstraint() throws Exception {
		Method method = action.getClass().getMethod("review", int.class);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(PrivateMessageOwnerRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void shouldHaveAuthenticatedConstraintAndDisplayLogin() throws Exception {
		Assert.assertTrue(action.getClass().isAnnotationPresent(SecurityConstraint.class));
		SecurityConstraint annotation = action.getClass().getAnnotation(SecurityConstraint.class);
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
