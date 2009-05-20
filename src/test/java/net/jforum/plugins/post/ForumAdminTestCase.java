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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumAdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();

	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private ForumLimitedTimeRepository repository = context.mock(ForumLimitedTimeRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private ForumAdminExtension extension = new ForumAdminExtension(config, forumRepository, propertyBag, repository, sessionManager);
	
	private RoleManager roleManager = context.mock(RoleManager.class);

	@SuppressWarnings("serial")
	@Test
	public void edit() {
		final int forumId = 1;

		context.checking(new Expectations() {{
			one(config).getBoolean("forum.time.limited.enable", false); will(returnValue(true));
			
			one(forumRepository).get(forumId); will(returnValue(new Forum(){{setId(1);}}));
			one(repository).getLimitedTime(with(any(Forum.class))); will(returnValue(0L));
			one(propertyBag).put("forumTimeLimitedEnable", true);
			one(propertyBag).put("forumLimitedTime", 0L);
		}});

		extension.edit(forumId);
		context.assertIsSatisfied();
	}

	@Test
	public void add() {
		context.checking(new Expectations() {{
			one(config).getBoolean("forum.time.limited.enable", false); will(returnValue(true));
			one(propertyBag).put("fourmTimeLimitedEnable", true);
			one(propertyBag).put("fourmLimitedTime", 0);
		}});

		extension.add();
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		this.securityChecking();

		final Forum forum = new Forum().withCategory(new Category());
		final ForumLimitedTime forumLimitedTime = new ForumLimitedTime();
		context.checking(new Expectations() {{
			one(config).getBoolean("forum.time.limited.enable", false); will(returnValue(true));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(repository).getForumLimitedTime(forum); will(returnValue(forumLimitedTime));
			one(repository).saveOrUpdate(forumLimitedTime);
		}});

		extension.editSave(forum, 23);
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		this.securityChecking();
		
		context.checking(new Expectations() {{
			one(config).getBoolean("forum.time.limited.enable", false); will(returnValue(true));

			Forum forum = new Forum();
			forum.setId(1);
			
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(propertyBag).get("forum"); will(returnValue(forum));
			one(repository).add(with(any(ForumLimitedTime.class)));
		}});

		extension.addSave(23);
		context.assertIsSatisfied();
	}

	@SuppressWarnings("serial")
	@Test
	public void delete() {
		this.securityChecking();

		final Forum forum = new Forum(23);

		context.checking(new Expectations() {{
			ForumLimitedTime forumLimitedTime = new ForumLimitedTime(){{setId(1);}};

			one(config).getBoolean("forum.time.limited.enable", false); will(returnValue(true));
			one(roleManager).isAdministrator(); will(returnValue(true));
			one(repository).getForumLimitedTime(forum); will(returnValue(forumLimitedTime));
			one(repository).remove(with(forumLimitedTime));
		}});

		extension.delete(23);
		context.assertIsSatisfied();
	}

	private void securityChecking() {
		context.checking(new Expectations() {{
			UserSession us = context.mock(UserSession.class);
			one(sessionManager).getUserSession(); will(returnValue(us));
			one(us).getRoleManager(); will(returnValue(roleManager));
			allowing(us).getUser(); will(returnValue(new User()));
		}});
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
