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

package net.jforum.repository;

import java.util.List;

import net.jforum.entities.Avatar;

/**
 * @author Bill
 */
public interface AvatarRepository extends Repository<Avatar>{

	/**
	 * get all the Avatar
	 * @return
	 */
	public List<Avatar> getAll();

	/**
	 * getGalleryAvater()
	 * @return a set of Avatar
	 */
	public List<Avatar> getGalleryAvatar();

	/**
	 * get All the Avatars that Uploaded by user
	 */
	public List<Avatar> getUploadedAvatar();
}
