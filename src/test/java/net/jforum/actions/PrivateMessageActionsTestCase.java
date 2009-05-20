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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.Role;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ForumException;
import net.jforum.core.support.vraptor.ViewPropertyBag;
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
import net.jforum.services.ViewService;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.vraptor.Interceptor;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.interceptor.MultipartRequestInterceptor;

/**
 * @author Rafael Steil
 */
public class PrivateMessageActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private PrivateMessageRepository repository = context.mock(PrivateMessageRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private SmilieRepository smilieRepository = context.mock(SmilieRepository.class);
	private PrivateMessageService service = context.mock(PrivateMessageService.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private PrivateMessageActions action = new PrivateMessageActions(repository, viewService,
		propertyBag, smilieRepository, userRepository, service, sessionManager);

	@Test
	public void review() {
		context.checking(new Expectations() {{
			PrivateMessage pm = new PrivateMessage(); pm.setId(1);
			one(repository).get(1); will(returnValue(pm));
			one(propertyBag).put("pm", pm);
			one(propertyBag).put("post", pm.asPost());
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
			one(viewService).redirectToAction(Actions.INBOX);
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
			one(propertyBag).put("pm", pm);
			one(propertyBag).put("post", new Post());
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
			ignoring(propertyBag);
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
			one(propertyBag).put("privateMessages", new ArrayList<PrivateMessage>());
			one(propertyBag).put("sentbox", true);
			one(viewService).renderView(Actions.MESSAGES);
		}});

		action.sent();
		context.assertIsSatisfied();
	}

	@Test
	public void sendSaveExpectSuccess() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));
			one(userRepository).get(1); will(returnValue(new User()));
			one(userSession).getUser(); will(returnValue(new User()));
			one(service).send(with(aNonNull(PrivateMessage.class)));
			one(viewService).redirectToAction(Actions.INBOX);
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
			one(propertyBag).put("pmRecipient", recipient);

			ignoring(propertyBag); ignoring(smilieRepository);
			one(viewService).renderView(Domain.TOPICS, Actions.ADD);
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

			one(propertyBag).put("pmRecipient", recipient);

			ignoring(propertyBag); ignoring(smilieRepository);
			one(viewService).renderView(Domain.TOPICS, Actions.ADD);
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

			ignoring(propertyBag); ignoring(smilieRepository);
			one(viewService).renderView(Domain.TOPICS, Actions.ADD);
		}});

		action.sendTo(1);
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCanInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(true));

			one(userRepository).findByUserName("an user"); will(returnValue(new ArrayList<User>()));
			one(propertyBag).put("users", new ArrayList<User>());
			one(propertyBag).put("username", "an user");
		}});

		action.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithUsernameCannotInteractWithOtherGroups() {
		context.checking(new Expectations() {{
			allowing(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getRoleManager(); will(returnValue(roleManager));

			one(roleManager).roleExists(SecurityConstants.INTERACT_OTHER_GROUPS); will(returnValue(false));

			User user = new User();
			one(userSession).getUser(); will(returnValue(user));

			one(userRepository).findByUserName("an user", user.getGroups()); will(returnValue(new ArrayList<User>()));
			one(propertyBag).put("users", new ArrayList<User>());
			one(propertyBag).put("username", "an user");
		}});

		action.findUser("an user");
		context.assertIsSatisfied();
	}

	@Test
	public void findUserWithoutUsername() {
		context.checking(new Expectations() {{
			one(propertyBag).put("username", null);
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
			one(propertyBag).put("post", new Post());
			one(propertyBag).put("isPrivateMessage", true);
			one(propertyBag).put("attachmentsEnabled", false);
			one(propertyBag).put("user", user);
			one(propertyBag).put("smilies", new ArrayList<Smilie>());
			one(viewService).renderView(Domain.TOPICS, Actions.ADD);
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
			one(propertyBag).put("inbox", true);
			one(propertyBag).put("privateMessages", new ArrayList<PrivateMessage>());
			one(viewService).renderView(Actions.MESSAGES);
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
	public void shouldBeInterceptedByActionSecurityInterceptor() throws Exception {
		Assert.assertTrue(action.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy annotation = action.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(annotation.value());
		Assert.assertTrue(interceptors.contains(ActionSecurityInterceptor.class));
	}

	@Test
	public void shouldBeInterceptedByMethodSecurityInterceptor() throws Exception {
		Assert.assertTrue(action.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy annotation = action.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(annotation.value());
		Assert.assertTrue(interceptors.contains(MethodSecurityInterceptor.class));
	}

	@Test
	public void shouldBeInterceptedByMultipartRequestInterceptor() throws Exception {
		Assert.assertTrue(action.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy annotation = action.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(annotation.value());
		Assert.assertTrue(interceptors.contains(MultipartRequestInterceptor.class));
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
