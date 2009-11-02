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
package net.jforum.security;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Topic;

/**
 * Given a list of topics and a rolemanager, filter the topics based on the security roles
 * @author Rafael Steil
 */
public class TopicFilter {
	public List<Topic> filter(List<Topic> topics, RoleManager roleManager) {
		List<Topic> result = new ArrayList<Topic>();

		if (roleManager != null) {
			for (Topic topic : topics) {
				if (roleManager.isForumAllowed(topic.getForum().getId())) {
					result.add(topic);
				}
			}
		}

		return result;
	}
}
