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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import net.jforum.util.I18n;

/**
 * @author Rafael Steil
 */
public class I18nTag extends JForumTag implements DynamicAttributes {
	private static I18n i18n;
	private String key;
	private List<Object> params = new ArrayList<Object>();

	public I18nTag() {
		if (i18n == null) {
			i18n = this.getBean(I18n.class);
		}
	}

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		if (this.params.size() == 0) {
			String message = i18n.getMessage(this.key);

			if (message == null) {
				throw new IllegalArgumentException(this.key + " was not found");
			}

			this.write(message);
		}
		else {
			String message = i18n.getFormattedMessage(this.key, this.params.toArray());

			if (message == null) {
				throw new IllegalArgumentException(this.key + " was not found");
			}

			this.write(message);
		}
	}

	/**
	 * @param key the message to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		params.add(value);
	}
}
