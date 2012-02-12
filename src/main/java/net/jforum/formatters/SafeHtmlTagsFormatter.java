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
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class SafeHtmlTagsFormatter implements Formatter {
	private SafeHtml safeHtml;

	public SafeHtmlTagsFormatter(SafeHtml safeHtml) {
		this.safeHtml = safeHtml;
	}

	/**
	 * @see net.jforum.formatters.Formatter#format(java.lang.String, net.jforum.formatters.PostOptions)
	 */
	@Override
	public String format(String text, PostOptions postOptions) {
		return this.safeHtml.makeSafe(text);
	}
}
