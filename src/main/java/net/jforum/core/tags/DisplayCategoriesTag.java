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
package net.jforum.core.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import net.jforum.entities.Category;
import net.jforum.security.RoleManager;

/**
 * @author Rafael Steil
 */
public class DisplayCategoriesTag extends JForumTag {
	private List<Category> categories;
	private RoleManager roleManager;
	private String var;

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		for (Category category : this.categories) {
			if (this.roleManager.isCategoryAllowed(category.getId())) {
				this.setAttribute(var, category);
				this.invokeJspBody();
			}
		}
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public void setItems(List<Category> categories) {
		this.categories = categories;
	}
}
