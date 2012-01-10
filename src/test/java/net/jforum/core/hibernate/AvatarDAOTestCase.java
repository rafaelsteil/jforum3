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

import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.repository.AvatarRepository;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class AvatarDAOTestCase extends AbstractDAOTestCase<Avatar> {
	@Test
	public void getAllSmiliesExpectTwoResults() {
		AvatarRepository dao = this.newDao();

		this.insert(this.createAvatar(120,150, "diskname1"), dao);
		this.insert(this.createAvatar(130,140, "diskname2"), dao);

		List<Avatar> avatars = dao.getAll();

		Assert.assertNotNull(avatars);
		Assert.assertEquals(2, avatars.size());
	}

	@Test
	public void insert() {
		AvatarRepository dao = this.newDao();
		Avatar s = this.createAvatar(120,150, "diskname");
		this.insert(s, dao);

		Assert.assertTrue(s.getId() > 0);

		Avatar loaded = dao.get(s.getId());

		Assert.assertNotNull(loaded);
		Assert.assertEquals(new Integer(120), loaded.getWidth());
		Assert.assertEquals(new Integer(150), loaded.getHeight());
		Assert.assertEquals("diskname", loaded.getFileName());
	}

	@Test
	public void getAllAvatarsExpectEmtpyList() {
		AvatarRepository dao = this.newDao();
		Assert.assertEquals(0, dao.getAll().size());
	}

	private AvatarRepository newDao() {
		return new AvatarRepository(session());
	}

	private Avatar createAvatar(Integer width,Integer height, String diskName) {
		Avatar a = new Avatar();

		a.setAvatarType(AvatarType.AVATAR_GALLERY);
		a.setHeight(height);
		a.setWidth(width);
		a.setFileName(diskName);

		return a;
	}
}
