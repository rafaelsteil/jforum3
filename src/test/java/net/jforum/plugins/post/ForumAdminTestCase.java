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
package net.jforum.plugins.post;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.JForumConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumAdminTestCase {
	

	@Mock private ForumLimitedTimeRepository repository;
	@Mock private JForumConfig config;
	@Mock private ForumRepository forumRepository;
	@Spy private MockResult mockResult;
	@Mock private UserSession userSession;
	@InjectMocks private ForumAdminExtension extension;
	@Mock private RoleManager roleManager;
 
	@Test
	@SuppressWarnings("serial")
	public void edit() {
		final int forumId = 1;

		when(config.getBoolean("forum.time.limited.enable", false)).thenReturn(true);
		when(forumRepository.get(forumId)).thenReturn(new Forum(1));
		when(repository.getLimitedTime(any(Forum.class))).thenReturn(0L);

		extension.edit(forumId);

		assertEquals(true, mockResult.included("forumTimeLimitedEnable"));
		assertEquals(0L, mockResult.included("forumLimitedTime"));

	}

	@Test
	public void add() {
		when(config.getBoolean("forum.time.limited.enable", false)).thenReturn(true);

		extension.add();

		assertEquals(true, mockResult.included("fourmTimeLimitedEnable"));
		assertEquals(0, mockResult.included("fourmLimitedTime"));
	}

	@Test
	public void editSave() {
		this.securityChecking();
		final Forum forum = new Forum();
		forum.setCategory(new Category());
		final ForumLimitedTime forumLimitedTime = new ForumLimitedTime();

		when(config.getBoolean("forum.time.limited.enable", false)).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(repository.getForumLimitedTime(forum)).thenReturn(forumLimitedTime);

		extension.editSave(forum, 23);

		verify(repository).saveOrUpdate(forumLimitedTime);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSave() {
		this.securityChecking();
		final Forum forum = new Forum();
		forum.setId(1);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("forum", forum);

		when(config.getBoolean("forum.time.limited.enable", false)).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(mockResult.included()).thenReturn(m);

		extension.addSave(23);

		verify(repository).add(any(ForumLimitedTime.class));
	}

	@SuppressWarnings("serial")
	@Test
	public void delete() {
		this.securityChecking();
		final Forum forum = new Forum(23);
		ForumLimitedTime forumLimitedTime = new ForumLimitedTime();
		forumLimitedTime.setId(1);
		when(config.getBoolean("forum.time.limited.enable", false)).thenReturn(true);
		when(roleManager.isAdministrator()).thenReturn(true);
		when(repository.getForumLimitedTime(forum)).thenReturn(forumLimitedTime);

		extension.delete(23);

		verify(repository).remove(forumLimitedTime);
	}

	private void securityChecking() {
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(userSession.getUser()).thenReturn(new User());
	}

	@Test
	public void editShouldExtendEdit() throws Exception {
		Method method = extension.getClass().getMethod("edit", int.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(Extends.class));
		assertEquals(Actions.EDIT, method.getAnnotation(Extends.class).value()[0]);
	}

	@Test
	public void editSaveShouldExtendEditSave() throws Exception {
		Method method = extension.getClass().getMethod("editSave", Forum.class, long.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(Extends.class));
		assertEquals(Actions.EDITSAVE, method.getAnnotation(Extends.class).value()[0]);
	}
	@Test
	public void addShouldExtendAdd() throws Exception {
		Method method = extension.getClass().getMethod("add");
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(Extends.class));
		assertEquals(Actions.ADD, method.getAnnotation(Extends.class).value()[0]);
	}
	@Test
	public void addSaveShouldExtendAddSave() throws Exception {
		Method method = extension.getClass().getMethod("addSave", long.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(Extends.class));
		assertEquals(Actions.ADDSAVE, method.getAnnotation(Extends.class).value()[0]);
	}
	@Test
	public void deleteShouldExtendSelete() throws Exception {
		Method method = extension.getClass().getMethod("delete", int[].class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(Extends.class));
		assertEquals("delete", method.getAnnotation(Extends.class).value()[0]);
	}

	@Test
	public void shouldBeAnExtensionOfAdminForums() {
		assertTrue(extension.getClass().isAnnotationPresent(ActionExtension.class));
		ActionExtension annotation = extension.getClass().getAnnotation(ActionExtension.class);
		assertEquals(Domain.FORUMS_ADMIN, annotation.value());
	}

}
