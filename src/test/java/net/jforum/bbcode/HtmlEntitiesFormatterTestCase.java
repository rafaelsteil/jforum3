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
package net.jforum.bbcode;

import net.jforum.formatters.HtmlEntitiesFormatter;
import net.jforum.formatters.PostOptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class HtmlEntitiesFormatterTestCase {
	@Test
	public void htmlEnabledShouldNotFormat() {
		PostOptions options = new PostOptions(true, false, false, false, null);
		String input = "some <b>content</b>";
		String expected = input;

		Assert.assertEquals(expected, new HtmlEntitiesFormatter().format(input, options));
	}

	@Test
	public void format() {
		PostOptions options = new PostOptions(false, false, false, false, null);
		String input = "some <b>bold</b> test and < other > stuff";
		String expected = "some &lt;b&gt;bold&lt;/b&gt; test and &lt; other &gt; stuff";

		Assert.assertEquals(expected, new HtmlEntitiesFormatter().format(input, options));
	}
}
