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

import static org.mockito.Mockito.*;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.sso.DefaultLoginAuthenticator;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTestCase {

	@Mock private UserRepository repository;
	@Mock private GroupRepository groupRepository;
	@Mock private JForumConfig config;
	@Mock private AvatarService avatarService;
	private UserService service;

	@Before
	public void setup() {
		service = new UserService(repository, groupRepository, config, new DefaultLoginAuthenticator(repository), avatarService);
	}

	@Test(expected = NullPointerException.class)
	public void updateNullUserExpectsException() {
		service.update(null, false);
	}

	@Test(expected = ValidationException.class)
	public void updateIdZeroExpectsException() {
		User user = new User(); user.setId(0);
		service.update(user, false);
	}

	@Test
	public void updateShouldChangeUsername() {
		final User current = new User();
		current.setUsername("old");
		when(repository.get(1)).thenReturn(current);

		User newUser = new User();
		newUser.setId(1);
		newUser.setUsername("new username");

		service.update(newUser, true);

		Assert.assertEquals(newUser.getUsername(), current.getUsername());
	}

	@Test
	public void updateExpectSuccess() {
		final User user = new User(); user.setId(1);
		final User current = new User();

		current.setAim(null);
		current.setAttachSignature(false);
		current.setAvatar(null);
		current.setBbCodeEnabled(false);
		current.setBiography(null);
		current.setFrom(null);
		current.setHtmlEnabled(false);
		current.setInterests(null);
		current.setLang(null);
		current.setMsn(null);
		current.setNotifyAlways(false);
		current.setOccupation(null);
		current.setViewEmailEnabled(false);
		current.setWebsite(null);
		current.setYim(null);
		current.setSignature(null);
		current.setNotifyReply(false);
		current.setNotifyPrivateMessages(false);
		current.setSmiliesEnabled(false);
		current.setNotifyText(false);

		when(repository.get(user.getId())).thenReturn(current);

		user.setAim("aim");
		user.setAttachSignature(true);
		user.setAvatar(null);
		user.setBbCodeEnabled(true);
		user.setBiography("bio");
		user.setFrom("from");
		user.setHtmlEnabled(true);
		user.setInterests("interests");
		user.setLang("lang");
		user.setMsn("msn");
		user.setNotifyAlways(true);
		user.setOccupation("occ");
		user.setViewEmailEnabled(true);
		user.setWebsite("website");
		user.setYim("yim");
		user.setSignature("signature");
		user.setNotifyReply(true);
		user.setNotifyPrivateMessages(true);
		user.setSmiliesEnabled(true);
		user.setNotifyText(true);

		service.update(user, false);

		verify(repository).update(current);
		Assert.assertEquals(user.getAim(), current.getAim());
		Assert.assertEquals(user.getAttachSignature(), current.getAttachSignature());
		Assert.assertEquals(user.isBbCodeEnabled(), current.isBbCodeEnabled());
		Assert.assertEquals(user.getBiography(), current.getBiography());
		Assert.assertEquals(user.getFrom(), current.getFrom());
		Assert.assertEquals(user.isHtmlEnabled(), current.isHtmlEnabled());
		Assert.assertEquals(user.getinterests(), current.getinterests());
		Assert.assertEquals(user.getLang(), current.getLang());
		Assert.assertEquals(user.getMsn(), current.getMsn());
		Assert.assertEquals(user.getNotifyAlways(), current.getNotifyAlways());
		Assert.assertEquals(user.getOccupation(), current.getOccupation());
		Assert.assertEquals(user.isViewEmailEnabled(), current.isViewEmailEnabled());
		Assert.assertEquals(user.getWebsite(), current.getWebsite());
		Assert.assertEquals(user.getYim(), current.getYim());
		Assert.assertEquals(user.getSignature(), current.getSignature());
		Assert.assertEquals(user.getNotifyReply(), current.getNotifyReply());
		Assert.assertEquals(user.getNotifyPrivateMessages(), current.getNotifyPrivateMessages());
		Assert.assertEquals(user.isSmiliesEnabled(), current.isSmiliesEnabled());
		Assert.assertEquals(user.getNotifyText(), current.getNotifyText());
	}

	@Test
	public void saveGroupsUsingNullIdsShouldDoNothing() {
		service.saveGroups(1, null);
	}

	@Test
	public void saveGroupsUsingZeroLengthArrayShouldDoNothing() {
		service.saveGroups(1, new int[0]);
	}

	@Test
	public void saveGroupsExpectSuccess() {
		Group g1 = new Group(); g1.setId(1);
		Group g5 = new Group(); g5.setId(5);
		Group g6 = new Group(); g6.setId(6);
		final User user = new User(); user.addGroup(g1);

		when(repository.get(1)).thenReturn(user);
		when(groupRepository.get(5)).thenReturn(g5);
		when(groupRepository.get(6)).thenReturn(g6);

		service.saveGroups(1, 5, 6);

		verify(repository).update(user);
		Assert.assertFalse(user.getGroups().contains(g1));
		Assert.assertTrue(user.getGroups().contains(g5));
		Assert.assertTrue(user.getGroups().contains(g6));
	}

	@Test
	public void validateLogin() {
		when(repository.validateLogin("user", MD5.hash("passwd"))).thenReturn(new User());

		User user = service.validateLogin("user", "passwd");

		Assert.assertNotNull(user);
	}

	@Test
	public void addWithoutRegistrationDateShouldForceValue() {
		User user = new User(); user.setUsername("u1"); user.setPassword("pwd1"); user.setEmail("email");
		user.setRegistrationDate(null);
		user.addGroup(new Group());

		service.add(user);

		Assert.assertNotNull(user.getRegistrationDate());
	}

	@Test(expected = ValidationException.class)
	public void addEmptyUsernameExpectsException() {
		User user = new User();
		user.setUsername("");
		user.setEmail("email");
		user.setPassword("pwd");

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addNullUsernameExpectsException() {
		User user = new User();
		user.setUsername(null);
		user.setEmail("email");
		user.setPassword("pwd");

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addEmptyEmailExpectsException() {
		User user = new User();
		user.setUsername("username");
		user.setEmail("");
		user.setPassword("pwd");

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addNullEmailExpectsException() {
		User user = new User();
		user.setUsername("username");
		user.setEmail(null);
		user.setPassword("pwd");

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addEmptyPasswordExpectsException() {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email");
		user.setPassword("");

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addNullPasswordExpectsException() {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email");
		user.setPassword(null);

		service.add(user);
	}

	@Test(expected = ValidationException.class)
	public void addIdBiggerThanZeroExpectsException() {
		User user = new User(); user.setUsername("username");
		user.setEmail("email"); user.setPassword("password");
		user.setId(1);

		service.add(user);
	}

	@Test(expected = NullPointerException.class)
	public void addNullUserExpectsException() {
		service.add(null);
	}

	@Test
	public void addWithoutGroupShouldUseDefault() {
		final User user = new User();
		user.setPassword("123");
		user.setUsername("username1");
		user.setEmail("email");
		user.getGroups().clear();

		when(config.getInt(ConfigKeys.DEFAULT_USER_GROUP)).thenReturn(1);
		when(groupRepository.get(1)).thenReturn(new Group());

		service.add(user);

		verify(repository).add(user);
		Assert.assertTrue(user.getGroups().size() > 0);
	}

	@Test
	public void addWithGroupExpectsSuccess() {
		final User user = new User();
		user.setPassword("123");
		user.setUsername("username1");
		user.setEmail("email");
		user.addGroup(new Group());

		service.add(user);

		verify(repository).add(user);
	}
}
