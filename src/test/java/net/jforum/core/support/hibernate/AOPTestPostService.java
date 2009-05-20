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

import net.jforum.entities.Post;
import net.jforum.services.PostService;

/**
 * @author Rafael Steil
 */
public class AOPTestPostService extends PostService {
	/**
	 * @see net.jforum.services.PostService#delete(net.jforum.entities.Post)
	 */
	@Override
	public void delete(Post post) { }
}
