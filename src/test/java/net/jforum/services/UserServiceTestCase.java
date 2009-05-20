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

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.sso.DefaultLoginAuthenticator;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class UserServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private UserRepository repository = context.mock(UserRepository.class);
	private GroupRepository groupRepository = context.mock(GroupRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private AvatarService avatarService = context.mock(AvatarService.class);
	private UserService service = new UserService(repository, groupRepository, config,
		new DefaultLoginAuthenticator(repository), avatarService);

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

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(current));
			allowing(repository);
		}});

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

		context.checking(new Expectations() {{
			one(repository).get(user.getId()); will(returnValue(current));
			one(repository).update(current);
		}});

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
		context.assertIsSatisfied();

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
		context.checking(new Expectations() {{

		}});

		service.saveGroups(1, null);
		context.assertIsSatisfied();
	}

	@Test
	public void saveGroupsUsingZeroLengthArrayShouldDoNothing() {
		context.checking(new Expectations() {{

		}});

		service.saveGroups(1, new int[0]);
		context.assertIsSatisfied();
	}

	@Test
	public void saveGroupsExpectSuccess() {
		Group g1 = new Group(); g1.setId(1);
		final User user = new User(); user.addGroup(g1);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(user));

			Group g5 = new Group(); g5.setId(5);
			Group g6 = new Group(); g6.setId(6);

			one(groupRepository).get(5); will(returnValue(g5));
			one(groupRepository).get(6); will(returnValue(g6));

			one(repository).update(user);
		}});

		service.saveGroups(1, 5, 6);
		context.assertIsSatisfied();

		Assert.assertFalse(user.getGroups().contains(g1));

		Group g5 = new Group(); g5.setId(5);
		Group g6 = new Group(); g6.setId(6);

		Assert.assertTrue(user.getGroups().contains(g5));
		Assert.assertTrue(user.getGroups().contains(g6));
	}

	@Test
	public void validateLogin() {
		context.checking(new Expectations() {{
			one(repository).validateLogin("user", MD5.hash("passwd")); will(returnValue(new User()));
		}});

		User user = service.validateLogin("user", "passwd");
		context.assertIsSatisfied();
		Assert.assertNotNull(user);
	}

	@Test
	public void addWithoutRegistrationDateShouldForceValue() {
		User user = new User(); user.setUsername("u1"); user.setPassword("pwd1"); user.setEmail("email");
		user.setRegistrationDate(null);
		user.addGroup(new Group());

		context.checking(new Expectations() {{
			ignoring(repository);
		}});

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

		context.checking(new Expectations() {{
			one(config).getInt(ConfigKeys.DEFAULT_USER_GROUP); will(returnValue(1));
			one(groupRepository).get(1); will(returnValue(new Group()));
			one(repository).add(user);
		}});

		service.add(user);
		context.assertIsSatisfied();
		Assert.assertTrue(user.getGroups().size() > 0);
	}

	@Test
	public void addWithGroupExpectsSuccess() {
		final User user = new User();
		user.setPassword("123");
		user.setUsername("username1");
		user.setEmail("email");
		user.addGroup(new Group());

		context.checking(new Expectations() {{
			one(repository).add(user);
		}});

		service.add(user);

		context.assertIsSatisfied();
	}
}
