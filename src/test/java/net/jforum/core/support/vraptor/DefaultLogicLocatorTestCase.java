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
package net.jforum.core.support.vraptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.jforum.core.UrlPattern;
import net.jforum.core.exceptions.ForumException;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.vraptor.component.ComponentManager;
import org.vraptor.component.ComponentType;
import org.vraptor.component.DefaultLogicMethod;
import org.vraptor.component.LogicMethod;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.url.LogicLocator;

/**
 * @author Rafael Steil
 */
public class DefaultLogicLocatorTestCase {
	private Mockery context = TestCaseUtils.newMockery();

	private final HttpSession session = context.mock(HttpSession.class);
	private final VRaptorServletRequest request = context.mock(VRaptorServletRequest.class);
	private final JForumConfig config = context.mock(JForumConfig.class);
	private final ServletContext servletContext = context.mock(ServletContext.class);
	private final ComponentManager componentManager = context.mock(ComponentManager.class);
	private final ApplicationContext springContext = context.mock(ApplicationContext.class);
	private final ComponentType componentType = context.mock(ComponentType.class);
	private final LogicMethod logicMethod = context.mock(DefaultLogicMethod.class);

	@Test
	public void zeroParameters_rootContextPath_onlyServletName_queryStringWithAtLeastTwoArgs() throws Exception {
		// /jforum.page?module=posts&action=list
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue(""));
			one(request).getRequestURI(); will(returnValue("/jforum.page"));
			one(request).getParameter("module"); will(returnValue("posts"));
			one(request).getParameter("action"); will(returnValue("list"));
			one(componentManager).getComponent("posts", "list"); will(returnValue(componentType));
			one(componentType).getLogic("list"); will(returnValue(logicMethod));
			exactly(0).of(request).setAttribute(with(aNonNull(String.class)), with(aNonNull(String.class)));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test
	public void oneParameter_nonRootContextPath_emptyQueryString_hasJSessionId() throws Exception {
		// /xpto/posts/list/50.page;jsessionid=1234567890
		context.checking(new Expectations() {{
			one(request).getRequestURI(); will(returnValue("/xpto/posts/list/50.page;jsessionid=1234567890"));
			one(request).getContextPath(); will(returnValue("/xpto"));
			one(config).getUrlPattern("posts.list.1"); will(returnValue(new UrlPattern("topicId")));
			exactly(1).of(request).setParameter("topicId", "50");
			one(componentManager).getComponent("posts", "list"); will(returnValue(componentType));
			one(componentType).getLogic("list"); will(returnValue(logicMethod));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void urlPatternNotFoundExpectsException() throws Exception {
		context.checking(new Expectations() {{
			one(request).getRequestURI(); will(returnValue("/xpto/posts/list/50.page;jsessionid=1234567890"));
			one(request).getContextPath(); will(returnValue("/xpto"));
			one(config).getUrlPattern("posts.list.1"); will(returnValue(null));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test
	public void oneParameter_rootContextPath() throws Exception {
		// /forums/show/3.page
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue(""));
			one(request).getRequestURI(); will(returnValue("/forums/show/3.page"));
			one(componentManager).getComponent("forums", "show"); will(returnValue(componentType));
			one(componentType).getLogic("show"); will(returnValue(logicMethod));
			one(config).getUrlPattern("forums.show.1"); will(returnValue(new UrlPattern("forumId")));
			exactly(1).of(request).setParameter("forumId", "3");
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test
	public void zeroParameters_rootContextPath_emtpyQueryString() throws Exception {
		// /categories/list.page
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue(""));
			one(request).getRequestURI(); will(returnValue("/categories/list.page"));
			one(componentManager).getComponent("categories", "list"); will(returnValue(componentType));
			one(componentType).getLogic("list"); will(returnValue(logicMethod));
			one(config).getUrlPattern("categories.list.0"); will(returnValue(new UrlPattern("")));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test
	public void twoParameters_nonRootContextPath_emtpyQueryString() throws Exception {
		// /jforum3/categories/list/15/1234.page
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue("/jforum3"));
			one(request).getRequestURI(); will(returnValue("/jforum3/categories/list/15/1234.page"));
			one(componentManager).getComponent("categories", "list"); will(returnValue(componentType));
			one(componentType).getLogic("list"); will(returnValue(logicMethod));
			one(config).getUrlPattern("categories.list.2"); will(returnValue(new UrlPattern("start, categoryId")));
			exactly(1).of(request).setParameter("start", "15");
			exactly(1).of(request).setParameter("categoryId", "1234");
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void jforumDotPageWithoutModuleExpectsException() throws Exception {
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue(""));
			one(request).getRequestURI(); will(returnValue("/jforum.page"));
			one(request).getParameter("module"); will(returnValue(null));
			one(request).getParameter("action"); will(returnValue("list"));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Test(expected = ForumException.class)
	public void jforumDotPageWithoutActionExpectsException() throws Exception {
		context.checking(new Expectations() {{
			one(request).getContextPath(); will(returnValue(""));
			one(request).getRequestURI(); will(returnValue("/jforum.page"));
			one(request).getParameter("module"); will(returnValue("posts"));
			one(request).getParameter("action"); will(returnValue(null));
		}});

		LogicLocator logicLocator = new DefaultLogicLocator(componentManager);
		logicLocator.locate(request);

		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			one(request).getSession(); will(returnValue(session));
			one(session).getServletContext(); will(returnValue(servletContext));
			one(servletContext).getAttribute(ConfigKeys.SPRING_CONTEXT); will(returnValue(springContext));
			one(springContext).getBean(JForumConfig.class.getName()); will(returnValue(config));
			one(config).getValue(ConfigKeys.SERVLET_EXTENSION); will(returnValue(".page"));
		}});
	}
}
