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

import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SmilieDAOTestCase extends AbstractDAOTestCase<Smilie> {
	@Test
	public void getAllSmiliesExpectTwoResults() {
		SmilieRepository dao = this.newDao();

		this.insert(this.createSmilie("[b1]", "diskname1"), dao);
		this.insert(this.createSmilie("[b1]", "diskname2"), dao);

		List<Smilie> smilies = dao.getAllSmilies();

		Assert.assertNotNull(smilies);
		Assert.assertEquals(2, smilies.size());
	}

	@Test
	public void insert() {
		SmilieRepository dao = this.newDao();
		Smilie s = this.createSmilie("x", "diskname");
		this.insert(s, dao);

		Assert.assertTrue(s.getId() > 0);

		Smilie loaded = dao.get(s.getId());

		Assert.assertNotNull(loaded);
		Assert.assertEquals("x", loaded.getCode());
		Assert.assertEquals("diskname", loaded.getDiskName());
	}

	@Test
	public void getAllSmiliesExpectEmtpyList() {
		SmilieRepository dao = this.newDao();
		Assert.assertEquals(0, dao.getAllSmilies().size());
	}

	private SmilieRepository newDao() {
		return new SmilieRepository(session());
	}

	private Smilie createSmilie(String code, String diskName) {
		Smilie s = new Smilie();

		s.setCode(code);
		s.setDiskName(diskName);

		return s;
	}
}
