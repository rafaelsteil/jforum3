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

import java.util.Arrays;

import net.jforum.entities.Smilie;
import net.jforum.formatters.PostOptions;
import net.jforum.formatters.SmiliesFormatter;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SmiliesFormatterTestCase {
	private Mockery mock = TestCaseUtils.newMockery();
	private SmilieRepository repository = mock.mock(SmilieRepository.class);
	private JForumConfig config = mock.mock(JForumConfig.class);

	@Test
	public void expectAllReplaces() {
		mock.checking(new Expectations() {{
			Smilie s1 = new Smilie(); s1.setCode(":)"); s1.setDiskName("#s1#");
			Smilie s2 = new Smilie(); s2.setCode(":D"); s2.setDiskName("#s2#");

			one(repository).getAllSmilies(); will(returnValue(Arrays.asList(s1, s2)));
			exactly(2).of(config).getValue(ConfigKeys.SMILIE_IMAGE_DIR); will(returnValue("smilies"));
		}});

		String input = "some text :). And another :D :):). This one not: :P";
		String expected = "some text <img src='/smilies/#s1#' border='0'/>. " +
				"And another <img src='/smilies/#s2#' border='0'/> <img src=\'/smilies/#s1#\' border=\'0\'/>" +
				"<img src=\'/smilies/#s1#\' border=\'0\'/>. This one not: :P";
		PostOptions options = new PostOptions(false, true, false, false, "");

		SmiliesFormatter formatter = new SmiliesFormatter(config, null);
		Assert.assertEquals(expected, formatter.format(input, options));

		mock.assertIsSatisfied();
	}

	@Test
	public void smliesDisabledShouldNotFormat() {
		String input = "some :) smiles :D here";
		String expected = input;

		PostOptions options = new PostOptions(false, false, false, false, null);

		Assert.assertEquals(expected, new SmiliesFormatter(null, null).format(input, options));
	}
}
