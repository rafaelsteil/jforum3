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
package net.jforum.events;

import net.jforum.entities.Post;

/**
 * Default implementation of an {@link Event}, for {@link Post}.
 * All methods are empty, as it is destined to be
 * extended by classes that only want to handle
 * one or another event
 * @author Rafael Steil
 */
public class EmptyPostEvent implements Event<Post> {
	/**
	 * @see net.jforum.events.PostEvent#added(net.jforum.entities.Post)
	 */
	public void added(Post post) { }

	/**
	 * @see net.jforum.events.PostEvent#deleted(net.jforum.entities.Post)
	 */
	public void deleted(Post post) { }

	/**
	 * @see net.jforum.events.PostEvent#updated(net.jforum.entities.Post)
	 */
	public void updated(Post post) { }

	/**
	 * @see net.jforum.events.Event#beforeAdd(java.lang.Object)
	 */
	public void beforeAdd(Post entity) { }

	/**
	 * @see net.jforum.events.Event#beforeAdd(java.lang.Object)
	 */
	public void beforeDeleted(Post entity) { }

	/**
	 * @see net.jforum.events.Event#beforeUpdated(java.lang.Object)
	 */
	public void beforeUpdated(Post entity) {
	}
}
