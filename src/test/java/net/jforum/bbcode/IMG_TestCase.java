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
public class IMG_TestCase extends TagBaseTest {
	@Test
	public void singleLine() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some <img src=\"image\" border=\"0\" /> here",
			formatter.format("some [img]image[/img] here", defaultOptions()));
	}

	@Test
	public void incompleteTagShouldDoNothing() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some [img]image here",
			formatter.format("some [img]image here", defaultOptions()));
	}

	@Test
	public void twoOpenZeroClosedShouldDoNothing() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some [img]image[img] here",
			formatter.format("some [img]image[img] here", defaultOptions()));
	}

	@Test
	public void twoOpenOneClosedExpectOneFormatted() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some <img src=\"image[img]other\" border=\"0\" /> here",
			formatter.format("some [img]image[img]other[/img] here", defaultOptions()));
	}

	@Test
	public void multipleLineShouldIgnore() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some [img]\nimage here[/img]\n\n",
			formatter.format("some [img]\nimage here[/img]\n\n", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		formatter.addBb(bbCodes.get("img"));
		Assert.assertEquals("some <img src=\"image\" border=\"0\" /> here",
			formatter.format("some [img]image[/IMG] here", defaultOptions()));
	}
}
