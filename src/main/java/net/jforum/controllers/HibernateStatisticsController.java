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
import net.jforum.security.AdministrationRule;

import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.HIBERNATE)
// @InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class HibernateStatisticsController {
	private SessionFactory sessionFactory;
	private final Result result;

	public HibernateStatisticsController(SessionFactory sessionFactory,
			Result result) {
		this.sessionFactory = sessionFactory;
		this.result = result;
	}

	public void list() {
		boolean statsEnabled = this.sessionFactory.getStatistics()
				.isStatisticsEnabled();

		if (!statsEnabled) {
			this.result.forwardTo("statsDisabled");
		} else {
			this.result.include("stats", this.sessionFactory.getStatistics());
		}
	}
}
