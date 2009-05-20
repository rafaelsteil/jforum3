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
package net.jforum.core.events.post;

import java.util.Arrays;

import junit.framework.Assert;
import net.jforum.entities.BadWord;
import net.jforum.entities.Post;
import net.jforum.repository.BadWordRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class BadWordEventTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private BadWordRepository repository = context.mock(BadWordRepository.class);
	private BadWordEvent event = new BadWordEvent(repository);

	@Test
	public void replaceAllShouldNotReplaceInsideAnotherWord() {
		context.checking(new Expectations() {{
			BadWord w1 = new BadWord(); w1.setWord("abc"); w1.setReplacement("REPLACEMENT");

			one(repository).getAll(); will(returnValue(Arrays.asList(w1)));
		}});

		Post p = new Post();
		p.setText("some content wordABCeditor more content");

		event.beforeAdd(p);
		context.assertIsSatisfied();

		Assert.assertEquals("some content wordABCeditor more content", p.getText());
	}

	@Test
	public void replaceAll() {
		context.checking(new Expectations() {{
			BadWord w1 = new BadWord(); w1.setWord("word1"); w1.setReplacement("replacement1");
			BadWord w2 = new BadWord(); w2.setWord("word2"); w2.setReplacement("replacement2");

			one(repository).getAll(); will(returnValue(Arrays.asList(w1, w2)));
		}});

		Post p = new Post();
		p.setText("some content of post 1. This is word1, and this is WORD2. End");

		event.beforeAdd(p);
		context.assertIsSatisfied();

		Assert.assertEquals("some content of post 1. This is replacement1, and this is replacement2. End", p.getText());
	}
}
