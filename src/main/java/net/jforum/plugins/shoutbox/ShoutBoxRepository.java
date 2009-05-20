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

import java.util.List;

import net.jforum.entities.Category;
import net.jforum.repository.Repository;

/**
 * @author Bill
 *
 */
public interface ShoutBoxRepository extends Repository<ShoutBox> {

	@Deprecated
	List<ShoutBox> getAvalibleBoxes(boolean isAnonymous);

	/**
	 * According category to find a ShoutBox
	 * @param category
	 * @return
	 */
	ShoutBox getShoutBox(Category category);

	/**
	 * Get all shoutboxes data from the database.
	 *
	 * @return all shoutboxes found
	 */
	public List<ShoutBox> getAllShoutBoxes();

}
