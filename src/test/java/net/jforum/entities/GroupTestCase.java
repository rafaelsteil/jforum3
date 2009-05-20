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
package net.jforum.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class GroupTestCase {
	@Test
	public void roleExistNotFoundExpectFalse() {
		Role role = new Role();
		role.setName("role1");

		Group g = new Group();
		g.addRole(role);

		Assert.assertFalse(g.roleExist("role2"));
	}

	@Test
	public void roleExistEntryIsFoundExpectTrue() {
		Role role = new Role();
		role.setName("role1");

		Group g = new Group();
		g.addRole(role);

		Assert.assertTrue(g.roleExist("role1"));
	}
}
