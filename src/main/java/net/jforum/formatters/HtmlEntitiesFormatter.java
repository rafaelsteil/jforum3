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

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Replace &lt; and &gt; by &amp;lt; and &amp;gt;
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class HtmlEntitiesFormatter implements Formatter {
	/**
	 * @see net.jforum.formatters.Formatter#format(java.lang.String, PostOptions)
	 */
	@Override
	public String format(String text, PostOptions postOptions) {
		if (!postOptions.isHtmlEnabled()) {
			text = StringUtils.replace(text, "<", "&lt;");
			text = StringUtils.replace(text, ">", "&gt;");
		}

		return text;
	}
}
