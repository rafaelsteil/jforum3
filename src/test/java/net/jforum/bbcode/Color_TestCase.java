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
public class Color_TestCase extends TagBaseTest {
	@Test
	public void singleLine() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "red text")),
			formatter.format("a [color=red]red text[/color] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("color"));
		Assert.assertEquals("some [color=red]text",
			formatter.format("some [color=red]text", defaultOptions()));
	}

	@Test
	public void withoutColorNameShouldIgnore() {
		formatter.addBb(bbCodes.get("color"));
		Assert.assertEquals("some [color]text[/color]",
			formatter.format("some [color]text[/color]", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "red", "text[color=yellow]")),
			formatter.format("some [color=red]text[color=yellow][/color]", defaultOptions()));
	}

	@Test
	public void multipleLines() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s", this.html(bb, "red", "\ntext\n\nhere")),
			formatter.format("some [color=red]\ntext\n\nhere[/color]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s here", this.html(bb, "red", "text")),
			formatter.format("some [coLOr=red]text[/cOloR] here", defaultOptions()));
	}

	@Test
	public void withQuotes() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color=\"red\"]colored[/color] text", defaultOptions()));
	}

	@Test
	public void withSimpleQuotes() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color='red']colored[/color] text", defaultOptions()));
	}

	@Test
	public void withUnclosedQuoteShouldFormat() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color=\"red]colored[/color] text", defaultOptions()));
	}

	@Test
	public void withUnopenedQuoteShouldFormat() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color=red\"]colored[/color] text", defaultOptions()));
	}

	@Test
	public void withUnclosedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color='red]colored[/color] text", defaultOptions()));
	}

	@Test
	public void withUnopenedSingleQuoteShouldFormat() {
		BBCode bb = bbCodes.get("color");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a %s text", this.html(bb, "red", "colored")),
			formatter.format("a [color=red']colored[/color] text", defaultOptions()));
	}

	private String html(BBCode bb, String color, String text) {
		return StringUtils.replace(bb.getReplace(), "$1", color)
			.replace("$2", text);
	}
}
