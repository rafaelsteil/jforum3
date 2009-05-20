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

import net.jforum.entities.Topic;
import net.jforum.events.EmptyTopicEvent;

/**
 * @author Bill
 *
 */
public class TagTopicEvent extends EmptyTopicEvent {
	private TagRepository repository;

	public TagTopicEvent(TagRepository repository) {
		this.repository = repository;
	}

	/**
	 * The actions are:
	 * <ul>
	 * 	<li> If topic.tags.size > 0, delete all the tags
	 * </ul>
	 */
	@Override
	public void beforeDeleted(Topic topic) {
		List<Tag> tags = repository.getTags(topic);
		for(Tag tag : tags)
			repository.remove(tag);
	}

}
