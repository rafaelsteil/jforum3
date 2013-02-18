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


import static org.mockito.Mockito.*;

import java.util.Arrays;

import net.jforum.entities.Smilie;
import net.jforum.formatters.PostOptions;
import net.jforum.formatters.SmiliesFormatter;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class SmiliesFormatterTestCase {
	@Mock private SmilieRepository repository;
	@Mock private JForumConfig config;
	@Mock private Container container;

	@Test
	public void expectAllReplaces() {
		Smilie s1 = new Smilie();
		s1.setCode(":)");
		s1.setDiskName("#s1#");

		Smilie s2 = new Smilie();
		s2.setCode(":D");
		s2.setDiskName("#s2#");
		
		when(container.instanceFor(SmilieRepository.class)).thenReturn(repository);
		when(repository.getAllSmilies()).thenReturn(Arrays.asList(s1, s2));
		when(config.getValue(ConfigKeys.SMILIE_IMAGE_DIR)).thenReturn("smilies");
		
		String input = "some text :). And another :D :):). This one not: :P";
		String expected = "some text <img src='/smilies/#s1#' border='0'/>. " +
				"And another <img src='/smilies/#s2#' border='0'/> <img src=\'/smilies/#s1#\' border=\'0\'/>" +
				"<img src=\'/smilies/#s1#\' border=\'0\'/>. This one not: :P";
		PostOptions options = new PostOptions(false, true, false, false, "");

		SmiliesFormatter formatter = new SmiliesFormatter(config, container);
		Assert.assertEquals(expected, formatter.format(input, options));

	}

	@Test
	public void smliesDisabledShouldNotFormat() {
		String input = "some :) smiles :D here";
		String expected = input;

		PostOptions options = new PostOptions(false, false, false, false, null);

		Assert.assertEquals(expected, new SmiliesFormatter(null, container).format(input, options));
	}
}
