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

import net.jforum.formatters.NewLineToHtmlBreakFormatter;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class NewLineToHtmBreakFormatterTestCase {
	@Test
	public void twoNewLinesExpectsTwoBreaks() {
		Assert.assertEquals("some<br/>  text and some<br/>  more",
			new NewLineToHtmlBreakFormatter().format("some\n text and some\n more", null));
	}

	@Test
	public void stringWithoutNewLineExpectNoChanges() {
		String input = "this is a regular content";
		Assert.assertEquals(input, new NewLineToHtmlBreakFormatter().format(input, null));
	}
}
