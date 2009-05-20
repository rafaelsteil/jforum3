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
package net.jforum.services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.LogicRequest;
import org.vraptor.url.RequestInfo;

/**
 * @author Rafael Steil
 */
public class ViewServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private HttpServletResponse response = context.mock(HttpServletResponse.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private ViewService service = new ViewService(request, response, config);
	private States state = context.states("state");

	@Test
	public void getForumLinkValueDoesNotHaveTrailingSlashShouldAdd() {
		context.checking(new Expectations() {{
			one(config).getValue(ConfigKeys.FORUM_LINK); will(returnValue("forum.link"));
		}});

		String link = service.getForumLink();
		context.assertIsSatisfied();
		Assert.assertEquals("forum.link/", link);
	}

	@Test
	public void getForumLinkValueHaveTrailingSlashShoulReturnOriginalValue() {
		context.checking(new Expectations() {{
			one(config).getValue(ConfigKeys.FORUM_LINK); will(returnValue("forum.link/"));
		}});

		String link = service.getForumLink();
		context.assertIsSatisfied();
		Assert.assertEquals("forum.link/", link);
	}

	@Test
	public void accessDenied() throws IOException {
		state.become("redirect");

		context.checking(new Expectations() {{
			one(response).sendRedirect(String.format("/%s/%s.page", Domain.MESSAGES, Actions.ACCESS_DENIED));
		}});

		service.accessDenied();
		context.assertIsSatisfied();
	}

	@Test
	public void displayLogin() throws IOException {
		state.become("redirect");

		context.checking(new Expectations() {{
			one(response).sendRedirect(String.format("/%s/%s.page", Domain.USER, Actions.LOGIN));
		}});

		service.displayLogin();
		context.assertIsSatisfied();
	}

	@Test
	public void buildUrl() {
		state.become("redirect");
		Assert.assertEquals("/component/action/arg1/arg2/6.page",
			service.buildUrl("component", "action", "arg1", "arg2", 6));
	}

	@Test
	public void redirectToActionAndComponentWithOneArgument() throws IOException {
		state.become("redirect");

		context.checking(new Expectations() {{
			one(response).sendRedirect("/myothercomponent/myotheraction/arg1.page");
		}});

		service.redirectToAction("myothercomponent", "myotheraction", "arg1");
		context.assertIsSatisfied();
	}

	@Test
	public void redirectToActionAndComponentWithoutArguments() throws IOException {
		state.become("redirect");

		context.checking(new Expectations() {{
			one(response).sendRedirect("/myothercomponent/myotheraction.page");
		}});

		service.redirectToAction("myothercomponent", "myotheraction");
		context.assertIsSatisfied();
	}

	@Test
	public void redirectToActionWithTwoArguments() throws IOException {
		state.become("redirect");
		this.commonRedirectToActionExpectations();

		context.checking(new Expectations() {{
			one(response).sendRedirect("/mycomponent/myaction/1/2.page");
		}});

		service.redirectToAction("myaction", 1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void redirectToActionWithoutArguments() throws IOException {
		state.become("redirect");
		this.commonRedirectToActionExpectations();

		context.checking(new Expectations() {{
			one(response).sendRedirect("/mycomponent/myaction.page");
		}});

		service.redirectToAction("myaction");
		context.assertIsSatisfied();
	}

	private void commonRedirectToActionExpectations() {
		context.checking(new Expectations() {{
			LogicRequest logicRequest = context.mock(LogicRequest.class);
			RequestInfo requestInfo = context.mock(RequestInfo.class);
			one(request).getAttribute("context"); will(returnValue(logicRequest));
			one(logicRequest).getRequestInfo(); will(returnValue(requestInfo));
			one(requestInfo).getComponentName(); will(returnValue("mycomponent"));
		}});
	}

	@Test
	public void renderViewTwoArguments() {
		context.checking(new Expectations() {{
			one(request).setAttribute(ConfigKeys.RENDER_CUSTOM_LOGIC, "my action");
			one(request).setAttribute(ConfigKeys.RENDER_CUSTOM_COMPONENT, "my component");
		}});

		service.renderView("my component", "my action");
		context.assertIsSatisfied();
	}

	@Test
	public void renderViewSingleArgument() {
		context.checking(new Expectations() {{
			one(request).setAttribute(ConfigKeys.RENDER_CUSTOM_LOGIC, "my action");
		}});

		service.renderView("my action");
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(config).getValue(ConfigKeys.SERVLET_EXTENSION); will(returnValue(".page"));
			allowing(request).setAttribute(ConfigKeys.IGNORE_VIEW_MANAGER_REDIRECT, "true"); when(state.is("redirect"));
			allowing(request).getContextPath(); will(returnValue("")); when(state.is("redirect"));
		}});
	}
}
