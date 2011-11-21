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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Avatar;
import net.jforum.repository.AvatarRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.AvatarService;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * @author Bill
 */
@Resource
@Path(Domain.AVATAR_ADMIN)
// @InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class AvatarAdminController {

	private AvatarService avatarService;
	private AvatarRepository repository;
	private final Result result;

	public AvatarAdminController(Result result, AvatarRepository repository,
			AvatarService service) {
		this.result = result;
		this.repository = repository;
		this.avatarService = service;
	}

	/**
	 * Deletes avatars
	 * 
	 * @param avatarId
	 *            One or many avatar id's for the avatars to be deleted.
	 */
	public void delete(int... avatarId) {
		if (avatarId != null) {
			for (int id : avatarId) {
				Avatar avatar = this.repository.get(id);
				this.repository.remove(avatar);
			}
		}

		this.result.redirectTo(this).list();
	}

	/**
	 * List all avatars
	 */
	public void list() {
		this.result.include("GalleryAvatars",
				this.repository.getGalleryAvatar());
		this.result.include("UploadedAvatars",
				this.repository.getUploadedAvatar());
	}

	public void add() {

	}

	/**
	 * Saves a new avatar
	 * 
	 * @param avatar
	 *            The avatar to be saved.
	 * @param image
	 *            Vraptor information object carrying info about the uploaded
	 *            avatar.
	 */
	public void addSave(Avatar avatar, UploadedFile image) {
		this.avatarService.add(avatar, image);
		this.result.redirectTo(Actions.LIST);
	}

	/**
	 * Shows the page to edit a existing avatar
	 * 
	 * @param avatarId
	 *            The avatar id for the avatar to be edited.
	 */
	public void edit(int avatarId) {
		this.result.include("avatar", this.repository.get(avatarId));
		this.result.forwardTo(Actions.ADD);
	}

	public void editSave(Avatar avatar, UploadedFile image) {
		this.avatarService.update(avatar, image);
		this.result.redirectTo(this).list();
	}

}
