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
public class AutoEmail_TestCase extends TagBaseTest {
	@Test
	public void usingValidEmailAdddressShouldFormat() {
		BBCode bb = getBBCode();
		formatter.addBb(bb);
		Assert.assertEquals(String.format("send to %s now", html(bb, "email", "address.domain")),
			formatter.format("send to email@address.domain now", defaultOptions()));
	}

	@Test
	public void usingValidEmailAndNewLineBeforeAddressShouldFormat() {
		BBCode bb = getBBCode();
		formatter.addBb(bb);
		Assert.assertEquals(String.format("send to\n%s now", html(bb, "email", "address.domain")),
			formatter.format("send to\nemail@address.domain now", defaultOptions()));
	}

	@Test
	public void emailWithoutDomainShouldIgnore() {
		BBCode bb = getBBCode();
		formatter.addBb(bb);
		Assert.assertEquals("send to email@address now",
			formatter.format("send to email@address now", defaultOptions()));
	}

	@Test
	public void emailInUpperCaseShouldIgnore() {
		BBCode bb = getBBCode();
		formatter.addBb(bb);
		Assert.assertEquals("send to EMAIL@address now",
			formatter.format("send to EMAIL@address now", defaultOptions()));
	}

	@Test
	public void withSpecialCharsShouldFormat() {
		BBCode bb = getBBCode();
		formatter.addBb(bb);
		Assert.assertEquals(String.format("send to %s now", html(bb, "e.ma-il_and-3949223", "address234.domain789")),
			formatter.format("send to e.ma-il_and-3949223@address234.domain789 now", defaultOptions()));
	}

	private BBCode getBBCode() {
		BBCode bb = bbCodes.get("auto-email");

		Assert.assertTrue(bb.alwaysProcess());

		return bb;
	}

	private String html(BBCode bb, String name, String domain) {
		return StringUtils.replace(bb.getReplace(), "$1", "")
			.replace("$2", name).replace("$3", domain);
	}
}
