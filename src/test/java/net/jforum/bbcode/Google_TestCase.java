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
public class Google_TestCase extends TagBaseTest {
	@Test
	public void format() {
		BBCode bb = bbCodes.get("google");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("search %s in google", this.html(bb, "java")),
			formatter.format("search [google]java[/google] in google", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("google"));
		Assert.assertEquals("a [google]search", formatter.format("a [google]search", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		BBCode bb = bbCodes.get("google");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("some %s search", this.html(bb, "search[google]")),
			formatter.format("some [google]search[google][/google] search", defaultOptions()));
	}

	@Test
	public void multipleLinesShouldIgnore() {
		formatter.addBb(bbCodes.get("google"));
		Assert.assertEquals("some [google]\nsearch[/google]",
			formatter.format("some [google]\nsearch[/google]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("google");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("search %s in google", this.html(bb, "java")),
			formatter.format("search [GoogLE]java[/gooGle] in google", defaultOptions()));
	}

	private String html(BBCode bb, String input) {
		return StringUtils.replace(bb.getReplace(), "$1", input);
	}
}
