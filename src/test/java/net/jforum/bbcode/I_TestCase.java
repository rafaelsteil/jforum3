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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class I_TestCase extends TagBaseTest {
	@Test
	public void singleLine() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some <i>italic</i> text", formatter.format("some [i]italic[/i] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some [i]italic text", formatter.format("some [i]italic text", defaultOptions()));
	}

	@Test
	public void twoOpenZeroClosedShouldDoNothing() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some [i]italic[i] text", formatter.format("some [i]italic[i] text", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some <i>italic[i] text</i>", formatter.format("some [i]italic[i] text[/i]", defaultOptions()));
	}

	@Test
	public void multipleLines() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some <i>\nitalic\n\n</i>\n text", formatter.format("some [i]\nitalic\n\n[/i]\n text", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		formatter.addBb(bbCodes.get("i"));
		Assert.assertEquals("some <i>italic</i> text", formatter.format("some [I]italic[/i] text", defaultOptions()));
	}
}
