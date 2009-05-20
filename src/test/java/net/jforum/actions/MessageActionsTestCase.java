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

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.services.ViewService;
import net.jforum.util.I18n;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class MessageActionsTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private I18n i18n = context.mock(I18n.class);
	private MessageActions action = new MessageActions(propertyBag, viewService, i18n);

	@Test
	public void replyWaitingModeration() {
		context.checking(new Expectations() {{
			one(viewService).buildUrl(Domain.TOPICS, Actions.LIST, 1); will(returnValue("url"));
			one(i18n).params("url"); will(returnValue(new Object[] { "url" }));
			one(i18n).getFormattedMessage("PostShow.waitingModeration", new Object[] { "url" }); will(returnValue("msg moderation 1"));
			one(propertyBag).put("message", "msg moderation 1");
		}});

		action.replyWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void topicWaitingModeration() {
		context.checking(new Expectations() {{
			one(viewService).buildUrl(Domain.FORUMS, Actions.SHOW, 1); will(returnValue("url"));
			one(i18n).params("url"); will(returnValue(new Object[] { "url" }));
			one(i18n).getFormattedMessage("PostShow.waitingModeration", new Object[] { "url" }); will(returnValue("msg moderation 1"));
			one(propertyBag).put("message", "msg moderation 1");
		}});

		action.topicWaitingModeration(1);
		context.assertIsSatisfied();
	}

	@Test
	public void accessDenied() {
		context.checking(new Expectations() {{
			one(i18n).getMessage("Message.accessDenied"); will(returnValue("msg denied"));
			one(propertyBag).put("message", "msg denied");
		}});

		action.accessDenied();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			one(viewService).renderView(Actions.MESSAGE);
			HttpServletRequest request = context.mock(HttpServletRequest.class);
			allowing(request).getContextPath();
		}});
	}
}
