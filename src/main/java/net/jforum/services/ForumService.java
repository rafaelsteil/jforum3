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
package net.jforum.services;

import java.util.List;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Forum;
import net.jforum.repository.ForumRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ForumService {
	private ForumRepository repository;

	public ForumService(ForumRepository repository) {
		this.repository = repository;
	}

	/**
	 * Add a new forum
	 * @param forum
	 */
	public void add(Forum forum) {
		this.applyCommonConstraints(forum);

		if (forum.getId() > 0) {
			throw new ValidationException("This appears to be an existing forum (id > 0). Please use update() instead.");
		}

		this.repository.add(forum);
	}

	/**
	 * Updates the information of an existing forum
	 * @param forum
	 */
	public void update(Forum forum) {
		this.applyCommonConstraints(forum);

		if (forum.getId() == 0) {
			throw new ValidationException("update() expects a forum with an existing id");
		}

		Forum currentForum = this.repository.get(forum.getId());

		currentForum.setName(forum.getName());
		currentForum.setDescription(forum.getDescription());
		currentForum.setModerated(forum.isModerated());
		currentForum.setAllowAnonymousPosts(forum.isAllowAnonymousPosts());
		currentForum.setCategory(forum.getCategory());

		this.repository.update(currentForum);
	}

	/**
	 * Deletes on or more forums
	 * @param ids
	 */
	public void delete(int... ids) {
		if (ids != null) {
			for (int id : ids) {
				Forum forum = this.repository.get(id);

				this.repository.remove(forum);
			}
		}
	}

	/**
	 * Changes the forum order one level up
	 * @param forumId
	 */
	public void upForumOrder(int forumId) {
		this.processOrdering(true, forumId);
	}

	/**
	 * Changes the forum order one level down
	 * @param forumId
	 */
	public void downForumOrder(int forumId) {
		this.processOrdering(false, forumId);
	}

	/**
	 * Changes the order of the specified forum, adding it one level or one level down
	 * @param up if true, sets the forum one level up. If false, one level down
	 * @param forumId the id of the category to change
	 */
	private void processOrdering(boolean up, int forumId) {
		Forum toChange = this.repository.get(forumId);
		List<Forum> forums = toChange.getCategory().getForums();

		int index = forums.indexOf(toChange);

		if (index > -1 && (up && index > 0) || (!up && index + 1 < forums.size())) {
			Forum otherForum = up ? forums.get(index - 1) : forums.get(index + 1);

			int oldOrder = toChange.getDisplayOrder();

			toChange.setDisplayOrder(otherForum.getDisplayOrder());
			otherForum.setDisplayOrder(oldOrder);

			this.repository.update(toChange);
			this.repository.update(otherForum);
		}
	}

	private void applyCommonConstraints(Forum forum) {
		if (forum == null) {
			throw new NullPointerException("Cannot save a null forum");
		}

		if (forum.getCategory() == null || forum.getCategory().getId() == 0) {
			throw new ValidationException("A forum must be associated to a category");
		}

		if (StringUtils.isEmpty(forum.getName())) {
			throw new ValidationException("A forum must have a name");
		}
	}
}
