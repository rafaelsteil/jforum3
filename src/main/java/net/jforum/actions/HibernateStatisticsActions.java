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

import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ViewService;

import org.hibernate.SessionFactory;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;

/**
 * @author Rafael Steil
 */
@Component(Domain.HIBERNATE)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class HibernateStatisticsActions {
	private SessionFactory sessionFactory;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;

	public HibernateStatisticsActions(SessionFactory sessionFactory, ViewPropertyBag propertyBag,
		ViewService viewService) {
		this.sessionFactory = sessionFactory;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
	}

	public void list() {
		boolean statsEnabled = this.sessionFactory.getStatistics().isStatisticsEnabled();

		if (!statsEnabled) {
			this.viewService.renderView("statsDisabled");
		}
		else {
			this.propertyBag.put("stats", this.sessionFactory.getStatistics());
		}
	}
}
