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
package net.jforum.actions.extensions;

import java.lang.reflect.Method;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.security.AuthenticatedRule;
import net.jforum.services.TopicWatchService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class TopicWatchExtensionTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private TopicWatchService service = context.mock(TopicWatchService.class);
	private MockResult mockResult = new MockResult();
	private TopicWatchExtension extension = new TopicWatchExtension(sessionManager, service, mockResult);

	@Test
	public void afterListNogLoggedWatchingShouldBeFalse() {
		this.afterListExpectations(false);

		context.checking(new Expectations() {{
			one(mockResult).include("isUserWatchingTopic", false);
		}});

		extension.afterList();
		context.assertIsSatisfied();
	}

	@Test
	public void afterListLoggedWatchingShouldBeTrue() {
		this.afterListExpectations(true);

		context.checking(new Expectations() {{
			Topic t = new Topic(); t.setId(1);
			one(service).getSubscription(t, new User()); will(returnValue(new TopicWatch()));
			one(mockResult).include("isUserWatchingTopic", true);
		}});

		extension.afterList();
		context.assertIsSatisfied();
	}

	private void afterListExpectations(final boolean isLogged) {
		context.checking(new Expectations() {{
			UserSession us = context.mock(UserSession.class);
			one(sessionManager).getUserSession(); will(returnValue(us));
			Topic t = new Topic(); t.setId(1);

			if (isLogged) {
				one(mockResult).included().get("topic"); will(returnValue(t));
			}

			one(us).isLogged(); will(returnValue(isLogged));
			allowing(us).getUser(); will(returnValue(new User()));
		}});
	}

	@Test
	public void afterListShouldExtendList() throws Exception {
		Method method = extension.getClass().getMethod("afterList", new Class[0]);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(Extends.class));
		Assert.assertEquals(Actions.LIST, method.getAnnotation(Extends.class).value()[0]);
	}

	@Test
	public void watchShouldHaveAuthenticatedRuleAndDisplayLogin() throws Exception {
		this.shouldHaveAuthenticatedRuleAndDisplayLogin("watch");
	}

	@Test
	public void unwatchShouldHaveAuthenticatedRuleAndDisplayLogin() throws Exception {
		this.shouldHaveAuthenticatedRuleAndDisplayLogin("unwatch");
	}

	private void shouldHaveAuthenticatedRuleAndDisplayLogin(String methodName) throws Exception {
		Method method = extension.getClass().getMethod(methodName, int.class, int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(AuthenticatedRule.class, method.getAnnotation(SecurityConstraint.class).value());
		Assert.assertTrue(method.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void shouldBeAnExtensionOfTopics() {
		Assert.assertTrue(extension.getClass().isAnnotationPresent(ActionExtension.class));
		ActionExtension annotation = extension.getClass().getAnnotation(ActionExtension.class);
		Assert.assertEquals(Domain.TOPICS, annotation.value());
	}
}
