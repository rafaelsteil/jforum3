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

import net.jforum.entities.Forum;
import net.jforum.events.Event;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Rafael Steil
 */
@Aspect
public class ForumEventListener extends AbstractListener<Event<Forum>, Forum> {
	@Pointcut("target(net.jforum.repository.ForumRepository)")
	@SuppressWarnings("all")
	private void targetRepository() {}

	@Before("repositoryAdd() && targetRepository() && args(forum)")
	public void beforeAdd(Forum forum) {
		this.fireBeforeAdd(forum);
	}

	@AfterReturning("repositoryAdd() && targetRepository() && args(forum)")
	public void added(Forum forum) {
		this.fireAdd(forum);
	}

	@AfterReturning("repositoryRemove() && targetRepository() && args(forum)")
	public void removed(Forum forum) {
		this.fireRemove(forum);
	}

	@AfterReturning("repositoryUpdate() && targetRepository() && args(forum)")
	public void updated(Forum forum) {
		this.fireUpdated(forum);
	}
}
