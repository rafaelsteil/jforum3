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
package net.jforum.events.listeners;

import net.jforum.entities.Topic;
import net.jforum.events.Event;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Rafael Steil
 */
@Aspect
public class TopicEventListener extends AbstractListener<Event<Topic>, Topic> {
	@Pointcut("target(net.jforum.repository.TopicRepository)")
	@SuppressWarnings("all")
	private void targetRepository() {}

	@AfterReturning("repositoryAdd() && targetRepository() && args(topic)")
	public void added(Topic topic) {
		this.fireAdd(topic);
	}

	@AfterReturning("repositoryRemove() && targetRepository() && args(topic)")
	public void removed(Topic topic) {
		this.fireRemove(topic);
	}

	@AfterReturning("repositoryUpdate() && targetRepository() && args(topic)")
	public void updated(Topic topic) {
		this.fireUpdated(topic);
	}
}
