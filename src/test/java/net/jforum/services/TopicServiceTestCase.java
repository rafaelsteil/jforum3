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
package net.jforum.services;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;

import net.jforum.actions.helpers.AttachedFile;
import net.jforum.entities.Forum;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class TopicServiceTestCase {
	
	@Mock private TopicRepository topicRepository;
	@Mock private PostRepository postRepository;
	@Mock private ForumRepository forumRepository;
	@Mock private PollService pollService;
	@Mock private AttachmentService attachmentService;
	@Mock private UserRepository userRepository;
	@InjectMocks private TopicService topicService;

	@Test
	public void addTopicInvocationsShouldBeInOrder() {
		final Topic t = mock(Topic.class);

		when(t.getSubject()).thenReturn("subject");
		User user = new User();
		when(t.getUser()).thenReturn(user);
	
		Forum forum = new Forum(); forum.setId(1);
		when(t.getForum()).thenReturn(forum);
	
		Post post = mock(Post.class);
		when(post.getSubject()).thenReturn("subject");
		when(post.getText()).thenReturn("text");
		when(t.getFirstPost()).thenReturn(post);
		Date date = new Date();
		when(t.getDate()).thenReturn(date);
		when(t.isWaitingModeration()).thenReturn(true);
		
		
		topicService.addTopic(t, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());
		
	
		verify(t).setFirstPost(null);
		verify(t).setHasAttachment(false);
	
		InOrder inOrder = inOrder(topicRepository, postRepository, post, t);
		inOrder.verify(topicRepository).add(t); 
		inOrder.verify(post).setForum(forum); 
		inOrder.verify(post).setTopic(t); 
		inOrder.verify(post).setDate(date); 
		inOrder.verify(post).setUser(user); 
		inOrder.verify(post).setSubject("subject"); 
	
		inOrder.verify(postRepository).add(post);
		inOrder.verify(t).setFirstPost(post); 
		inOrder.verify(t).setLastPost(post); 
	
		verify(pollService).associatePoll(t, Collections.<PollOption>emptyList());
		verify(attachmentService).insertAttachments(Collections.<AttachedFile>emptyList(), post);
	}

	@Test
	public void replyPostRepositoryShouldBeCalledBeforeCurrentTopicSetLastPost() {
		final Post post = new Post(); post.setSubject("subject"); post.setText("msg");
		post.setUser(new User());
		
		Topic topic = mock(Topic.class);
		when(topicRepository.get(1)).thenReturn(topic);
		when(topic.getForum()).thenReturn(new Forum());
		
		
		Topic topicCheck = new Topic(); topicCheck.setId(1);
		topicService.reply(topicCheck, post, Collections.<AttachedFile>emptyList());
		
		
		InOrder inOrder = inOrder(postRepository, topic);
		inOrder.verify(postRepository).add(post); 
		inOrder.verify(topic).setLastPost(post); 
		
		verify(topic).incrementTotalReplies();
		verify(attachmentService).insertAttachments(Collections.<AttachedFile>emptyList(), post);
	}


	@Test
	public void replyPostWithoutSubjectShouldUseTopicSubject() {
		final Topic topic = new Topic(); topic.setSubject("topic subject"); topic.setId(1);
		when(topicRepository.get(topic.getId())).thenReturn(topic);
		
		
		Post post = new Post(); post.setText("122"); post.setSubject(null); post.setUser(new User());
		topicService.reply(topic, post, Collections.<AttachedFile>emptyList());
		
		
		verify(attachmentService).insertAttachments(eq(Collections.<AttachedFile>emptyList()), any(Post.class));
		Assert.assertEquals(topic.getSubject(), post.getSubject());
	}

	@Test
	public void replyModeratedPostShouldNotUpdateSomeProperties() {
		final Topic topic = this.newTopic();
		int currentTotalReplies = topic.getTotalReplies();
		Post post2 = new Post();
		post2.setId(2);
		Post post5 = new Post();
		post5.setId(5);
		
		topic.setLastPost(post2);
		Forum forum = new Forum(1);
		forum.setLastPost(post5);

		when(topicRepository.get(topic.getId())).thenReturn(topic);

		
		Post post = new Post(); post.setSubject("s1"); post.setText("t1");
		post.setDate(null); post.setTopic(null); post.setModerate(true); post.setUser(new User());

		topicService.reply(topic, post, Collections.<AttachedFile>emptyList());
		
		
		Assert.assertEquals(0, post.getUser().getTotalPosts());
		Assert.assertEquals(topic, post.getTopic());
		Assert.assertEquals(currentTotalReplies, topic.getTotalReplies());
		Assert.assertFalse(forum.getLastPost().equals(post));
		Assert.assertFalse(topic.getLastPost().equals(post));
	}

	@Test
	public void addModeratedTopicShouldNotUpdateForumLastPost() {
		final Topic topic = this.newTopic();
		topic.setPendingModeration(true);
		final Forum forum = new Forum(1);
		
		topicService.addTopic(topic, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());
		
		Assert.assertEquals(0, topic.getUser().getTotalPosts());
		Assert.assertNull(forum.getLastPost());
	}

	@Test(expected = IllegalStateException.class)
	public void replyWithNullPostTextExpectsException() {
		Topic t = new Topic(); t.setSubject("a");
		when(topicRepository.get(0)).thenReturn(t);
		

		Post p = new Post();
		p.setSubject("123");
		p.setText(null);

		topicService.reply(new Topic(), p, null);
	}

	@Test
	public void replyPostExpectsChangesToUpdatableProperties() {
		final Post post = new Post(); post.setUser(new User());
		post.setSubject("s1"); post.setText("t1"); post.setDate(null); post.setTopic(null);
		final Topic current = new Topic(); current.setId(1); current.setForum(new Forum());
		int currentTotalReplies = current.getTotalReplies();
		
		when(topicRepository.get(1)).thenReturn(current);
			
		
		Topic tempTopic = new Topic(); tempTopic.setId(1);
		topicService.reply(tempTopic, post, Collections.<AttachedFile>emptyList());
		
		
		verify(postRepository).add(post);
		Assert.assertEquals(1, post.getUser().getTotalPosts());
		Assert.assertNotNull(post.getDate());
		Assert.assertEquals(current, post.getTopic());
		Assert.assertEquals(post, post.getTopic().getLastPost());
		Assert.assertEquals(post, current.getForum().getLastPost());
		Assert.assertEquals(currentTotalReplies + 1, current.getTotalReplies());
	}

	@Test(expected = IllegalStateException.class)
	public void addNewTopicWithNullUserExpectException() {
		Topic t = new Topic();
		t.setUser(null);

		topicService.addTopic(t, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void addNewTopicWithoutSubjectExpectException() {
		Topic t = new Topic();
		t.setUser(new User());
		t.setSubject(null);

		topicService.addTopic(t, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void addNewTopicWithoutPostSubjectExpectsException() {
		Topic t = new Topic();
		t.setUser(new User());
		t.setSubject("123");
		t.setFirstPost(new Post());
		t.getFirstPost().setSubject(null);

		topicService.addTopic(t, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void addNewTopicWithoutPostTextExpectsException() {
		Topic t = new Topic();
		t.setUser(new User());
		t.setSubject("123");
		t.setFirstPost(new Post());
		t.getFirstPost().setSubject("123");
		t.getFirstPost().setText(null);

		topicService.addTopic(t, null, null);
	}

	@Test
	public void addNewTopicShouldSaveFirstPostAndAllRelatedObjecUpdates() {
		final Topic topic = this.newTopic();
		final Forum forum = new Forum(1);
		
		when(forumRepository.get(1)).thenReturn(forum);
		when(userRepository.getTotalPosts(topic.getUser())).thenReturn(1);
		
		
		topicService.addTopic(topic, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());
		
		
		verify(topicRepository).add(topic);
		verify(postRepository).add(topic.getFirstPost());
		Assert.assertEquals(1, topic.getUser().getTotalPosts());
		Assert.assertTrue(topic.getLastPost() == topic.getFirstPost());
		Assert.assertTrue(topic == topic.getFirstPost().getTopic());
		Assert.assertTrue(topic == topic.getLastPost().getTopic());
		Assert.assertNotNull(topic.getDate());
		Assert.assertNotNull(topic.getFirstPost().getDate());
		Assert.assertNotNull(topic.getLastPost().getDate());
		Assert.assertEquals(topic.getDate(), topic.getFirstPost().getDate());
		Assert.assertEquals(topic.getDate(), topic.getLastPost().getDate());
		Assert.assertEquals(topic.getFirstPost(), forum.getLastPost());
	}

	private Topic newTopic() {
		Topic topic = new Topic();
		User user = new User();
		user.setId(1);
		
		topic.setSubject("topic 1");
		topic.getForum().setId(1);
		topic.setUser(user);
		topic.setFirstPost(new Post());
		topic.getFirstPost().setSubject("123");
		topic.getFirstPost().setText("some message");
		topic.getFirstPost().setSignatureEnabled(true);

		return topic;
	}
}
