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

import net.jforum.formatters.BBCode;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SimpleUrl_TestCase extends TagBaseTest {
	@Test
	public void format() {
		BBCode bb = bbCodes.get("simple-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something")),
			formatter.format("a [url]http://something[/url] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("simple-url"));
		Assert.assertEquals("some [url=http://something]text",
			formatter.format("some [url=http://something]text", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		BBCode bb = bbCodes.get("simple-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "http://something[url]http://other.site")),
			formatter.format("some [url]http://something[url]http://other.site[/url]", defaultOptions()));
	}

	@Test
	@Ignore("bbcode need a major refactor to be able to test that case, the auto-url-simple rule conflict")
	public void multipleLinesShouldIgnore() {
		BBCode bb = bbCodes.get("simple-url");
		formatter.addBb(bb);
		Assert.assertEquals("some [url]\nhttp://something\n\n[/url]",
			formatter.format("some [url]\nhttp://something\n\n[/url]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("simple-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s here", this.html(bb, "http://something")),
			formatter.format("some [UrL]http://something[/URL] here", defaultOptions()));
	}

	private String html(BBCode bb, String url) {
		return StringUtils.replace(bb.getReplace(), "$1", url);
	}
}
