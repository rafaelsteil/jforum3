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
package net.jforum.plugins.tagging;

import java.util.List;
import java.util.Map;

import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.repository.Repository;

/**
 * @author Bill
 *
 */
public interface TagRepository extends Repository<Tag> {

	List<Tag> getTags(Topic topic);

	/**
	 * remove all the tag where tag name is ? 
	 * @param tag
	 */
	void remove(String tag);

	/**
	 * get all the distinct tag names
	 * @return
	 */
	List<String> getAll();
	
	/**
	 * get all the distinct tag names.
	 * @param start
	 * @param count
	 * @return
	 */
	List<String> getAll(int start, int count);

	/**
	 * list all the distinct tag name
	 * @param tag
	 */
	void update(String oldTag, String newTag);
	
	/**
	 * list top ? hot tags
	 * <name,counts>
	 * @return
	 */
	Map<String,Long> getHotTags(int limit);

	/**
	 * list the hot tag that belong to the give topics.
	 * @param topics
	 * @param limit
	 * @return
	 */
	Map<String,Long> getHotTags(List<Topic> topics,int limit);
	
	/**
	 * list the hot tag that belong to the give forum.
	 * @param topics
	 * @param limit
	 * @return
	 */
	Map<String,Long> getHotTags(Forum forum,int limit);	
	
	/**
	 * list the hot tag that belong to the give forums.
	 * @param topics
	 * @param limit
	 * @return
	 */
	Map<String,Long> getAccessableHotTags(List<Forum> forum,int limit);	
	
	/**
	 * count how many tag(str) have in our system
	 * @param name
	 * @return
	 */
	int count(String name);

	/**
	 * get all the tags that tagged with the gaven string
	 * @param tag
	 * @return
	 */
	List<Topic> getTopics(String tag);
}
