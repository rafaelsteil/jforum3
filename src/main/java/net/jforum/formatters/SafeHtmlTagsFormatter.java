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
package net.jforum.formatters;

import net.jforum.util.SafeHtml;

/**
 * @author Rafael Steil
 */
public class SafeHtmlTagsFormatter implements Formatter {
	private SafeHtml safeHtml;

	public SafeHtmlTagsFormatter(SafeHtml safeHtml) {
		this.safeHtml = safeHtml;
	}

	/**
	 * @see net.jforum.formatters.Formatter#format(java.lang.String, net.jforum.formatters.PostOptions)
	 */
	public String format(String text, PostOptions postOptions) {
		return safeHtml.makeSafe(text);
	}
}
