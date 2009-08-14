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

import net.jforum.entities.Banlist;

/**
 * @author Rafael Steil
 */
public interface BanlistRepository extends Repository<Banlist> {

	/**
	 * @see net.jforum.dao.BanlistDAO#selectAll()
	 */
	public List<Banlist> getAllBanlists();

}