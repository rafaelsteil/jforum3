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

import net.jforum.actions.helpers.Actions;
import net.jforum.services.ViewService;

import org.apache.commons.lang.ArrayUtils;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 *
 */
@Resource
@Path("adminTags")
public class TagAdminController {
	private ViewService viewService;
	private TagRepository repository;
	private final Result result;

	public TagAdminController(TagRepository repository, Result result) {
		this.result = result;
		this.repository = repository;
	}

	public void delete(String... tags) {
		if (!ArrayUtils.isEmpty(tags)) {
			for (String tag : tags) {
				if (tag != null) {
					this.repository.remove(tag);
				}
			}
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	public void list() {
		result.include("tags", this.repository.getAll());
	}

	public void add() {
	}

	public void edit(String tag) {
		result.include("name", tag);
		this.viewService.renderView(Actions.ADD);
	}

	public void editsave(String oldTag, String newTag) {
		this.repository.update(oldTag,newTag);
		this.viewService.redirectToAction(Actions.LIST);
	}
}
