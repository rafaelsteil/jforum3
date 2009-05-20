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
import net.jforum.core.support.vraptor.MultipartRequestInterceptor;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.SmilieService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.interceptor.UploadedFileInformation;

/**
 * @author Rafael Steil
 */
@Component(Domain.SMILIES_ADMIN)
@InterceptedBy( { MultipartRequestInterceptor.class, ActionSecurityInterceptor.class })
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class SmilieAdminActions {
	private SmilieRepository repository;
	private SmilieService service;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;

	public SmilieAdminActions(SmilieService service, SmilieRepository repository,
		ViewPropertyBag propertyBag, ViewService viewService) {
		this.service = service;
		this.repository = repository;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
	}

	/**
	 * List all smilies
	 */
	public void list() {
		propertyBag.put("smilies", repository.getAllSmilies());
	}

	/**
	 * Shows the page to insert a new smilie
	 */
	public void add() {

	}

	/**
	 * Saves a new smilie
	 * @param smilie
	 */
	public void addSave(@Parameter(key = "smilie") Smilie smilie,
		@Parameter(key = "image") UploadedFileInformation image) {
		service.add(smilie, image);
		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the page to edit a existing smilie
	 */
	public void edit(@Parameter(key = "smilieId") int smilieId) {
		propertyBag.put("smilie", repository.get(smilieId));
		viewService.renderView(Actions.ADD);
	}

	public void editSave(@Parameter(key = "smilie") Smilie smilie,
		@Parameter(key = "image") UploadedFileInformation image) {
		service.update(smilie, image);
		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Deletes smilies
	 * @param smiliesId
	 */
	public void delete(@Parameter(key = "smiliesId") int... smiliesId) {
		service.delete(smiliesId);
		viewService.redirectToAction(Actions.LIST);
	}
}
