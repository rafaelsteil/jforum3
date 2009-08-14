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

import net.jforum.entities.Category;
import net.jforum.events.EmptyCategoryEvent;
import net.jforum.util.JForumConfig;

/**
 * @author Bill
 *
 */
public class ShoutBoxEvent extends EmptyCategoryEvent {

	private ShoutBoxRepository repository;
	private JForumConfig config;
	
	public ShoutBoxEvent(JForumConfig config, ShoutBoxRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	@Override
	public void added(Category entity) {
		//add ShoutBox
		ShoutBox shoutBox = new ShoutBox();
		shoutBox.setCategory(entity);
		shoutBox.setAllowAnonymous(config.getBoolean(ConfigKeys.SHOUTBOX_DEFAULT_ALLOW_ANONYMOUS, false));
		shoutBox.setDisabled(config.getBoolean(ConfigKeys.SHOUTBOX_DEFAULT_DISABLED, true));
		shoutBox.setShoutLength(config.getInt(ConfigKeys.SHOUTBOX_DEFAULT_SHOUT_LENGTH,250));
		repository.add(shoutBox);
	}

	@Override
	public void beforeDeleted(Category entity) {
		// del ShoutBox
		ShoutBox shoutBox = repository.getShoutBox(entity);
		if(shoutBox!=null)
			repository.remove(shoutBox);
	}

}
