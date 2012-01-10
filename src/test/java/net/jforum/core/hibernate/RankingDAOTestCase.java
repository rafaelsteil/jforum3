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

import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RankingDAOTestCase extends AbstractDAOTestCase<Ranking> {
	@Test
	public void delete() {
		RankingRepository dao = this.newDao();
		Ranking r = this.createRanking("image1", "title1", 7, false);

		this.insert(r, dao);
		this.delete(r, dao);

		Assert.assertNull(dao.get(r.getId()));
	}

	@Test
	public void update() {
		RankingRepository dao = this.newDao();

		Ranking r = this.createRanking("image1", "title1", 7, false);

		this.insert(r, dao);

		Ranking loaded = dao.get(r.getId());
		loaded.setImage("image 1.1");
		loaded.setTitle("title1.1");
		loaded.setMin(8);
		loaded.setSpecial(true);

		this.update(loaded, dao);

		loaded = dao.get(r.getId());

		Assert.assertEquals("image 1.1", loaded.getImage());
		Assert.assertEquals("title1.1", loaded.getTitle());
		Assert.assertEquals(8, loaded.getMin());
		Assert.assertEquals(true, loaded.isSpecial());
	}

	@Test
	public void getAllRankingsExpectEmptyList() {
		RankingRepository dao = this.newDao();
		List<Ranking> rankings = dao.getAllRankings();
		Assert.assertNotNull(rankings);
		Assert.assertEquals(0, rankings.size());
	}

	@Test
	public void getAllRankingsExpectTwoRecordsOrderedAsc() {
		RankingRepository dao = this.newDao();

		this.insert(this.createRanking("img", "r1", 1, false), dao);
		this.insert(this.createRanking("img2", "r2", 2, false), dao);

		List<Ranking> rankings = dao.getAllRankings();

		Assert.assertEquals(2, rankings.size());
		Assert.assertEquals("r1", rankings.get(0).getTitle());
		Assert.assertEquals("r2", rankings.get(1).getTitle());
	}

	@Test
	public void insert() {
		RankingRepository dao = this.newDao();

		Ranking r = this.createRanking("some image", "ranking title", 10, true);

		this.insert(r, dao);

		Assert.assertTrue(r.getId() > 0);

		Ranking loaded = dao.get(r.getId());
		Assert.assertEquals("some image", loaded.getImage());
		Assert.assertEquals("ranking title", loaded.getTitle());
		Assert.assertEquals(10, loaded.getMin());
		Assert.assertEquals(true, loaded.isSpecial());
	}

	private RankingRepository newDao() {
		return new RankingRepository(session());
	}

	private Ranking createRanking(String image, String title, int min, boolean special) {
		Ranking r = new Ranking();

		r.setImage(image);
		r.setMin(min);
		r.setSpecial(special);
		r.setTitle(title);

		return r;
	}
}
