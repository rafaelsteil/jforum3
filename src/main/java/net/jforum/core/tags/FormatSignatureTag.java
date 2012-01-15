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

import net.jforum.formatters.Formatter;
import net.jforum.formatters.PostFormatters;
import net.jforum.formatters.PostOptions;

/**
 * @author Rafael Steil
 */
public class FormatSignatureTag extends JForumTag {
	private static PostFormatters formatters;
	private String signature;

	public FormatSignatureTag() {
		if (formatters == null) {
			formatters = this.getBean(PostFormatters.class);
		}
	}

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		PostOptions options = new PostOptions(false, true, true, false, this.request().getContextPath());

		for (Formatter formatter : formatters) {
			signature = formatter.format(signature, options);
		}

		this.write(signature);
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
