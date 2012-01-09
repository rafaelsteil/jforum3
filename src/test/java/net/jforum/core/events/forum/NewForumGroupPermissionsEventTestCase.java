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
package net.jforum.core.events.forum;

import java.util.Arrays;

import net.jforum.entities.Forum;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.services.GroupService;
import net.jforum.util.SecurityConstants;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class NewForumGroupPermissionsEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private GroupRepository groupRepository = context.mock(GroupRepository.class);
	private GroupService groupService = context.mock(GroupService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private NewForumGroupPermissionsEvent event = new NewForumGroupPermissionsEvent(groupRepository, groupService, userSession);

	@Test
	public void added() {
		context.checking(new Expectations() {{
			Group group1 = createGroupWithRole(1, SecurityConstants.ADMINISTRATOR, SecurityConstants.APPROVE_MESSAGES);
			Group group2 = createGroupWithRole(2, SecurityConstants.CO_ADMINISTRATOR);
			Group group3 = createGroupWithRole(3, SecurityConstants.CATEGORY);
			Group group4 = createGroupWithRole(4, SecurityConstants.FORUM);

			one(groupRepository).getAllGroups();will(returnValue(
				Arrays.asList(group1, group2, group3, group4)));

			User user = new User();
			user.getGroups().add(group2);
			user.getGroups().add(group4);

			one(userSession).getUser(); will(returnValue(user));

			one(groupService).appendRole(group1, SecurityConstants.FORUM, 1);
			one(groupService).appendRole(group2, SecurityConstants.FORUM, 1);
		}});

		Forum forum = new Forum();
		forum.setId(1);

		event.added(forum);

		context.assertIsSatisfied();
	}

	private Group createGroupWithRole(int groupId, String... roleNames) {
		Group g = new Group();
		g.setId(groupId);

		for (String roleName : roleNames) {
			Role r = new Role();
			r.setName(roleName);

			g.addRole(r);
		}

		return g;
	}
}
