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

import net.jforum.entities.Forum;
import net.jforum.security.RoleManager;

/**
 * @author Rafael Steil
 */
public class DisplayForumsTag extends JForumTag {
	private String var;
	private List<Forum> forums;
	private RoleManager roleManager;
	private boolean isModerator;

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		int counter = 1;

		for (Forum forum : this.forums) {
			if (this.roleManager.isForumAllowed(forum.getId())) {
				this.setAttribute(this.var, forum);
				this.setAttribute(this.var + "Counter", counter);
				this.invokeJspBody();

				counter++;
			}
		}
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public void setItems(List<Forum> forums) {
		this.forums = forums;
	}

	public void setModerator(boolean isModerator) {
		this.isModerator = isModerator;
	}
}
