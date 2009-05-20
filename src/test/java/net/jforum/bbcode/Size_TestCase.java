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
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class Size_TestCase extends TagBaseTest {
	@Test
	public void singleLine() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size=10]sized[/size] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("size"));
		Assert.assertEquals("some [size=10]text",
			formatter.format("some [size=10]text", defaultOptions()));
	}

	@Test
	public void withoutSizePortionShouldIgnore() {
		formatter.addBb(bbCodes.get("size"));
		Assert.assertEquals("some [size]text[/size]",
			formatter.format("some [size]text[/size]", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "10", "text[size=12]")),
			formatter.format("some [size=10]text[size=12][/size]", defaultOptions()));
	}

	@Test
	public void multipleLines() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "10", "\ntext\n\nhere")),
			formatter.format("some [size=10]\ntext\n\nhere[/size]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s here", this.html(bb, "10", "text")),
			formatter.format("some [siZE=10]text[/siZe] here", defaultOptions()));
	}

	@Test
	public void withQuotes() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size=\"10\"]sized[/size] text", defaultOptions()));
	}

	@Test
	public void withSimpleQuotes() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size='10']sized[/size] text", defaultOptions()));
	}

	@Test
	public void withUnclosedQuoteShouldFormat() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size=\"10]sized[/size] text", defaultOptions()));
	}

	@Test
	public void withUnopenedQuoteShould() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size=10\"]sized[/size] text", defaultOptions()));
	}

	@Test
	public void withUnclosedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size='10]sized[/size] text", defaultOptions()));
	}

	@Test
	public void withUnopenedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("size");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "10", "sized")),
			formatter.format("a [size=10']sized[/size] text", defaultOptions()));
	}

	private String html(BBCode bb, String size, String text) {
		return StringUtils.replace(bb.getReplace(), "$1", size)
			.replace("$2", text);
	}
}
