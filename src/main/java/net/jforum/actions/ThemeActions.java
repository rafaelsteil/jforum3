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
import net.jforum.entities.Theme;
import net.jforum.repository.ThemeRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.THEMES_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ThemeActions {
	private final ViewPropertyBag propertyBag;
	private final ViewService viewService;
	private final ThemeRepository repository;

	public ThemeActions(ViewPropertyBag propertyBag, ViewService viewService, ThemeRepository repository) {
		this.propertyBag = propertyBag;
		this.viewService = viewService;
		this.repository = repository;
	}

	public void list() {
		propertyBag.put("themes", repository.getAll());
	}

	public void add() {

	}

	public void addSave(@Parameter(key = "theme") Theme theme) {
		repository.add(theme);
		viewService.redirectToAction(Actions.LIST);
	}

	public void edit(@Parameter(key = "themeId") int themeId) {
		propertyBag.put("theme", repository.get(themeId));
	}

	public void editSave(@Parameter(key = "theme") Theme theme) {
		repository.update(theme);
		viewService.redirectToAction(Actions.LIST);
	}

	public void delete(@Parameter(key = "themeId") int themeId) {
		Theme theme = repository.get(themeId);
		repository.remove(theme);
		viewService.redirectToAction(Actions.LIST);
	}
}
