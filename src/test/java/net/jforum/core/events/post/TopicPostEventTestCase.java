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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;

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
public class TopicPostEventTestCase {
	
	@Mock private TopicRepository repository;
	@Mock private UserRepository userRepository;
	@InjectMocks private TopicPostEvent event;

	@Test
	public void shouldUpdateUserTotalPost() {
		final Post post = this.newPost();
		post.getUser().setTotalPosts(5);
	
		when(repository.getTotalPosts(post.getTopic())).thenReturn(1);
		when(userRepository.getTotalPosts(post.getUser())).thenReturn(2);
		when(repository.getFirstPost(any(Topic.class))).thenReturn(newPost());
	
		event.deleted(post);
		
		Assert.assertEquals(2, post.getUser().getTotalPosts());
	}

	@Test
	public void emptyPostsShouldRemoveTopic() {
		final Post post = this.newPost();
		int totalPosts = post.getTopic().getTotalPosts();
		
		when(repository.getTotalPosts(post.getTopic())).thenReturn(0);
		
		event.deleted(post);
		
		verify(repository).remove(post.getTopic());
		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	@Test
	public void removeLastPostOnly() {
		final Post post = this.newPost();
		post.getTopic().getFirstPost().setId(3);
		post.getTopic().getLastPost().setId(4);
		int totalPosts = post.getTopic().getTotalPosts();
	
		Post lastPost = new Post(); lastPost.setId(5);
		when(repository.getTotalPosts(post.getTopic())).thenReturn(2);
		when(repository.getLastPost(post.getTopic())).thenReturn(lastPost);
		
		post.setId(4);
		event.deleted(post);
		
		verify(userRepository).getTotalPosts(post.getUser());
		Post expected = new Post(); expected.setId(5);
		Assert.assertEquals(expected, post.getTopic().getLastPost());
		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	@Test
	public void removeFirstPostOnly() {
		final Post post = this.newPost();
		int totalPosts = post.getTopic().getTotalPosts();
	
		Post newFirst = newPost(); newFirst.setId(6);
		newFirst.getUser().setId(9);

		when(repository.getTotalPosts(post.getTopic())).thenReturn(2);
		when(repository.getFirstPost(post.getTopic())).thenReturn(newFirst);
		
		event.deleted(post);
		
		verify(userRepository).getTotalPosts(post.getUser());
		
		Post expected = new Post(); expected.setId(6);
		Assert.assertEquals(expected, post.getTopic().getFirstPost());

		User expectedUser = new User(); expectedUser.setId(9);
		Assert.assertEquals(expectedUser, post.getTopic().getUser());
		Assert.assertEquals(totalPosts - 1, post.getTopic().getTotalPosts());
	}

	private Post newPost() {
		Post p = new Post(); p.setId(1); p.setUser(new User());
		Topic t = new Topic(); t.setId(1);
		t.setUser(new User());
		t.setFirstPost(new Post()); t.getFirstPost().setId(1);
		t.setLastPost(new Post()); t.getLastPost().setId(2);
		p.setTopic(t);

		return p;
	}
}
