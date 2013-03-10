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
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
@Ignore("do we really want to test i18n like this")
public class MessageControllerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private I18n i18n = context.mock(I18n.class);
	private Result mockResult = context.mock(MockResult.class);
	private MessageController controller = new MessageController(i18n, mockResult);

	@Test
	public void replyWaitingModeration() {
		context.checking(new Expectations() {
			{
				one(i18n).getFormattedMessage("PostShow.waitingModeration",
					URLBuilder.build(Domain.TOPICS, Actions.LIST, 1));
				will(returnValue("msg moderation 1"));

				one(mockResult).include("message", "msg moderation 1");
				one(mockResult).forwardTo(Actions.MESSAGE);
			}
		});

		controller.replyWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void topicWaitingModeration() {
		context.checking(new Expectations() {
			{
				one(i18n).getFormattedMessage("PostShow.waitingModeration",
					URLBuilder.build(Domain.FORUMS, Actions.SHOW, 1));
				will(returnValue("msg moderation 1"));
				one(mockResult).include("message", "msg moderation 1");
				one(mockResult).forwardTo(Actions.MESSAGE);
			}
		});

		controller.topicWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void accessDenied() {
		context.checking(new Expectations() {
			{
				one(i18n).getMessage("Message.accessDenied");
				will(returnValue("msg denied"));
				one(mockResult).include("message", "msg denied");
				one(mockResult).forwardTo(Actions.MESSAGE);
			}
		});

		controller.accessDenied();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {
			{
				HttpServletRequest request = context.mock(HttpServletRequest.class);
				allowing(request).getContextPath();
			}
		});
	}
}
