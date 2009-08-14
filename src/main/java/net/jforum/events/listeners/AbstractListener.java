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

import java.util.ArrayList;
import java.util.List;

import net.jforum.events.Event;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Rafael Steil
 */
public abstract class AbstractListener<EventType extends Event<TargetType>, TargetType> {
	private List<EventType> events = new ArrayList<EventType>();

	@Pointcut("execution(* net.jforum.repository.Repository.add(..))")
	@SuppressWarnings("all")
	protected void repositoryAdd() {}

	@Pointcut("execution(* net.jforum.repository.Repository.remove(..))")
	@SuppressWarnings("all")
	protected void repositoryRemove() {}

	@Pointcut("execution(* net.jforum.repository.Repository.update(..))")
	@SuppressWarnings("all")
	protected void repositoryUpdate() {}

	/**
	 * Defines the list the events
	 * @param events the events to set
	 */
	public void setEvents(List<EventType> events) {
		this.events = events;
	}

	protected void fireBeforeAdd(TargetType entity) {
		for (EventType event : this.events) {
			event.beforeAdd(entity);
		}
	}

	protected void fireAdd(TargetType entity) {
		for (EventType event : this.events) {
			event.added(entity);
		}
	}

	protected void fireBeforeRemove(TargetType entity) {
		for (EventType event : this.events) {
			event.beforeDeleted(entity);
		}
	}

	protected void fireRemove(TargetType entity) {
		for (EventType event : this.events) {
			event.deleted(entity);
		}
	}

	protected void fireBeforeUpdated(TargetType entity) {
		for (EventType event : this.events) {
			event.beforeUpdated(entity);
		}
	}

	protected void fireUpdated(TargetType entity) {
		for (EventType event : this.events) {
			event.updated(entity);
		}
	}
}
