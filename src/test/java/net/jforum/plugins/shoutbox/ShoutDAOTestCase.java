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
import net.jforum.util.JDBCLoader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ShoutDAOTestCase extends AbstractDAOTestCase<ShoutBox> {

	@SuppressWarnings({ "deprecation", "serial" })
	@Test
	public void getAll() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutDAO dao = this.newShoutDAO();
		List<Shout> shouts = dao.getAll();

		Assert.assertEquals(3, shouts.size());
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(1); }}));
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(2); }}));
	}

	@SuppressWarnings({ "deprecation", "serial" })
	@Test
	public void getAllByShoutBox() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutDAO dao = this.newShoutDAO();
		ShoutBox shoutBox = new ShoutBox();
		shoutBox.setId(1);

		List<Shout> shouts = dao.getAll(shoutBox);

		Assert.assertEquals(2, shouts.size());
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(1); }}));
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(2); }}));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getMyLastShout() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutDAO dao = this.newShoutDAO();
		Shout shout = dao.getMyLastShout("127.0.0.1");

		Assert.assertNotNull(shout);
		Assert.assertEquals(new Integer(3), shout.getId());
	}

	@Test
	@SuppressWarnings({ "deprecation", "serial" })
	public void getShoutShouldReturnAllIfMaxIs0SinceLastId() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutDAO dao = this.newShoutDAO();
		ShoutBox shoutBox = new ShoutBox();
		shoutBox.setId(1);

		List<Shout> shouts = dao.getShout(0, shoutBox, 0);

		Assert.assertEquals(2, shouts.size());
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(1); }}));
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(2); }}));
	}

	@Test
	@SuppressWarnings({ "deprecation", "serial" })
	public void getShout() {
		new JDBCLoader(sessionFactory.getCurrentSession().connection())
			.run("/shoutboxdao/dump.sql");

		ShoutDAO dao = this.newShoutDAO();
		ShoutBox shoutBox = new ShoutBox();
		shoutBox.setId(1);

		List<Shout> shouts = dao.getShout(1, shoutBox, 1);

		Assert.assertEquals(1, shouts.size());
		Assert.assertTrue(shouts.contains(new Shout(){{ setId(2); }}));
	}

	private ShoutDAO newShoutDAO() {
		return new ShoutDAO(sessionFactory);
	}
}
