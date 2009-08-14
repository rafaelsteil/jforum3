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

import net.jforum.entities.Group;

/**
 * @author Rafael Steil
 */
public interface GroupRepository extends Repository<Group> {

	/**
	 * @return all existing groups
	 */
	public List<Group> getAllGroups();

	/**
	 * get a group according to the given name
	 * @param string
	 * @return
	 */
	public Group getByName(String string);
}