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
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Bill
 */

@Component(Domain.SHOUTBOX_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ShoutBoxAdminActions {
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private ShoutBoxService shoutBoxService;
	private ShoutBoxRepository shoutBoxRepository;
	private SessionManager sessionManager;

	public ShoutBoxAdminActions(ViewPropertyBag propertyBag, ShoutBoxRepository shoutBoxRepository,
			ShoutBoxService shoutBoxService, ViewService viewService, SessionManager sessionManager) {
		this.propertyBag = propertyBag;
		this.shoutBoxRepository = shoutBoxRepository;
		this.shoutBoxService = shoutBoxService;
		this.viewService = viewService;
		this.sessionManager = sessionManager;
	}

	/**
	 * List all existing categories
	 */
	public void list() {
		this.propertyBag.put("shoutboxes", this.shoutBoxRepository.getAllShoutBoxes());
	}

	public void edit(@Parameter(key = "shoutBoxId") int shoutBoxId){
		ShoutBox shoutbox = shoutBoxService.get(shoutBoxId);

		if (this.sessionManager.getUserSession().getRoleManager().isCategoryAllowed(shoutbox.getCategory().getId())) {
			this.propertyBag.put("shoutbox", shoutbox);
		}
		else {
			this.viewService.redirectToAction(Actions.LIST);
		}
	}

	public void editSave(@Parameter(key = "shoutbox") ShoutBox shoutbox){
		ShoutBox current = this.shoutBoxRepository.get(shoutbox.getId());

		if (this.sessionManager.getUserSession().getRoleManager().isCategoryAllowed(current.getCategory().getId())) {
			shoutBoxService.update(shoutbox);
		}

		viewService.redirectToAction(Actions.LIST);
	}
}
