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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
//import net.jforum.actions.helpers.PermissionOptions;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTestCase {

	@Mock private GroupRepository repository;
	@Mock private SessionManager sessionManager;
	@Mock private UserSession userSession;
	@Mock private RoleManager roleManager;
	@Mock private UserRepository userRepository;
	@InjectMocks private GroupService service;

	@Test
	@Ignore("test must be fixed, permission problem")
	public void savePermissions() {
		final Group group = new Group();

		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAdministrator()).thenReturn(false);
		when(repository.get(1)).thenReturn(new Group());

		//TODO: service.savePermissions(1, new PermissionOptions());

		verify(userRepository).changeAllowAvatarState(false, group);
		verify(sessionManager).computeAllOnlineModerators();
		verify(repository).update(group);
	}

	@Test
	public void delete() {
		when(repository.get(1)).thenReturn(new Group());
		when(repository.get(2)).thenReturn(new Group());

		service.delete(1, 2);

		verify(repository, times(2)).remove(notNull(Group.class));
	}

	@Test(expected = NullPointerException.class)
	public void updateUsingNullGroupExpectsNPE() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingNullNameExpectsValidationException() {
		Group g = new Group();
		g.setId(1);
		g.setName(null);

		service.update(g);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingEmptyNameExpectsValidationException() {
		Group g = new Group();
		g.setId(1);
		g.setName("");

		service.update(g);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingIdZeroExpectsValidationException() {
		Group g = new Group();
		g.setName("g1");
		g.setId(0);

		service.update(g);
	}

	@Test
	public void updateExpectsSuccess() {
		Group g = new Group();
		g.setName("g1");
		g.setId(2);

		service.update(g);

		verify(repository).update(notNull(Group.class));
	}

	@Test
	public void addExpectSuccess() {
		Group g = new Group();
		g.setName("g1");

		service.add(g);

		verify(repository).add(notNull(Group.class));
	}

	@Test(expected = NullPointerException.class)
	public void addUsingNullGroupExpectsNPE() {
		service.add(null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingidBiggerThanZeroExpectsValidationException() {
		Group g = new Group();
		g.setName("g1");
		g.setId(1);

		service.add(g);
	}

	@Test(expected = ValidationException.class)
	public void addUsingEmtpyNameExpectsValidationException() {
		Group g = new Group();
		g.setName("");

		service.add(g);
	}

	@Test(expected = ValidationException.class)
	public void addUsingNullNameExpectsValidationException() {
		Group g = new Group();
		g.setName(null);

		service.add(g);
	}
}
