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
public class B_TestCase extends TagBaseTest {
	@Test
	public void singleLine() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some <strong>bold</strong> text", formatter.format("some [b]bold[/b] text", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some [b]bold text", formatter.format("some [b]bold text", defaultOptions()));
	}

	@Test
	public void twoOpenZeroClosedShouldDoNothing() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some [b]bold[b] text", formatter.format("some [b]bold[b] text", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some <strong>bold[b] text</strong>", formatter.format("some [b]bold[b] text[/b]", defaultOptions()));
	}

	@Test
	public void multipleLines() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some <strong>\nbold\n\n</strong>\n text", formatter.format("some [b]\nbold\n\n[/b]\n text", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		formatter.addBb(bbCodes.get("b"));
		Assert.assertEquals("some <strong>bold</strong> text", formatter.format("some [B]bold[/b] text", defaultOptions()));
	}
}
