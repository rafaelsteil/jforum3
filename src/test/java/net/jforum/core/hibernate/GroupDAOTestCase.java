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

import java.util.List;

import net.jforum.entities.Group;
import net.jforum.entities.Role;
import net.jforum.repository.GroupRepository;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class GroupDAOTestCase extends AbstractDAOTestCase<Group> {
	@Test
	public void removeAllPermissions() {
		GroupRepository dao = this.newDao();
		Group group = this.newGroup();

		Role r1 = new Role(); r1.setName("r1");
		Role r2 = new Role(); r2.setName("r2");
		r2.addRoleValue(1);
		r2.addRoleValue(2);

		group.addRole(r1);
		group.addRole(r2);

		this.insert(group, dao);

		group = dao.get(group.getId());
		Assert.assertEquals(2, group.getRoles().size());

		group.getRoles().clear();
		this.update(group, dao);

		group = dao.get(group.getId());
		Assert.assertEquals(0, group.getRoles().size());
	}

	@Test
	public void clearAllRolesInsertOneNewUseJustOneUpdate() {
		GroupRepository dao = this.newDao();
		Group group = this.newGroup();

		Role role = new Role(); role.setName("r2");
		role.addRoleValue(1);
		role.addRoleValue(2);

		group.addRole(role);

		this.insert(group, dao);

		group = dao.get(group.getId());
		Assert.assertEquals(1, group.getRoles().size());

		group.getRoles().clear();
		role = new Role(); role.setName("r3");
		group.addRole(role);

		this.update(group, dao);

		group = dao.get(group.getId());
		Assert.assertEquals(1, group.getRoles().size());
		Assert.assertEquals("r3", group.getRoles().get(0).getName());
	}

	@Test
	public void addRoleWithoutRoleValue() {
		GroupRepository dao = this.newDao();
		Group group = this.newGroup();
		this.insert(group, dao);

		Role role = new Role(); role.setName("r1");
		group.addRole(role);

		this.update(group, dao);

		Group loadedGroup = dao.get(group.getId());
		Assert.assertEquals(1, loadedGroup.getRoles().size());
		Assert.assertEquals(0, loadedGroup.getRoles().get(0).getRoleValues().size());
	}

	@Test
	public void addTwoRolesOneWithRoleValues() {
		GroupRepository dao = this.newDao();
		Group group = this.newGroup();
		this.insert(group, dao);

		// Role 1
		Role role1 = new Role(); role1.setName("r1");
		role1.addRoleValue(1);
		role1.addRoleValue(5);
		role1.addRoleValue(9);

		group.addRole(role1);

		// Role 2
		Role role2 = new Role(); role2.setName("r2");
		group.addRole(role2);

		this.update(group, dao);

		Group loaded = dao.get(group.getId());

		Assert.assertEquals(2, loaded.getRoles().size());
		role1 = loaded.getRoles().get(0);

		Assert.assertEquals(3, role1.getRoleValues().size());
		Assert.assertTrue(role1.getRoleValues().contains(1));
		Assert.assertTrue(role1.getRoleValues().contains(5));
		Assert.assertTrue(role1.getRoleValues().contains(9));
		Assert.assertEquals(0, loaded.getRoles().get(1).getRoleValues().size());
	}

	@Test
	public void insert() {
		GroupRepository dao = this.newDao();
		Group group = this.newGroup();

		this.insert(group, dao);

		Assert.assertTrue(group.getId() > 0);

		Group loaded = dao.get(group.getId());

		Assert.assertEquals(group.getDescription(), loaded.getDescription());
		Assert.assertEquals(group.getName(), loaded.getName());
	}

	@Test
	public void update() {
		GroupRepository dao = this.newDao();
		Group g = this.newGroup();
		this.insert(g, dao);

		g = dao.get(g.getId());

		g.setName("changed name");
		g.setDescription("changed description");

		this.update(g, dao);

		Group loaded = dao.get(g.getId());

		Assert.assertEquals(g.getName(), loaded.getName());
		Assert.assertEquals(g.getDescription(), loaded.getDescription());
	}

	@Test
	public void delete() {
		GroupRepository dao = this.newDao();
		Group parent = this.newGroup();

		this.insert(parent, dao);

		Group loaded = dao.get(parent.getId());
		Assert.assertNotNull(loaded);

		this.delete(loaded, dao);

		Assert.assertNull(dao.get(parent.getId()));
	}

	@Test
	public void allGroups() {
		GroupRepository dao = this.newDao();

		Group g1 = new Group(); g1.setName("g1"); this.insert(g1, dao);
		Group g2 = new Group(); g1.setName("g2"); this.insert(g2, dao);
		Group g3 = new Group(); g1.setName("g3"); this.insert(g3, dao);

		List<Group> groups = dao.getAllGroups();
		Assert.assertEquals(3, groups.size());
	}

	private Group newGroup() {
		Group group = new Group();

		group.setDescription("description");
		group.setName("name");

		return group;
	}

	private GroupRepository newDao() {
		return new GroupRepository(session());
	}
}
