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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Topic;
import net.jforum.entities.TopicWatch;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.security.AuthenticatedRule;
import net.jforum.services.TopicWatchService;

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
public class TopicWatchExtensionTestCase {
	
	@Mock private TopicWatchService service;
	@Spy private MockResult mockResult;
	@Mock private UserSession userSession;
	@InjectMocks private TopicWatchExtension extension;

	@Test
	public void afterListNogLoggedWatchingShouldBeFalse() {
		this.afterListExpectations(false);

		extension.afterList();

		assertEquals(false, mockResult.included("isUserWatchingTopic"));
	}

	@Test
	public void afterListLoggedWatchingShouldBeTrue() {
		this.afterListExpectations(true);
		Topic t = new Topic();
		t.setId(1);
		when(service.getSubscription(t, new User())).thenReturn(new TopicWatch());

		extension.afterList();

		assertEquals(true, mockResult.included("isUserWatchingTopic"));
	}

	private void afterListExpectations(final boolean isLogged) {
		Topic t = new Topic();
		t.setId(1);

		if (isLogged) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("topic", t);
			when(mockResult.included()).thenReturn(m);
		}

		when(userSession.isLogged()).thenReturn(isLogged);
		when(userSession.getUser()).thenReturn(new User());
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
