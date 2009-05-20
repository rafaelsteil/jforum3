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
import net.jforum.entities.Avatar;
import net.jforum.repository.AvatarRepository;
import net.jforum.security.AdministrationRule;
import net.jforum.services.AvatarService;
import net.jforum.services.ViewService;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.interceptor.UploadedFileInformation;

/**
 * @author Bill
 */
@Component(Domain.AVATAR_ADMIN)
@InterceptedBy({MultipartRequestInterceptor.class,ActionSecurityInterceptor.class})
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class AvatarAdminActions {

	private ViewService viewService;
	private AvatarService service;
	private ViewPropertyBag propertyBag;
	private AvatarRepository repository;

	public AvatarAdminActions(ViewPropertyBag propertyBag,
			AvatarRepository repository, AvatarService service,
			ViewService viewService) {
		this.propertyBag = propertyBag;
		this.repository = repository;
		this.service = service;
		this.viewService = viewService;
	}

	/**
	 * Deletes avatars
	 * @param avatarId
	 */
	public void delete(@Parameter(key = "avatarId") int... avatarId) {
		if (avatarId != null) {
			for (int id : avatarId) {
				Avatar avatar = repository.get(id);
				repository.remove(avatar);
			}
		}

		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * List all avatars
	 */
	public void list() {
		propertyBag.put("GalleryAvatars", repository.getGalleryAvatar());
		propertyBag.put("UploadedAvatars", repository.getUploadedAvatar());
	}

	public void add() {

	}

	/**
	 * Saves a new avatar
	 * @param smilie
	 */
	public void addSave(@Parameter(key = "avatar")Avatar avatar,
		@Parameter(key = "image") UploadedFileInformation image) {
		service.add(avatar, image);
		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the page to edit a existing avatar
	 */
	public void edit(@Parameter(key = "avatarId") int avatarId) {
		propertyBag.put("avatar", repository.get(avatarId));
		viewService.renderView(Actions.ADD);
	}

	public void editSave(@Parameter(key = "avatar") Avatar avatar,
		@Parameter(key = "image") UploadedFileInformation image) {
		service.update(avatar, image);
		viewService.redirectToAction(Actions.LIST);
	}

}
