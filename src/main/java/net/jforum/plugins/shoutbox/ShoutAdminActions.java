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
package net.jforum.plugins.shoutbox;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Bill
 *
 */
@Component("adminShout")
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ShoutAdminActions {
	private ViewService viewService;
	private ViewPropertyBag propertyBag;
	private ShoutRepository repository;
	public ShoutAdminActions(ViewPropertyBag propertyBag,
			ShoutRepository repository, ViewService viewService) {
		super();
		this.propertyBag = propertyBag;
		this.repository = repository;
		this.viewService = viewService;
	}

	public void delete(@Parameter(key = "ShoutID") int... shoutId) {
		if (shoutId != null) {
			for (int id : shoutId) {
				Shout shout = this.repository.get(id);
				this.repository.remove(shout);
			}
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	public void list() {
		this.propertyBag.put("shouts", this.repository.getAll());
	}

	public void add() {

	}

	public void addSave(@Parameter(key = "shout") Shout shout) {
		this.repository.add(shout);
		this.viewService.redirectToAction(Actions.LIST);
	}

	public void edit(@Parameter(key = "id") int id) {
		Shout shout = this.repository.get(id);
		this.propertyBag.put("shout", shout);
		this.viewService.renderView(Actions.ADD);
	}

	public void editSave(@Parameter(key = "shout") Shout shout) {
		this.repository.update(shout);
		this.viewService.redirectToAction(Actions.LIST);
	}
}
