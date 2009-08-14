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

import net.jforum.entities.Category;
import net.jforum.events.Event;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Rafael Steil
 */
@Aspect
public class CategoryEventListener extends AbstractListener<Event<Category>, Category> {
	@Pointcut("target(net.jforum.repository.CategoryRepository)")
	@SuppressWarnings("all")
	private void targetRepository() {}

	@AfterReturning("repositoryAdd() && targetRepository() && args(category)")
	public void added(Category category) {
		this.fireAdd(category);
	}

	@AfterReturning("repositoryRemove() && targetRepository() && args(category)")
	public void removed(Category category) {
		this.fireRemove(category);
	}

	@AfterReturning("repositoryUpdate() && targetRepository() && args(category)")
	public void updated(Category category) {
		this.fireUpdated(category);
	}
	
	@Before("repositoryRemove() && targetRepository() && args(category)")
	public void beforeRemove(Category category) {
		this.fireBeforeRemove(category);
	}
}
