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

/**
 * Generic / common operations for most of the repositories. 
 *  
 * @author Rafael Steil
 */
public interface Repository<T> {
	/**
	 * Tries to get an instance of the object 
	 * @param id the id to search for
	 * @return the requested instance, or <code>null</code> if not found
	 */
	public T get(int id);
	
	/**
	 * Adds a new instance of the object
	 * @param instance the instance to save
	 */
	public void add(T entity);
	
	/**
	 * Deletes the object
	 * @param instance the object to delete
	 */
	public void remove(T entity);
	
	/**
	 * Updates the information of an existing object
	 * @param instance the instance to update
	 */
	public void update(T entity);
}
