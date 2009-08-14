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
package net.jforum.plugins.shoutbox;

import java.util.List;

import net.jforum.repository.Repository;

/**
 * @author Bill
 *
 */
public interface ShoutRepository extends Repository<Shout> {

	/**
	 * get all the shouts
	 * @return
	 */
	List<Shout> getAll();
	
	/**
	 * get all the shouts of the give shoutbox
	 * @param shoubox
	 * @return
	 */
	List<Shout> getAll(ShoutBox shoubox);
	
	/**
	 * 
	 * @param start
	 * @param count
	 * @return
	 */
	List<Shout> getAll(int start,int count);
	
	/**
	 * 
	 * @param shoutbox
	 * @param start
	 * @param count
	 * @return
	 */
	List<Shout> getAll(ShoutBox shoutbox, int start, int count);
	
	/**
	 * get the shouts from the give shout box since lastID
	 * max displayCount
	 * @param lastId
	 * @param shoutBox
	 * @param displayCount
	 * @return
	 */
	List<Shout> getShout(int lastId,ShoutBox shoutBox, int displayCount);
	
	/**
	 * get latest Shout from same IP
	 * @param shouterIp
	 * @return
	 */
	Shout getMyLastShout(String shouterIp);
}
