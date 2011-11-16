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
package net.jforum.plugins.post;

import net.jforum.entities.Forum;
import net.jforum.repository.Repository;

/**
 * @author Bill
 *
 */
public interface ForumLimitedTimeRepository extends Repository<ForumLimitedTime> {

	/**
	 * get the limited time for gaven forum
	 * if not setted or no limited will return 0
	 * @param forum
	 * @return
	 */
	long getLimitedTime(Forum forum);

	/**
	 * get the FourmLimitedTime for gaven forum
	 * @param forum
	 * @return
	 */
	public ForumLimitedTime getForumLimitedTime(Forum forum);

	/**
	 * save or Updates the information of an existing object
	 * @param instance the instance to update
	 */
	public void saveOrUpdate(ForumLimitedTime fourmLimitedTime);
}
