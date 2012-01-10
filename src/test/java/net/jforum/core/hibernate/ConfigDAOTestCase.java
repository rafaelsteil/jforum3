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

import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Rafael Steil
 */
public class ConfigDAOTestCase extends AbstractDAOTestCase<Config> {
	@Test
	public void update() {
		ConfigRepository dao = this.newDao();
		Config c = this.newConfig("k1", "v1"); this.insert(c, dao);
		c = dao.get(c.getId());
		c.setValue("new value");
		this.update(c, dao);
		Config loaded = dao.get(c.getId());
		Assert.assertEquals("k1", loaded.getName());
		Assert.assertEquals("new value", loaded.getValue());
	}

	@Test
	public void getByNameUsingInvalidKeyExpectsNul() {
		ConfigRepository dao = this.newDao();
		Assert.assertNull(dao.getByName("some invalid key name"));
	}

	@Test
	public void getByNameExpectSuccess() {

		ConfigRepository dao = this.newDao();
		Config c = this.newConfig("k1", "v1"); this.insert(c, dao);
		Config loaded = dao.getByName("k1");
		Assert.assertNotNull(loaded);
		Assert.assertEquals(c.getId(), loaded.getId());
		Assert.assertEquals(c.getName(), loaded.getName());
		Assert.assertEquals(c.getValue(), loaded.getValue());
	}

	@Test
	public void insert() {
		ConfigRepository dao = this.newDao();
		Config c = this.newConfig("name1", "value1"); this.insert(c, dao);

		Assert.assertTrue(c.getId() > 0);

		Config loaded = dao.get(c.getId());
		Assert.assertEquals(c.getName(), loaded.getName());
		Assert.assertEquals(c.getValue(), loaded.getValue());
	}

	private Config newConfig(String name, String value) {
		Config c = new Config();

		c.setName(name);
		c.setValue(value);

		return c;
	}

	private ConfigRepository newDao() {
		return new ConfigRepository(session());
	}
}
