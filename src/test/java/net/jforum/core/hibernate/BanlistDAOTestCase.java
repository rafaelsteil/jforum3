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

import net.jforum.entities.Banlist;
import net.jforum.repository.BanlistRepository;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class BanlistDAOTestCase extends AbstractDAOTestCase<Banlist> {
	@Test
	public void getAllBanlistsExpectEmptyList() {
		BanlistRepository dao = this.newDao();
		List<Banlist> banlists = dao.getAllBanlists();
		Assert.assertNotNull(banlists);
		Assert.assertEquals(0, banlists.size());
	}

	@Test
	public void getAllBanlistsExpectTwoRecords() {
		BanlistRepository dao = this.newDao();

		Banlist b1 = new Banlist(); b1.setEmail("email@1"); this.insert(b1, dao);
		Banlist b2 = new Banlist(); b2.setUserId(3); this.insert(b2, dao);

		List<Banlist> banlists = dao.getAllBanlists();

		Assert.assertEquals(2, banlists.size());
		Assert.assertEquals("email@1", banlists.get(0).getEmail());
		Assert.assertEquals(3, banlists.get(1).getUserId());
	}

	private BanlistRepository newDao() {
		return new BanlistRepository(session());
	}
}
