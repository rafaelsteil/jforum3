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
public class YouTube_TestCase extends TagBaseTest {
	@Test
	public void format() {
		BBCode bb = bbCodes.get("youtube");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a video: %s", this.tagHtml(bb, "www.", "123456")),
			formatter.format("a video: [youtube]http://www.youtube.com/watch?v=123456[/youtube]", defaultOptions()));
	}

	@Test
	public void invalidUrlShouldNotFormat() {
		BBCode bb = bbCodes.get("youtube");
		formatter.addBb(bb);
		Assert.assertEquals("a video: [youtube]http://something.else[/youtube]",
			formatter.format("a video: [youtube]http://something.else[/youtube]", defaultOptions()));
	}

	@Test
	public void mixedCase() {
		BBCode bb = bbCodes.get("youtube");
		formatter.addBb(bb);
		Assert.assertEquals(String.format("a video: %s", this.tagHtml(bb, "www.", "123456")),
			formatter.format("a video: [YOUTUBE]http://www.youtube.com/watch?v=123456[/yOuTube]", defaultOptions()));
	}

	private String tagHtml(BBCode bb, String url, String videoCode) {
		return StringUtils.replace(bb.getReplace(), "$1", url)
			.replace("$2", videoCode);
	}
}
