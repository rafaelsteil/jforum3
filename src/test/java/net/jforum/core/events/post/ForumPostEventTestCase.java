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

import static org.mockito.Mockito.*;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumPostEventTestCase {
	
	@Mock private ForumRepository repository;
	@InjectMocks private ForumPostEvent event;

	@Test
	public void deleteLastPostExpectUpdate() {
		final Post post = this.newPost();
		post.setId(2);
	
		Post newLastPost = new Post(); newLastPost.setId(13);
		when(repository.getLastPost(post.getForum())).thenReturn(newLastPost);
	
		event.deleted(post);
		
		Post expected = new Post(); expected.setId(13);
		Assert.assertEquals(expected, post.getForum().getLastPost());
	}

	@Test
	public void deleteOrdinaryPostShouldDoNothing() {
		Post post = this.newPost();
		post.setId(10);
		event.deleted(post);
		
	}

	private Post newPost() {
		Post p = new Post(); p.setId(1);
		Topic t = new Topic();
		t.setId(1);
		t.setFirstPost(new Post()); t.getFirstPost().setId(1);
		t.setLastPost(new Post()); t.getLastPost().setId(2);
		t.setForum(new Forum()); t.getForum().setId(1); t.getForum().setLastPost(t.getLastPost());
		p.setForum(t.getForum());
		p.setTopic(t);

		return p;
	}
}
