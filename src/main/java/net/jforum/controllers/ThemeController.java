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
import net.jforum.entities.Theme;
import net.jforum.repository.ThemeRepository;
import net.jforum.security.AdministrationRule;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.THEMES_ADMIN)
// @InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ThemeController {
	private final ThemeRepository repository;
	private final Result result;

	public ThemeController(Result result, ThemeRepository repository) {
		this.result = result;
		this.repository = repository;
	}

	public void list() {
		this.result.include("themes", this.repository.getAll());
	}

	public void add() {

	}

	public void addSave(Theme theme) {
		this.repository.add(theme);
		this.result.redirectTo(this).list();
	}

	public void edit(int themeId) {
		this.result.include("theme", this.repository.get(themeId));
	}

	public void editSave(Theme theme) {
		this.repository.update(theme);
		this.result.redirectTo(this).list();
	}

	public void delete(int themeId) {
		Theme theme = this.repository.get(themeId);
		this.repository.remove(theme);
		this.result.redirectTo(this).list();
	}
}
