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
package net.jforum.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ChangePostRuleTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserSession userSession = context.mock(UserSession.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private PostRepository repository = context.mock(PostRepository.class);
    private SessionManager sessionManager = context.mock(SessionManager.class);
	private ChangePostRule rule = new ChangePostRule(repository, sessionManager);
	private Map<String, String> parameterMap = new HashMap<String, String>() {/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	{ put("postId", "1"); }};

	@Test(expected = AccessRuleException.class)
	public void postIdNotFoundExpectsException() {
		parameterMap.clear();

		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			ignoring(userSession); ignoring(roleManager);
		}});


		rule.shouldProceed(userSession, request);
		context.assertIsSatisfied();
	}

	@Test
	public void postIdInPostIdParameterExpectSuccess() throws Exception {
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("postId"); will(returnValue("1"));
		}});

		TestCaseUtils.executePrivateMethod("findPostId", rule, request);
		context.assertIsSatisfied();
	}

	@Test
	public void postIdInPostDotIdParameterExpectSuccess() throws Exception {
		parameterMap.clear(); parameterMap.put("post.id", "1");
		context.checking(new Expectations() {{
			atLeast(1).of(request).getParameterMap(); will(returnValue(parameterMap));
			one(request).getParameter("post.id"); will(returnValue("1"));
		}});

		TestCaseUtils.executePrivateMethod("findPostId", rule, request);
		context.assertIsSatisfied();
	}
}
