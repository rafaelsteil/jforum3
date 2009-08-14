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
import net.jforum.repository.BanlistRepository;
import net.jforum.security.AdministrationRule;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;

/**
 * @author Rafael Steil
 */
@Component(Domain.BANNING_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class BanlistAdminActions {
	private BanlistRepository repository;
	private ViewPropertyBag propertyBag;

	public BanlistAdminActions(BanlistRepository repository,
			ViewPropertyBag propertyBag) {
		this.repository = repository;
		this.propertyBag = propertyBag;
	}

	public void list() {
		this.propertyBag.put("banlist", this.repository.getAllBanlists());
	}
}
