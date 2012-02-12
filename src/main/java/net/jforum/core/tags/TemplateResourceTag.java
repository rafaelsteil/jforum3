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

import net.jforum.util.ConfigKeys;

/**
 * Given a resource name, builds it's absolute URL, to be used in the templates
 * @author Rafael Steil
 */
public class TemplateResourceTag extends JForumTag {
	private String item;

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws IOException {
		String path = new StringBuilder(128)
			.append(this.request().getContextPath())
			.append(config().getValue(ConfigKeys.TEMPLATE_DIRECTORY))
			.append(this.item)
			.toString();

		this.write(path);
	}

	/**
	 * @param item the resource to set
	 */
	public void setItem(String item) {
		this.item = item;
	}
}
