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
public class DescriptiveUrl_TestCase extends TagBaseTest {
	@Test
	public void format() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url=http://something]url description[/url] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("descriptive-url"));
		Assert.assertEquals("some [url=http://something]text",
			formatter.format("some [url=http://something]text", defaultOptions()));
	}

	@Test
	@Ignore("bbcode need a major refactor to be able to test that case, the simple-url rule conflict")
	public void withoutUrlAddressShouldIgnore() {
		formatter.addBb(bbCodes.get("descriptive-url"));
		Assert.assertEquals("some [url]text[/url]",
			formatter.format("some [url]text[/url]", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "http://something", "url description[url=http://other.site]")),
			formatter.format("some [url=http://something]url description[url=http://other.site][/url]", defaultOptions()));
	}

	@Test
	public void multipleLinesShouldIgnore() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals("some [url=http://something]\ntext\n\nhere[/url]",
			formatter.format("some [url=http://something]\ntext\n\nhere[/url]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s here", this.html(bb, "http://something", "url description")),
			formatter.format("some [UrL=http://something]url description[/URL] here", defaultOptions()));
	}

	@Test
	public void withQuotes() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url=\"http://something\"]url description[/url] text", defaultOptions()));
	}

	@Test
	public void withSimpleQuotes() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url='http://something']url description[/url] text", defaultOptions()));
	}

	@Test
	public void withUnclosedQuoteShouldFormat() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "something", "url description")),
			formatter.format("a [url=\"something]url description[/url] text", defaultOptions()));
	}

	@Test
	public void withUnopenedQuoteShouldFormat() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url=http://something\"]url description[/url] text", defaultOptions()));
	}

	@Test
	public void withUnclosedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url='http://something]url description[/url] text", defaultOptions()));
	}

	@Test
	public void withUnopenedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("descriptive-url");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "http://something", "url description")),
			formatter.format("a [url=http://something']url description[/url] text", defaultOptions()));
	}

	private String html(BBCode bb, String url, String description) {
		return StringUtils.replace(bb.getReplace(), "$1", url)
			.replace("$2", description);
	}
}
