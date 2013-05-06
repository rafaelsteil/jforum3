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

import net.jforum.entities.Category;

/**
 * Default implementation of an {@link Event}, for {@link Topic}.
 * All methods are empty, as it is destined to be
 * extended by classes that only want to handle
 * one or another event
 * @author Rafael Steil
 */

public class EmptyCategoryEvent implements Event<Category> {

	/**
	 * @see net.jforum.events.Event#added(Category)
	 */
	public void added(Category entity) { }

	/**
	 * @see net.jforum.events.Event#deleted(Category)
	 */
	public void deleted(Category entity) { }

	/**
	 * @see net.jforum.events.Event#updated(Category)
	 */
	public void updated(Category entity) { }

	/**
	 * @see net.jforum.events.Event#beforeAdd(java.lang.Object)
	 */
	public void beforeAdd(Category entity) {}

	/**
	 * @see net.jforum.events.Event#beforeDeleted(java.lang.Object)
	 */
	public void beforeDeleted(Category entity) {}

	/**
	 * @see net.jforum.events.Event#beforeUpdated(java.lang.Object)
	 */
	public void beforeUpdated(Category entity) {
	}

}
