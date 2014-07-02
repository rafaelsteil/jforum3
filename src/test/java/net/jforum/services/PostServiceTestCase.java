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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import net.jforum.entities.ModerationLog;
import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class PostServiceTestCase {

	@Mock private PostRepository postRepository;
	@Mock private AttachmentService attachmentService;
	@Mock private PollService pollService;
	@Mock private TopicRepository topicRepository;
	@Mock private ModerationLogService moderationLogService;
	private ModerationLog moderationLog = new ModerationLog();
	@InjectMocks private PostService service;

	@Test
	public void newOptionsExpectChanges() {
		final Post current = this.createCurrentPost();
		current.getTopic().getFirstPost().setId(1);
		current.setBbCodeEnabled(false);
		current.setHtmlEnabled(false);
		current.setSmiliesEnabled(false);
		current.setSignatureEnabled(false);

		when(postRepository.get(1)).thenReturn(current);

		Post newPost = new Post();
		newPost.setId(1);
		newPost.setText("new text");
		newPost.setSubject("new subject");
		newPost.setBbCodeEnabled(true);
		newPost.setHtmlEnabled(true);
		newPost.setSmiliesEnabled(true);
		newPost.setSignatureEnabled(true);
		newPost.setTopic(new Topic());

		service.update(newPost, false, null, null, moderationLog);

		verify(postRepository).update(current);
		verify(topicRepository).update(current.getTopic());
		assertEquals(true, current.isBbCodeEnabled());
		assertEquals(true, current.isHtmlEnabled());
		assertEquals(true, current.isSmiliesEnabled());
		assertEquals(true, current.isSignatureEnabled());
	}

	@Test
	public void changePoll() {
		final Post currentPost = this.createCurrentPost();
		Poll poll = new Poll();
		poll.setId(1);
		currentPost.getTopic().setPoll(poll); 
		currentPost.getTopic().getFirstPost().setId(1);

		PollOption pollOption = new PollOption(); pollOption.setText("A");
		final List<PollOption> pollOptions = Arrays.asList(pollOption);

		when(postRepository.get(1)).thenReturn(currentPost);

		Post newPost = new Post();
		newPost.setId(1);
		newPost.setText("new text");
		newPost.setSubject("new subject");

		Poll newPoll = new Poll();
		newPoll.setLabel("new label");
		newPoll.setLength(10);
		newPost.setTopic(new Topic());
		newPost.getTopic().setPoll(newPoll);

		service.update(newPost, false, pollOptions, null, moderationLog);

		verify(pollService).processChanges(currentPost.getTopic().getPoll(), pollOptions);
		verify(postRepository).update(currentPost);
		verify(topicRepository).update(currentPost.getTopic());
		assertEquals(10, currentPost.getTopic().getPoll().getLength());
		assertEquals("new label", currentPost.getTopic().getPoll().getLabel());
	}

	@Test
	public void changeFirstPost() {
		final Post current = this.createCurrentPost();
		current.getTopic().setType(Topic.TYPE_NORMAL);
		current.getTopic().getFirstPost().setId(1);

		when(postRepository.get(1)).thenReturn(current);

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		newPost.setTopic(new Topic()); newPost.getTopic().setType(Topic.TYPE_STICKY);
		service.update(newPost, true, null, null, moderationLog);

		verify(postRepository).update(current);
		verify(topicRepository).update(current.getTopic());
		assertEquals(newPost.getSubject(), current.getTopic().getSubject());
		assertEquals(Topic.TYPE_STICKY, current.getTopic().getType());
	}

	@Test
	public void changeFirstPostCannotChangeTopicType() {
		final Post current = this.createCurrentPost();
		current.getTopic().setType(Topic.TYPE_NORMAL);
		current.getTopic().getFirstPost().setId(1);

		when(postRepository.get(1)).thenReturn(current);

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		newPost.setTopic(new Topic()); newPost.getTopic().setType(Topic.TYPE_STICKY);
		service.update(newPost, false, null, null, moderationLog);

		verify(postRepository).update(current);
		verify(topicRepository).update(current.getTopic());
		assertEquals(newPost.getSubject(), current.getTopic().getSubject());
		assertEquals(Topic.TYPE_NORMAL, current.getTopic().getType());
	}

	@Test
	public void changeUpdatableProperties() {
		final Post current = this.createCurrentPost();

		when(postRepository.get(1)).thenReturn(current);

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		service.update(newPost, false, null, null, moderationLog);

		verify(postRepository).update(current);
		verify(topicRepository).update(current.getTopic());
		assertEquals(newPost.getSubject(), current.getSubject());
		assertEquals(newPost.getText(), current.getText());
		assertEquals(1, current.getEditCount());
		assertNotNull(current.getEditDate());
	}

	@Test(expected = IllegalStateException.class)
	public void withoutIdExpectsException() {
		Post p = new Post(); p.setId(0); p.setSubject("aa"); p.setText("bb");
		service.update(p, false, null, null, moderationLog);
	}

	@Test(expected = IllegalStateException.class)
	public void emptyTextExpectsException() {
		Post p = new Post(); p.setId(1); p.setSubject("aa"); p.setText("");
		service.update(p, false, null, null, moderationLog);
	}

	@Test(expected = IllegalStateException.class)
	public void nullTextExpectsException() {
		Post p = new Post(); p.setId(1); p.setSubject("bb"); p.setText(null);
		service.update(p, false, null, null, moderationLog);
	}

	@Test(expected = IllegalStateException.class)
	public void emptySubjectExpectsException() {
		Post p = new Post(); p.setId(1); p.setSubject(""); p.setText("xx");
		service.update(p, false, null, null, moderationLog);
	}

	@Test(expected = IllegalStateException.class)
	public void nullSubjectExpectsException() {
		Post p = new Post(); p.setId(1); p.setSubject(null); p.setText("ee");
		service.update(p, false, null, null, moderationLog);
	}

	@Test(expected = NullPointerException.class)
	public void nullPostExpectException() {
		service.update(null, false, null, null, moderationLog);
	}

	private Post createCurrentPost() {
		Post post = new Post();
		post.setId(1);

		Topic topic = new Topic();
		topic.setFirstPost(new Post());
		post.setTopic(topic);

		return post;
	}
}
