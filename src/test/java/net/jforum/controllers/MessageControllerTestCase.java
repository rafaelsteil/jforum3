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

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.util.I18n;
import net.jforum.util.TestCaseUtils;
import net.jforum.util.URLBuilder;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class MessageControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private I18n i18n = context.mock(I18n.class);
	private MockResult mockResult = new MockResult();
	private MessageController action = new MessageController(i18n, mockResult);

	@Test
	public void replyWaitingModeration() {
		context.checking(new Expectations() {{
			one(i18n).params(URLBuilder.build(Domain.TOPICS, Actions.LIST, 1)); will(returnValue(new Object[] { "url" }));
			one(i18n).getFormattedMessage("PostShow.waitingModeration", new Object[] { "url" }); will(returnValue("msg moderation 1"));
			one(mockResult).forwardTo(Actions.MESSAGE);
		}});

		action.replyWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void topicWaitingModeration() {
		context.checking(new Expectations() {{
			one(i18n).params(URLBuilder.build(Domain.TOPICS, Actions.LIST, 1)); will(returnValue(new Object[] { "url" }));
			one(i18n).getFormattedMessage("PostShow.waitingModeration", new Object[] { "url" }); will(returnValue("msg moderation 1"));
			one(mockResult).forwardTo(Actions.MESSAGE);
		}});

		action.topicWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void accessDenied() {
		context.checking(new Expectations() {
			{
				one(i18n).getMessage("Message.accessDenied");
				will(returnValue("msg denied"));
				one(mockResult).include("message", "msg denied");
			}
		});

		action.accessDenied();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {
			{
				one(mockResult).forwardTo(Actions.MESSAGE);
				HttpServletRequest request = context
						.mock(HttpServletRequest.class);
				allowing(request).getContextPath();
			}
		});
	}
}
