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
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class TopicServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private PostRepository postRepository = context.mock(PostRepository.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private PollService pollService = context.mock(PollService.class);
	private AttachmentService attachmentService = context.mock(AttachmentService.class);
	private TopicService topicService = new TopicService(topicRepository, postRepository, forumRepository, attachmentService, pollService);

	@Test
	public void addTopicInvocationsShouldBeInOrder() {
		final Sequence sequence = context.sequence("order");
		final Topic t = context.mock(Topic.class);

		context.checking(new Expectations() {{
			allowing(t).getSubject(); will(returnValue("subject"));
			User user = new User();
			allowing(t).getUser(); will(returnValue(user));

			Forum forum = new Forum(); forum.setId(1);
			allowing(t).getForum(); will(returnValue(forum));

			Post post = context.mock(Post.class);
			allowing(post).getSubject(); will(returnValue("subject"));
			allowing(post).getText(); will(returnValue("text"));

			allowing(t).getFirstPost(); will(returnValue(post));
			Date date = new Date();
			allowing(t).getDate(); will(returnValue(date));

			one(t).setFirstPost(null);
			one(t).setHasAttachment(false);

			one(topicRepository).add(t); inSequence(sequence);
			one(post).setForum(forum); inSequence(sequence);
			one(post).setTopic(t); inSequence(sequence);
			one(post).setDate(date); inSequence(sequence);
			one(post).setUser(user); inSequence(sequence);
			one(post).setSubject("subject"); inSequence(sequence);

			one(postRepository).add(post); inSequence(sequence);
			one(t).setFirstPost(post); inSequence(sequence);
			one(t).setLastPost(post); inSequence(sequence);

			one(t).isWaitingModeration(); will(returnValue(true));
			
			one(pollService).associatePoll(t, Collections.<PollOption>emptyList());
			one(attachmentService).insertAttachments(Collections.<AttachedFile>emptyList(), post);
		}});

		topicService.addTopic(t, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());
		context.assertIsSatisfied();
	}

	@Test
	public void replyPostRepositoryShouldBeCalledBeforeCurrentTopicSetLastPost() {
		final Sequence sequence = context.sequence("order");
		final Post post = new Post(); post.setSubject("subject"); post.setText("msg");
		post.setUser(new User());

		context.checking(new Expectations() {{
			Topic topic = context.mock(Topic.class);
			one(topicRepository).get(1); will(returnValue(topic));

			one(postRepository).add(post); inSequence(sequence);
			one(topic).setLastPost(post); inSequence(sequence);

			allowing(topic).getForum(); will(returnValue(new Forum()));
			one(topic).incrementTotalReplies();

			one(attachmentService).insertAttachments(Collections.<AttachedFile>emptyList(), post);
		}});

		Topic topic = new Topic(); topic.setId(1);
		topicService.reply(topic, post, Collections.<AttachedFile>emptyList());

		context.assertIsSatisfied();
	}


	@Test
	public void replyPostWithoutSubjectShouldUseTopicSubject() {
		final Topic topic = new Topic(); topic.setSubject("topic subject"); topic.setId(1);

		context.checking(new Expectations() {{
			one(topicRepository).get(topic.getId()); will(returnValue(topic));
			ignoring(postRepository);
			one(attachmentService).insertAttachments(with(Collections.<AttachedFile>emptyList()), with(any(Post.class)));
		}});

		Post post = new Post(); post.setText("122"); post.setSubject(null); post.setUser(new User());
		topicService.reply(topic, post, Collections.<AttachedFile>emptyList());
		context.assertIsSatisfied();

		Assert.assertEquals(topic.getSubject(), post.getSubject());
	}

	@Test
	public void replyModeratedPostShouldNotUpdateSomeProperties() {
		final Topic topic = this.newTopic();
		int currentTotalReplies = topic.getTotalReplies();
		topic.setLastPost(new Post() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }});
		Forum forum = new Forum() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); setLastPost(new Post() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(5); }}); }};

		context.checking(new Expectations() {{
			one(topicRepository).get(topic.getId()); will(returnValue(topic));
			ignoring(postRepository);
			ignoring(attachmentService);
		}});

		Post post = new Post(); post.setSubject("s1"); post.setText("t1");
		post.setDate(null); post.setTopic(null); post.setModerate(true); post.setUser(new User());

		topicService.reply(topic, post, Collections.<AttachedFile>emptyList());
		context.assertIsSatisfied();

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
		final Forum forum = new Forum() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }};

		context.checking(new Expectations() {{
			ignoring(topicRepository);
			ignoring(postRepository);
			ignoring(pollService);
			ignoring(attachmentService);
		}});

		topicService.addTopic(topic, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());
		context.assertIsSatisfied();
		Assert.assertEquals(0, topic.getUser().getTotalPosts());
		Assert.assertNull(forum.getLastPost());
	}

	@Test(expected = IllegalStateException.class)
	public void replyWithNullPostTextExpectsException() {
		context.checking(new Expectations() {{
			Topic t = new Topic(); t.setSubject("a");
			one(topicRepository).get(0); will(returnValue(t));
		}});

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

		context.checking(new Expectations() {{
			one(topicRepository).get(1); will(returnValue(current));
			one(postRepository).add(post);
			ignoring(attachmentService);
		}});

		Topic tempTopic = new Topic(); tempTopic.setId(1);

		topicService.reply(tempTopic, post, Collections.<AttachedFile>emptyList());
		context.assertIsSatisfied();

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

		final Forum forum = new Forum() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }};

		context.checking(new Expectations() {{
			one(forumRepository).get(1); will(returnValue(forum));
			one(topicRepository).add(topic);
			one(postRepository).add(topic.getFirstPost());
			ignoring(pollService);
			ignoring(attachmentService);
		}});

		topicService.addTopic(topic, Collections.<PollOption>emptyList(), Collections.<AttachedFile>emptyList());

		context.assertIsSatisfied();

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

		topic.setSubject("topic 1");
		topic.getForum().setId(1);
		topic.setUser(new User() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		topic.setFirstPost(new Post());
		topic.getFirstPost().setSubject("123");
		topic.getFirstPost().setText("some message");
		topic.getFirstPost().setSignatureEnabled(true);

		return topic;
	}
}
