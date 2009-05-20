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

import net.jforum.entities.Post;

/**
 * @author Rafael Steil
 */
public interface PostRepository extends Repository<Post> {
	/**
	 * Count how many posts there are in a specific topic until this post id
	 * @param postId the post id to check
	 * @return the number of posts until this post, in the same topic
	 */
	public int countPreviousPosts(int postId);
}