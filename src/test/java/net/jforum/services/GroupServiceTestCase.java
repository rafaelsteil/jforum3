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

//import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class GroupServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private GroupRepository repository = context.mock(GroupRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private UserSession userSession = context.mock(UserSession.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private GroupService service = new GroupService(repository, userRepository, userSession, sessionManager);

	@Test
	public void savePermissions() {
		final Group group = new Group();

		context.checking(new Expectations() {{
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAdministrator(); will(returnValue(false));
			one(userRepository).changeAllowAvatarState(false, group);
			one(sessionManager).computeAllOnlineModerators();
			one(repository).get(1); will(returnValue(new Group()));
			one(repository).update(group);
		}});

//TODO: fix PermOption		service.savePermissions(1, new PermissionOptions());
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Group()));
			one(repository).get(2); will(returnValue(new Group()));
			exactly(2).of(repository).remove(with(aNonNull(Group.class)));
		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
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

		context.checking(new Expectations() {{
			one(repository).update(with(aNonNull(Group.class)));
		}});

		service.update(g);
		context.assertIsSatisfied();
	}

	@Test
	public void addExpectSuccess() {
		Group g = new Group();
		g.setName("g1");

		context.checking(new Expectations() {{
			one(repository).add(with(aNonNull(Group.class)));
		}});

		service.add(g);
		context.assertIsSatisfied();
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
