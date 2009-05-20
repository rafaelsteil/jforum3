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
package net.jforum.core.support.hibernate;

import net.jforum.entities.Category;
import net.jforum.services.CategoryService;

/**
 * @author Rafael Steil
 */
public class AOPTestCategoryService extends CategoryService {
	/**
	 * @see net.jforum.services.CategoryService#add(net.jforum.entities.Category)
	 */
	@Override
	public void add(Category category) { }

	/**
	 * @see net.jforum.services.CategoryService#upCategoryOrder(int)
	 */
	@Override
	public void upCategoryOrder(int categoryId) { }

	/**
	 * @see net.jforum.services.CategoryService#downCategoryOrder(int)
	 */
	@Override
	public void downCategoryOrder(int categoryId) {
	}

	/**
	 * @see net.jforum.services.CategoryService#delete(int[])
	 */
	@Override
	public void delete(int... ids) {
	}

	/**
	 * @see net.jforum.services.CategoryService#update(net.jforum.entities.Category)
	 */
	@Override
	public void update(Category category) {
	}
}
