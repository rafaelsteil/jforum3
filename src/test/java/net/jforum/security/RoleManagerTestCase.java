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
package net.jforum.security;

import java.util.Arrays;
import java.util.List;

import net.jforum.entities.Group;
import net.jforum.entities.Role;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RoleManagerTestCase {
	@Test
	public void singleRoleExists() {
		Group g = new Group(); g.addRole(this.newRole("role1"));
		RoleManager manager = new RoleManager(); manager.setGroups(Arrays.asList(g));
		Assert.assertTrue(manager.roleExists("role1"));
	}

	@Test
	public void roleWithValuesExists() {
		Group g = new Group(); g.addRole(this.newRole("role1", Arrays.asList(1, 2)));
		RoleManager manager = new RoleManager(); manager.setGroups(Arrays.asList(g));
		Assert.assertTrue(manager.roleExists("role1"));
		Assert.assertTrue(manager.roleExists("role1", 1));
		Assert.assertTrue(manager.roleExists("role1", 2));
		Assert.assertFalse(manager.roleExists("role1", 3));
	}

	@Test
	public void twoGroupsSameRoleWithDifferentValuesShouldMerge() {
		Group g1 = new Group(); g1.addRole(this.newRole("role1", Arrays.asList(1, 2)));
		Group g2 = new Group(); g2.addRole(this.newRole("role1", Arrays.asList(2, 3, 4)));
		RoleManager manager = new RoleManager(); manager.setGroups(Arrays.asList(g1, g2));
		Assert.assertTrue(manager.roleExists("role1"));
		Assert.assertTrue(manager.roleExists("role1", 1));
		Assert.assertTrue(manager.roleExists("role1", 2));
		Assert.assertTrue(manager.roleExists("role1", 3));
		Assert.assertTrue(manager.roleExists("role1", 4));
	}

	@Test
	public void twoGroupsUniqueRoles() {
		Group g1 = new Group(); g1.addRole(this.newRole("role1"));
		Group g2 = new Group(); g2.addRole(this.newRole("role2"));
		RoleManager manager = new RoleManager(); manager.setGroups(Arrays.asList(g1, g2));
		Assert.assertTrue(manager.roleExists("role1"));
		Assert.assertTrue(manager.roleExists("role2"));
	}

	@Test
	public void twoGroupsSameRoleShouldExist() {
		Group g1 = new Group(); g1.addRole(this.newRole("role1"));
		Group g2 = new Group(); g2.addRole(this.newRole("role1"));
		RoleManager manager = new RoleManager(); manager.setGroups(Arrays.asList(g1, g2));
		Assert.assertTrue(manager.roleExists("role1"));
	}

	private Role newRole(String name, List<Integer> values) {
		Role role = new Role();
		role.setName(name);
		role.getRoleValues().addAll(values);
		return role;
	}

	private Role newRole(String name) {
		Role role = new Role();
		role.setName(name);
		return role;
	}
}
