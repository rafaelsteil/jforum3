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
package net.jforum.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;


/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupInteractionFilterTestCase {
	
	@Mock private UserSession userSession;
	@Spy private MockResult mockResult;

	@Test
	public void filterForumListing() {
		User u1 = new User(); User u2 = new User(); User u3 = new User();

		Group g1 = new Group(); g1.setId(1);
		Group g2 = new Group(); g2.setId(2);
		Group g3 = new Group(); g3.setId(3);

		u1.addGroup(g1);
		u2.addGroup(g1); u2.addGroup(g2);
		u3.addGroup(g3);

		final UserSession us1 = new UserSession(); us1.setSessionId("1"); us1.setUser(u1);
		final UserSession us2 = new UserSession(); us2.setSessionId("2"); us2.setUser(u2);
		final UserSession us3 = new UserSession(); us3.setSessionId("3"); us3.setUser(u3);

		when(userSession.getUser()).thenReturn(u1);

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("onlineUsers", Arrays.asList(us1, us2, us3));
		when(mockResult.included()).thenReturn(m);
		
		GroupInteractionFilter filter = new GroupInteractionFilter();
		filter.filterForumListing(mockResult, userSession);
		
		assertEquals(2, mockResult.included("totalLoggedUsers"));
		assertEquals(new HashSet<UserSession>(Arrays.asList(us1, us2)), mockResult.included("onlineUsers"));
	}
}