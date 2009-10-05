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

import java.util.Arrays;
import java.util.List;

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.ModerationLog;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PostServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private PostRepository postRepository = context.mock(PostRepository.class);
	private AttachmentService attachmentService = context.mock(AttachmentService.class);
	private PollService pollService = context.mock(PollService.class);
    private TopicRepository topicRepository = context.mock(TopicRepository.class);
    private ModerationLogService moderationLogService = context.mock(ModerationLogService.class);
    private ModerationLog moderationLog = new ModerationLog();
	private PostService service = new PostService(postRepository, attachmentService, pollService, topicRepository, moderationLogService);

	@Test
	public void newOptionsExpectChanges() {
		final Post current = this.createCurrentPost();
		current.getTopic().getFirstPost().setId(1);
		current.setBbCodeEnabled(false);
		current.setHtmlEnabled(false);
		current.setSmiliesEnabled(false);
		current.setSignatureEnabled(false);

		context.checking(new Expectations() {{
			ignoring(attachmentService); ignoring(pollService);
			one(postRepository).get(1); will(returnValue(current));
		}});

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		newPost.setBbCodeEnabled(true);
		newPost.setHtmlEnabled(true);
		newPost.setSmiliesEnabled(true);
		newPost.setSignatureEnabled(true);

		service.update(newPost, false, null, null, moderationLog);

		context.assertIsSatisfied();
		Assert.assertEquals(true, current.isBbCodeEnabled());
		Assert.assertEquals(true, current.isHtmlEnabled());
		Assert.assertEquals(true, current.isSmiliesEnabled());
		Assert.assertEquals(true, current.isSignatureEnabled());
	}

	@Test
	public void changePoll() {
		final Post currentPost = this.createCurrentPost();
		currentPost.getTopic().setPoll(new Poll() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		currentPost.getTopic().getFirstPost().setId(1);

		PollOption pollOption = new PollOption(); pollOption.setText("A");
		final List<PollOption> pollOptions = Arrays.asList(pollOption);

		context.checking(new Expectations() {{
			ignoring(attachmentService);
			one(pollService).processChanges(currentPost.getTopic().getPoll(), pollOptions);
			one(postRepository).get(1); will(returnValue(currentPost));
		}});

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");

		Poll newPoll = new Poll(); newPoll.setLabel("new label"); newPoll.setLength(10);
		newPost.setTopic(new Topic()); newPost.getTopic().setPoll(newPoll);

		service.update(newPost, false, pollOptions, null, moderationLog);

		context.assertIsSatisfied();
		Assert.assertEquals(10, currentPost.getTopic().getPoll().getOptions().size());
		Assert.assertEquals("new label", currentPost.getTopic().getPoll().getLabel());
	}

	@Test
	public void changeFirstPost() {
		final Post current = this.createCurrentPost();
		current.getTopic().setType(Topic.TYPE_NORMAL);
		current.getTopic().getFirstPost().setId(1);

		context.checking(new Expectations() {{
			ignoring(attachmentService); ignoring(pollService);
			one(postRepository).get(1); will(returnValue(current));
		}});

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		newPost.setTopic(new Topic()); newPost.getTopic().setType(Topic.TYPE_STICKY);
		service.update(newPost, true, null, null, moderationLog);

		context.assertIsSatisfied();
		Assert.assertEquals(newPost.getSubject(), current.getTopic().getSubject());
		Assert.assertEquals(Topic.TYPE_STICKY, current.getTopic().getType());
	}

	@Test
	public void changeFirstPostCannotChangeTopicType() {
		final Post current = this.createCurrentPost();
		current.getTopic().setType(Topic.TYPE_NORMAL);
		current.getTopic().getFirstPost().setId(1);

		context.checking(new Expectations() {{
			ignoring(attachmentService); ignoring(pollService);
			one(postRepository).get(1); will(returnValue(current));
		}});

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		newPost.setTopic(new Topic()); newPost.getTopic().setType(Topic.TYPE_STICKY);
		service.update(newPost, false, null, null, moderationLog);

		context.assertIsSatisfied();
		Assert.assertEquals(newPost.getSubject(), current.getTopic().getSubject());
		Assert.assertEquals(Topic.TYPE_NORMAL, current.getTopic().getType());
	}

	@Test
	public void changeUpdatableProperties() {
		final Post current = this.createCurrentPost();

		context.checking(new Expectations() {{
			ignoring(attachmentService); ignoring(pollService);
			one(postRepository).get(1); will(returnValue(current));
		}});

		Post newPost = new Post(); newPost.setId(1); newPost.setText("new text"); newPost.setSubject("new subject");
		service.update(newPost, false, null, null, moderationLog);

		context.assertIsSatisfied();
		Assert.assertEquals(newPost.getSubject(), current.getSubject());
		Assert.assertEquals(newPost.getText(), current.getText());
		Assert.assertEquals(1, current.getEditCount());
		Assert.assertNotNull(current.getEditDate());
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
