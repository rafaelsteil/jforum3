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
package net.jforum.plugins.shoutbox;

import java.util.List;

import net.jforum.core.hibernate.AbstractDAOTestCase;
import net.jforum.entities.Category;
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ShoutBoxDAOTestCase extends AbstractDAOTestCase<ShoutBox> {
	@Test
	@SuppressWarnings("deprecation")
	public void removeShouldDeleteShout() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutBoxDAO dao = this.newShoutBoxDAO();
		ShoutDAO shoutDAO = this.newShoutDAO();
		ShoutBox shoutBox = dao.get(1);

		Assert.assertEquals(2, shoutDAO.getAll(shoutBox).size());

		this.delete(shoutBox, dao);

		Assert.assertEquals(0, shoutDAO.getAll(shoutBox).size());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getShoutBox() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutBoxDAO dao = this.newShoutBoxDAO();
		Category category = new Category();
		category.setId(2);

		ShoutBox shoutBox = dao.getShoutBox(category);

		Assert.assertNotNull(shoutBox);
		Assert.assertEquals(new Integer(3), shoutBox.getId());
	}

	@SuppressWarnings({ "deprecation", "serial" })
	@Test
	public void getAllShoutBoxes() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutBoxDAO dao = this.newShoutBoxDAO();
		List<ShoutBox> shoutBoxes = dao.getAllShoutBoxes();

		Assert.assertEquals(2, shoutBoxes.size());
		Assert.assertTrue(shoutBoxes.contains(new ShoutBox(){{ setId(1); }}));
		Assert.assertTrue(shoutBoxes.contains(new ShoutBox(){{ setId(2); }}));
	}

	private ShoutBoxDAO newShoutBoxDAO() {
		return new ShoutBoxDAO(sessionFactory);
	}

	private ShoutDAO newShoutDAO() {
		return new ShoutDAO(sessionFactory);
	}
}
