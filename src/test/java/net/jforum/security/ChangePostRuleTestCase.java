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

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;
import net.jforum.util.TestCaseUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangePostRuleTestCase {
	
	@Mock private UserSession userSession;
	@Mock private HttpServletRequest request;
	@Mock private RoleManager roleManager;
	@Mock private PostRepository repository;
	@Mock private SessionManager sessionManager;
	@InjectMocks private ChangePostRule rule;
	private Map<String, String[]> parameterMap;

	@Before
	public void setup() {
		parameterMap = new HashMap<String, String[]>();
		parameterMap.put("postId", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameterMap()).thenReturn(parameterMap);
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}
	
	@Test(expected = AccessRuleException.class)
	public void postIdNotFoundExpectsException() {
		parameterMap.clear();

		rule.shouldProceed(userSession, request);
	}

	@Test
	public void postIdInPostIdParameterExpectSuccess() throws Exception {
		when(request.getParameter("postId")).thenReturn("1");
		
		TestCaseUtils.executePrivateMethod("findPostId", rule, request);
	}

	@Test
	public void postIdInPostDotIdParameterExpectSuccess() throws Exception {
		parameterMap.clear(); parameterMap.put("post.id", Arrays.asList("1").toArray(new String[1]));
		when(request.getParameter("post.id")).thenReturn("1");
		
		TestCaseUtils.executePrivateMethod("findPostId", rule, request);
	}
}
