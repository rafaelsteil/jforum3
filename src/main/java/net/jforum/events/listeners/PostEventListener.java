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

import net.jforum.entities.Post;
import net.jforum.events.Event;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Rafael Steil
 */
@Aspect
public class PostEventListener extends AbstractListener<Event<Post>, Post> {
	@Pointcut("target(net.jforum.repository.PostRepository)")
	@SuppressWarnings("all")
	private void targetRepository() {}

	@Before("repositoryAdd() && targetRepository() && args(post)")
	public void beforeAdd(Post post) {
		this.fireBeforeAdd(post);
	}

	@Before("repositoryUpdate() && targetRepository() && args(post)")
	public void beforeUpdated(Post post) {
		this.fireBeforeUpdated(post);
	}

	@AfterReturning("repositoryAdd() && targetRepository() && args(post)")
	public void added(Post post) {
		this.fireAdd(post);
	}

	@AfterReturning("repositoryRemove() && targetRepository() && args(post)")
	public void removed(Post post) {
		this.fireRemove(post);
	}

	@AfterReturning("repositoryUpdate() && targetRepository() && args(post)")
	public void updated(Post post) {
		this.fireUpdated(post);
	}
}
