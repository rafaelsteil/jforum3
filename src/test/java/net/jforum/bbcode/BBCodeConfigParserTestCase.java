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

import java.io.File;

import net.jforum.formatters.BBCode;
import net.jforum.formatters.BBCodeConfigParser;
import net.jforum.formatters.BBConfigFormatter;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
@Ignore("bbcode need a major refactor to be able to test that case, the rule's file must be injected")
public class BBCodeConfigParserTestCase {
	private Mockery mock = TestCaseUtils.newMockery();
	private BBConfigFormatter formatter = mock.mock(BBConfigFormatter.class);

	@Test
	public void parse() {
		mock.checking(new Expectations() {{
			// Tag 1
			BBCode tag1 = new BBCode();
			tag1.setRegex("tag1-regex");
			tag1.setReplace("tag1-replace");
			tag1.setTagName("tag1");

			one(formatter).addBb(tag1);

			// Tag 2
			BBCode tag2 = new BBCode();
			tag2.setRegex("tag2-regex");
			tag2.setReplace("tag2-replace");
			tag2.setTagName("tag2");
			tag2.enableAlwaysProcess();

			one(formatter).addBb(tag2);
		}});

		File file = new File(this.getClass().getResource("/bb_config_parser.xml").getFile());
		new BBCodeConfigParser(/*file,*/ formatter);

		mock.assertIsSatisfied();
	}
}
