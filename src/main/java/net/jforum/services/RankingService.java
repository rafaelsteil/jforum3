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
package net.jforum.services;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class RankingService {
	private RankingRepository repository;

	public RankingService(RankingRepository repository) {
		this.repository = repository;
	}

	/**
	 * Add a new ranking
	 * @param ranking
	 */
	public void add(Ranking ranking) {
		this.applySaveConstraints(ranking);

		if (ranking.getId() > 0) {
			throw new ValidationException("This appears to be an existing ranking (id > 0). Please use update() instead");
		}

		this.normalizeRankingInstance(ranking);
		this.repository.add(ranking);
	}

	/**
	 * Update an existing ranking
	 * @param ranking
	 */
	public void update(Ranking ranking) {
		this.applySaveConstraints(ranking);

		if (ranking.getId() == 0) {
			throw new ValidationException("update() expects a ranking with an existing id");
		}

		this.normalizeRankingInstance(ranking);
		this.repository.update(ranking);
	}

	/**
	 * Deletes a ranking
	 * @param ids
	 */
	public void delete(int... ids) {
		if (ids != null) {
			for (int id : ids) {
				Ranking ranking = this.repository.get(id);
				this.repository.remove(ranking);
			}
		}
	}

	/**
	 * A special ranking should not have a minimum number of posts
	 * @param ranking
	 */
	private void normalizeRankingInstance(Ranking ranking) {
		if (ranking.isSpecial()) {
			ranking.setMin(0);
		}
	}

	private void applySaveConstraints(Ranking ranking) {
		if (ranking == null) {
			throw new NullPointerException("Ranking to save cannot be null");
		}

		if (StringUtils.isEmpty(ranking.getTitle())) {
			throw new ValidationException("The ranking should have a name");
		}

		if (!ranking.isSpecial() && ranking.getMin() < 1) {
			throw new ValidationException("The minimum number of messages should be bigger than zero");
		}
	}
}
