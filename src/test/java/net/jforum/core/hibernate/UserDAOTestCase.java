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
package net.jforum.core.hibernate;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class UserDAOTestCase extends AbstractDAOTestCase<User> {
	@Test
	@SuppressWarnings("deprecation")
	@Ignore("seam to be a problem with hsqldb commit, work fine with other connector")
	public void changeAllowAvatarState() {
		new JDBCLoader(this.session()).run("/userdao/changeAllowAvatarState.sql");

		GroupRepository groupDao = this.newGroupDao();
		UserRepository dao = this.newDao();

		Group group1 = groupDao.get(1);
		Group group2 = groupDao.get(2);

		List<User> users = dao.getAllUsers(0, 10, Arrays.asList(group1));
		Assert.assertFalse(users.get(0).isAvatarEnabled());
		Assert.assertFalse(users.get(1).isAvatarEnabled());

		dao.changeAllowAvatarState(true, group1);
		this.commit();
		this.beginTransaction();

		users = dao.getAllUsers(0, 10, Arrays.asList(group1));
		Assert.assertEquals(2, users.size());
		Assert.assertTrue(users.get(0).isAvatarEnabled());
		Assert.assertTrue(users.get(1).isAvatarEnabled());

		Assert.assertFalse(dao.getAllUsers(0, 10, Arrays.asList(group2)).get(0).isAvatarEnabled());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void findByUsernameFilteringByGroup() {
		new JDBCLoader(this.session()).run("/userdao/findByUsernameFilteringByGroup.sql");

		UserRepository dao = this.newDao();
		User user = dao.get(1);

		List<User> users = dao.findByUserName("user", user.getGroups());
		Assert.assertEquals(2, users.size());

		Assert.assertEquals("USER1", users.get(0).getUsername());
		Assert.assertEquals("user2", users.get(1).getUsername());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getAllUsersFilteringByGroup() {
		new JDBCLoader(this.session()).run("/userdao/getAllUsersFilteringByGroup.sql");

		UserRepository dao = this.newDao();
		User user = dao.get(1);

		List<User> users = dao.getAllUsers(0, 10, user.getGroups());
		Assert.assertEquals(2, users.size());

		Assert.assertEquals("u1", users.get(0).getUsername());
		Assert.assertEquals("u2", users.get(1).getUsername());
	}

	@Test
	public void getByEmailShouldFindAMatch() {
		User user = new User();
		user.setUsername("user1");
		user.setEmail("email1");

		UserRepository dao = this.newDao();
		this.insert(user, dao);

		user = dao.getByEmail("email1");
		Assert.assertNotNull(user);
		Assert.assertEquals("user1", user.getUsername());
	}

	@Test
	public void validateLostPasswordHashUsingBadDataExpectFail() {
		UserRepository dao = this.newDao();
		User user = dao.validateLostPasswordHash("bad username", "bad hash");
		Assert.assertNull(user);
	}

	@Test
	public void validateLostPasswordHashUsingGoodDataExpectSuccess() {
		UserRepository dao = this.newDao();

		User user = new User();
		user.setUsername("rafael");
		user.setActivationKey("act key 1");

		this.insert(user, dao);

		user = dao.validateLostPasswordHash("rafael", "act key 1");
		Assert.assertNotNull(user);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTotalPostsExpectTwoResults() {
		new JDBCLoader(session())
			.run("/userdao/getTotalPostsExpectTwoResults.sql");
		User user = new User(); user.setId(1);
		int total = this.newDao().getTotalPosts(user);
		Assert.assertEquals(2, total);
	}

	@Test
	public void isUsernaneAvailableTestingUsernameExpectFalse() {
		User user = new User(); user.setUsername("username1"); user.setEmail("email1");
		UserRepository dao = this.newDao();
		this.insert(user, dao);
		Assert.assertFalse(dao.isUsernameAvailable("UserNaMe1", "email2"));
	}

	@Test
	public void isUsernaneAvailableTestingEmailExpectFalse() {
		User user = new User(); user.setUsername("username1"); user.setEmail("email1");
		UserRepository dao = this.newDao();
		this.insert(user, dao);
		Assert.assertFalse(dao.isUsernameAvailable("UserNaMe2", "eMAil1"));
	}

	@Test
	public void isUsernaneAvailableExpectTrue() {
		User user = new User(); user.setUsername("username1"); user.setEmail("email1");
		UserRepository dao = this.newDao();
		this.insert(user, dao);
		Assert.assertTrue(dao.isUsernameAvailable("UserNaMe2", "email2"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getTotalUnreadPrivateMessages() {
		new JDBCLoader(session())
			.run("/userdao/getTotalUnreadPrivateMessages.sql");
		User user = new User(); user.setId(1);
		Assert.assertEquals(1, this.newDao().getTotalUnreadPrivateMessages(user));
	}

	@Test
	public void findByUserNameExpectThreeResults() {
		User user1 = new User(); user1.setUsername("Amy Winehouse");
		User user2 = new User(); user2.setUsername("John Amy");
		User user3 = new User(); user3.setUsername("Something With Amy inside");
		User user4 = new User(); user4.setUsername("another username");

		UserRepository dao = this.newDao();

		this.insert(user1, dao);
		this.insert(user3, dao);
		this.insert(user4, dao);
		this.insert(user2, dao);

		List<User> users = dao.findByUserName("amy");
		Assert.assertEquals(3, users.size());
		Assert.assertEquals("Amy Winehouse", users.get(0).getUsername());
		Assert.assertEquals("John Amy", users.get(1).getUsername());
		Assert.assertEquals("Something With Amy inside", users.get(2).getUsername());
	}

	@Test
	public void userGroupsCascade() {
		User user = new User(); user.setUsername("u1");
		Group g = new Group(); g.setName("g1");
		this.newGroupDao().add(g);

		user.addGroup(g);

		UserRepository dao = this.newDao();
		this.insert(user, dao);

		user = dao.get(user.getId());
		Assert.assertEquals(1, user.getGroups().size());
	}

	@Test
	public void userGroupsCascadeCleanThenAddNewExpectDeletesAndInsertsOk() {
		User user = new User();
		user.setUsername("u1");

		Group g = new Group(); g.setName("g1");
		Group g2 = new Group(); g2.setName("g2");
		Group g3 = new Group(); g3.setName("g3");

		GroupRepository groupDao = this.newGroupDao();
		groupDao.add(g);
		groupDao.add(g2);
		groupDao.add(g3);

		this.commit();
		this.beginTransaction();

		user.addGroup(g);
		user.addGroup(g2);

		UserRepository dao = this.newDao();
		this.insert(user, dao);

		user = dao.get(user.getId());
		Assert.assertEquals(2, user.getGroups().size());
		Assert.assertTrue(user.getGroups().contains(g));
		Assert.assertTrue(user.getGroups().contains(g2));

		user.getGroups().remove(g);
		user.addGroup(g3);

		this.update(user, dao);

		user = dao.get(user.getId());
		Assert.assertEquals(2, user.getGroups().size());
		Assert.assertTrue(user.getGroups().contains(g2));
		Assert.assertTrue(user.getGroups().contains(g3));
	}

	@Test
	public void validateLoginUsingInvalidCredentialsExpectsInvalidLogin() {
		User user = new User();
		user.setUsername("username1");
		user.setPassword("password1");

		UserRepository dao = this.newDao();
		this.insert(user, dao);

		Assert.assertNull(dao.validateLogin("a", "b"));
	}

	@Test
	public void validateLoginUsingGoodCredentialsExpectsSuccess() {
		User user = new User();
		user.setUsername("username2");
		user.setPassword("password2");

		UserRepository dao = this.newDao();
		this.insert(user, dao);

		Assert.assertNotNull(dao.validateLogin("username2", "password2"));
	}

	@Test
	public void addExpectsSuccess() {
		User user = new User();
		user.setUsername("u1");
		user.setPassword("pwd1");
		user.setEmail("email1");

		UserRepository dao = this.newDao();

		this.insert(user, dao);

		Assert.assertTrue(user.getId() > 0);

		User loaded = dao.get(user.getId());
		Assert.assertEquals(user.getUsername(), loaded.getUsername());
		Assert.assertEquals(user.getPassword(), loaded.getPassword());
		Assert.assertEquals(user.getEmail(), loaded.getEmail());
	}

	@Test
	public void getByUsernameUsingInexistentValueExpectsNull() {
		UserRepository dao = this.newDao();
		Assert.assertNull(dao.getByUsername("non existent username"));
	}

	@Test
	public void getByUsernameExpectsValidUser() {
		UserRepository dao = this.newDao();
		User u = new User(); u.setUsername("usernameX"); this.insert(u, dao);
		User u2 = dao.getByUsername("usernameX");
		Assert.assertNotNull(u2);
		Assert.assertEquals(u.getUsername(), u2.getUsername());
	}

	@Test
	public void geTotalUsers() {
		UserRepository dao = this.newDao();
		User u1 = new User(); u1.setUsername("u1"); this.insert(u1, dao);
		User u2 = new User(); u2.setUsername("u1"); this.insert(u2, dao);
		User u3 = new User(); u3.setUsername("u1"); this.insert(u3, dao);

		Assert.assertEquals(3, dao.getTotalUsers());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getLastRegisteredUser() {
		UserRepository dao = this.newDao();
		User u1 = new User(); u1.setUsername("u1"); u1.setRegistrationDate(new GregorianCalendar(2008, 3, 19, 20, 03, 10).getTime()); this.insert(u1, dao);
		User u2 = new User(); u2.setUsername("u2"); u2.setRegistrationDate(new GregorianCalendar(2008, 3, 5, 7, 19, 10).getTime()); this.insert(u2, dao);

		User lastRegisteredUser = dao.getLastRegisteredUser();

		Assert.assertNotNull(lastRegisteredUser);
		Assert.assertEquals(u1.getUsername(), lastRegisteredUser.getUsername());
		Assert.assertEquals(u1.getId(), lastRegisteredUser.getId());
	}

	@Test
	public void listExpectEmptyList() {
		UserRepository dao = this.newDao();
		Assert.assertEquals(0, dao.getAllUsers(0, 10).size());
	}

	@Test
	public void listExpectTwoRecords() {
		UserRepository dao = this.newDao();

		User u1 = new User(); u1.setUsername("u1"); this.insert(u1, dao);
		User u2 = new User(); u1.setUsername("u2"); this.insert(u2, dao);

		List<User> users = dao.getAllUsers(0, 10);

		Assert.assertEquals(2, users.size());
		Assert.assertTrue(users.contains(u1));
		Assert.assertTrue(users.contains(u2));
	}

	private UserRepository newDao() {
		return new UserRepository(session());
	}

	private GroupRepository newGroupDao() {
		return new GroupRepository(session());
	}
}

