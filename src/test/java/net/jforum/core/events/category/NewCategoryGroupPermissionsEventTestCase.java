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
package net.jforum.core.events.category;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import net.jforum.entities.Category;
import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.services.GroupService;
import net.jforum.util.SecurityConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class NewCategoryGroupPermissionsEventTestCase {
	
	@Mock private GroupRepository groupRepository;
	@Mock private GroupService groupService;
	@Mock private UserSession userSession;
	@InjectMocks private NewCategoryGroupPermissionsEvent event;

	@Test
	public void added() {
		Group group1 = createGroupWithRole(1, SecurityConstants.ADMINISTRATOR, SecurityConstants.APPROVE_MESSAGES);
		Group group2 = createGroupWithRole(2, SecurityConstants.CO_ADMINISTRATOR);
		Group group3 = createGroupWithRole(3, SecurityConstants.CATEGORY);
		Group group4 = createGroupWithRole(4, SecurityConstants.CATEGORY);

		when(groupRepository.getAllGroups()).thenReturn(Arrays.asList(group1, group2, group3, group4));

		User user = new User();
		user.getGroups().add(group2);
		user.getGroups().add(group4);

		when(userSession.getUser()).thenReturn(user);

		Category c = new Category();
		c.setId(1);

		event.added(c);

		verify(groupService).appendRole(group1, SecurityConstants.CATEGORY, 1);
		verify(groupService).appendRole(group2, SecurityConstants.CATEGORY, 1);
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
