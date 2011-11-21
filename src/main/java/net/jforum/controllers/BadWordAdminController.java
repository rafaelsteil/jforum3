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
import net.jforum.entities.BadWord;
import net.jforum.repository.BadWordRepository;
import net.jforum.security.AdministrationRule;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.BAD_WORD_ADMIN)
// @InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class BadWordAdminController {
	private BadWordRepository repository;
	private final Result result;

	public BadWordAdminController(Result result, BadWordRepository repository) {
		this.result = result;
		this.repository = repository;
	}

	public void delete(int... badWordId) {
		if (badWordId != null) {
			for (int id : badWordId) {
				BadWord word = this.repository.get(id);
				this.repository.remove(word);
			}
		}

		this.result.redirectTo(this).list();
	}

	public void list() {
		this.result.include("words", this.repository.getAll());
	}

	public void add() {

	}

	public void addSave(BadWord word) {
		this.repository.add(word);
		this.result.redirectTo(this).list();
	}

	public void edit(int id) {
		BadWord word = this.repository.get(id);
		this.result.include("word", word);
		this.result.forwardTo(this).add();
	}

	public void editSave(BadWord word) {
		this.repository.update(word);
		this.result.redirectTo(this).list();
	}
}
