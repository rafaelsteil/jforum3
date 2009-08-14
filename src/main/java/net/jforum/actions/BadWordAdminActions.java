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
import net.jforum.entities.BadWord;
import net.jforum.repository.BadWordRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.BAD_WORD_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class BadWordAdminActions {
	private ViewService viewService;
	private ViewPropertyBag propertyBag;
	private BadWordRepository repository;

	public BadWordAdminActions(ViewService viewService, ViewPropertyBag propertyBag, BadWordRepository repository) {
		this.viewService = viewService;
		this.propertyBag = propertyBag;
		this.repository = repository;
	}

	public void delete(@Parameter(key = "badWordId") int... badWordId) {
		if (badWordId != null) {
			for (int id : badWordId) {
				BadWord word = this.repository.get(id);
				this.repository.remove(word);
			}
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	public void list() {
		this.propertyBag.put("words", this.repository.getAll());
	}

	public void add() {

	}

	public void addSave(@Parameter(key = "word") BadWord word) {
		this.repository.add(word);
		this.viewService.redirectToAction(Actions.LIST);
	}

	public void edit(@Parameter(key = "id") int id) {
		BadWord word = this.repository.get(id);
		this.propertyBag.put("word", word);
		this.viewService.renderView(Actions.ADD);
	}

	public void editSave(@Parameter(key = "word") BadWord word) {
		this.repository.update(word);
		this.viewService.redirectToAction(Actions.LIST);
	}
}
