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
		propertyBag.put("shoutboxes", shoutBoxRepository.getAllShoutBoxes());
	}

	public void edit(@Parameter(key = "shoutBoxId") int shoutBoxId){
		ShoutBox shoutbox = shoutBoxService.get(shoutBoxId);

		if (sessionManager.getUserSession().getRoleManager().isCategoryAllowed(shoutbox.getCategory())) {
			propertyBag.put("shoutbox", shoutbox);
		}
		else {
			viewService.redirectToAction(Actions.LIST);
		}
	}

	public void editSave(@Parameter(key = "shoutbox") ShoutBox shoutbox){
		ShoutBox current = shoutBoxRepository.get(shoutbox.getId());

		if (sessionManager.getUserSession().getRoleManager().isCategoryAllowed(current.getCategory())) {
			shoutBoxService.update(shoutbox);
		}

		viewService.redirectToAction(Actions.LIST);
	}
}
