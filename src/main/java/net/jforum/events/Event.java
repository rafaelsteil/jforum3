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


/**
 * Common events.
 *
 * Note that events occur inside a transactional context,
 * before a commit, so it is possible that a rollback
 * occur in case of errors. Also, all entities passed
 * as argument will be in the "managed" state
 *
 * @author Rafael Steil
 */
public interface Event<T> {
	/**
	 * Execued just before adding an entity
	 * @param entity
	 */
	public void beforeAdd(T entity);

	/**
	 * Executed after an entity was added
	 * @param entity
	 */
	public void added(T entity);

	/**
	 * Executed just before deleted an entity
	 * @param entity
	 */
	public void beforeDeleted(T entity);

	/**
	 * Executed after an entity was deleted
	 * @param entity
	 */
	public void deleted(T entity);

	/**
	 * Execute before an entity is updated
	 * @param entity
	 */
	public void beforeUpdated(T entity);

	/**
	 * Executed after an entity was updated
	 * @param entity
	 */
	public void updated(T entity);
}
