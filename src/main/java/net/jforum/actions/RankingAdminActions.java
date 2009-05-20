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
package net.jforum.actions;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.RankingService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.RANKINGS_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class RankingAdminActions {
	private RankingRepository repository;
	private RankingService service;
	private ViewService viewService;
	private ViewPropertyBag propertyBag;

	public RankingAdminActions(RankingRepository repository, ViewPropertyBag propertyBag,
		ViewService viewService, RankingService service) {
		this.repository = repository;
		this.viewService = viewService;
		this.propertyBag = propertyBag;
		this.service = service;
	}

	public void list() {
		propertyBag.put("rankings", repository.getAllRankings());
	}

	public void add() {

	}

	public void addSave(@Parameter(key = "ranking") Ranking ranking) {
		service.add(ranking);
		viewService.redirectToAction(Actions.LIST);
	}

	public void edit(@Parameter(key = "rankingId") int rankingId) {
		propertyBag.put("ranking", repository.get(rankingId));
		viewService.renderView(Actions.ADD);
	}

	public void editSave(@Parameter(key = "ranking") Ranking ranking) {
		service.update(ranking);
		viewService.redirectToAction(Actions.LIST);
	}

	public void delete(@Parameter(key = "rankingsId") int... rankingsId) {
		service.delete(rankingsId);
		viewService.redirectToAction(Actions.LIST);
	}
}
