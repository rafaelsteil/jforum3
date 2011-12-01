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
package net.jforum.controllers;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.RankingService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.RANKINGS_ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class RankingAdminController {
	private RankingRepository repository;
	private RankingService service;
	private final Result result;

	public RankingAdminController(RankingRepository repository,
			RankingService service, Result result) {
		this.repository = repository;
		this.service = service;
		this.result = result;
	}

	public void list() {
		this.result.include("rankings", this.repository.getAllRankings());
	}

	public void add() {

	}

	public void addSave(Ranking ranking) {
		this.service.add(ranking);
		this.result.redirectTo(this).list();
	}

	public void edit(int rankingId) {
		this.result.include("ranking", this.repository.get(rankingId));
		this.result.forwardTo(this).add();
	}

	public void editSave(Ranking ranking) {
		this.service.update(ranking);
		this.result.redirectTo(this).list();
	}

	public void delete(int... rankingsId) {
		this.service.delete(rankingsId);
		this.result.redirectTo(this).list();
	}
}
