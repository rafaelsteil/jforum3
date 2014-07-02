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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import net.jforum.entities.BadWord;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.BadWordRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class BadWordEventTestCase {
	
	@Mock private BadWordRepository repository;
	@InjectMocks private BadWordEvent event;

	@Test
	public void replaceAllShouldNotReplaceInsideAnotherWord() {
		BadWord w1 = new BadWord(); w1.setWord("abc"); w1.setReplacement("REPLACEMENT");
	
		when(repository.getAll()).thenReturn(Arrays.asList(w1));
		
		Post p = new Post();
		Topic topic = new Topic();
		topic.setSubject("title");
		p.setTopic(topic);
		p.setText("some content wordABCeditor more content");

		event.beforeAdd(p);
		
		assertEquals("some content wordABCeditor more content", p.getText());
	}

	@Test
	public void replaceAll() {
		BadWord w1 = new BadWord(); w1.setWord("word1"); w1.setReplacement("replacement1");
		BadWord w2 = new BadWord(); w2.setWord("word2"); w2.setReplacement("replacement2");

		when(repository.getAll()).thenReturn(Arrays.asList(w1, w2));
		
		Post p = new Post();
		Topic topic = new Topic();
		topic.setSubject("title");
		p.setTopic(topic);
		p.setText("some content of post 1. This is word1, and this is WORD2. End");

		event.beforeAdd(p);
		
		assertEquals("some content of post 1. This is replacement1, and this is replacement2. End", p.getText());
	}
}
