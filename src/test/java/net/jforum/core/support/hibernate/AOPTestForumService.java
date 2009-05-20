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
package net.jforum.core.support.hibernate;

import net.jforum.entities.Forum;
import net.jforum.services.ForumService;

/**
 * @author Rafael Steil
 */
public class AOPTestForumService extends ForumService {
	/**
	 * @see net.jforum.services.ForumService#add(net.jforum.entities.Forum)
	 */
	@Override
	public void add(Forum forum) {
	}

	/**
	 * @see net.jforum.services.ForumService#update(net.jforum.entities.Forum)
	 */
	@Override
	public void update(Forum forum) {
	}

	/**
	 * @see net.jforum.services.ForumService#delete(int[])
	 */
	@Override
	public void delete(int... ids) {
	}

	/**
	 * @see net.jforum.services.ForumService#upForumOrder(int)
	 */
	@Override
	public void upForumOrder(int forumId) {
	}

	/**
	 * @see net.jforum.services.ForumService#downForumOrder(int)
	 */
	@Override
	public void downForumOrder(int forumId) {
	}
}
