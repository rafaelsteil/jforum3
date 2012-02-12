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
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.SmilieService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.SMILIES_ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class SmilieAdminController {
	private SmilieRepository repository;
	private SmilieService service;
	private final Result result;

	public SmilieAdminController(SmilieService service,
			SmilieRepository repository, Result result) {
		this.service = service;
		this.repository = repository;
		this.result = result;
	}

	/**
	 * List all smilies
	 */
	public void list() {
		this.result.include("smilies", this.repository.getAllSmilies());
	}

	/**
	 * Shows the page to insert a new smilie
	 */
	public void add() {

	}

	/**
	 * Saves a new smilie
	 *
	 * @param smilie
	 */
	public void addSave(Smilie smilie, UploadedFile image) {
		this.service.add(smilie, image);
		this.result.redirectTo(this).list();
	}

	/**
	 * Shows the page to edit a existing smilie
	 */
	public void edit(int smilieId) {
		this.result.include("smilie", this.repository.get(smilieId));
		this.result.forwardTo(this).add();
	}

	public void editSave(Smilie smilie, UploadedFile image) {
		this.service.update(smilie, image);
		this.result.redirectTo(this).list();
	}

	/**
	 * Deletes smilies
	 *
	 * @param smiliesId
	 */
	public void delete(int... smiliesId) {
		this.service.delete(smiliesId);
		this.result.redirectTo(this).list();
	}
}
