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

import javax.servlet.jsp.JspException;

/**
 * @author Rafael Steil
 */
public class SettingsTag extends JForumTag {
	private String key;
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		this.write(this.config().getValue(this.key));
	}
	
	public void setKey(String key) {
		this.key = key;
	}
}
