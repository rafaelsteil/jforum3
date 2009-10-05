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

import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.ModerationLog;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.ModerationLogRepository;
import net.jforum.util.TestCaseUtils;
import net.jforum.util.JForumConfig;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ModerationServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private PostRepository postRepository = context.mock(PostRepository.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
    private JForumConfig jForumConfig = context.mock(JForumConfig.class);
    private ModerationLog moderationLog = new ModerationLog();
    private ModerationLogRepository moderationLogRepository = context.mock(ModerationLogRepository.class);
    private ModerationLogService moderationLogService = new ModerationLogService(jForumConfig, moderationLogRepository, topicRepository);
	private ModerationService service = new ModerationService(postRepository, forumRepository, topicRepository, moderationLogService);
	private States state = context.states("state");

	@Test
	public void moveTopics() {
		state.become("move");

		final Forum oldForum = new Forum(); oldForum.setId(1); oldForum.setLastPost(null);
		final Forum targetForum = new Forum(); targetForum.setId(2); targetForum.setLastPost(null);
		final Topic topic = new Topic(); topic.setId(3); topic.setMovedId(0); topic.setForum(oldForum);

		context.checking(new Expectations() {{
			one(forumRepository).get(2); will(returnValue(targetForum));
			one(topicRepository).get(3); will(returnValue(topic));

			one(forumRepository).moveTopics(targetForum, topic.getId());

			one(forumRepository).getLastPost(oldForum); will(returnValue(new Post() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(5); }}));
			one(forumRepository).getLastPost(targetForum); will(returnValue(new Post() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(6); }}));
		}});

		service.moveTopics(2, moderationLog, 3);
		context.assertIsSatisfied();
		Assert.assertEquals(targetForum.getLastPost(), new Post() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(6); }});
		Assert.assertEquals(oldForum.getLastPost(), new Post() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(5); }});
	}

	@Test
	public void moveTopicsEmptyListShouldIgnore() {
		state.become("move");
		service.moveTopics(1, null);
	}

	@Test
	public void lockUnlock() {
		final Topic lockedTopic = new Topic(); lockedTopic.lock();
		final Topic unlockedTopic = new Topic(); unlockedTopic.unlock();

		context.checking(new Expectations() {{
			one(topicRepository).get(1); will(returnValue(lockedTopic));
			one(topicRepository).get(2); will(returnValue(unlockedTopic));
		}});

        int[] ids = {1, 2};
		service.lockUnlock(ids, moderationLog);
		context.assertIsSatisfied();
		Assert.assertFalse(lockedTopic.isLocked());
		Assert.assertTrue(unlockedTopic.isLocked());
	}

	@Test
	public void lockUnlockNullIdsShouldIgnore() {
		service.lockUnlock(null, moderationLog);
	}

	@Test
	public void deleteTopics() {
		final Topic topic = new Topic(); topic.setId(1);

		context.checking(new Expectations() {{
			one(topicRepository).remove(topic);
		}});

		service.deleteTopics(Arrays.asList(topic), moderationLog);
		context.assertIsSatisfied();
	}

	@Test
	public void reject() {
		context.checking(new Expectations() {{
			Post post = new Post() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(1); }};
			one(postRepository).get(1); will(returnValue(post));
			one(postRepository).remove(post);
		}});

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.REJECT);
		service.doApproval(1, this.asList(info));
		context.assertIsSatisfied();
	}

	@Test
	public void approveEntireTopicIsWaitingModerationShouldChangeTopicStatusAndNotIncrementTotalRepliesAndTotalUserPosts() {
		final Post post = new Post(); post.setId(1); post.setModerate(true); post.setUser(new User());
		Topic topic = new Topic(); topic.setPendingModeration(true); topic.setLastPost(null);
		post.setTopic(topic);

		context.checking(new Expectations() {{
			one(postRepository).get(1); will(returnValue(post));
			one(topicRepository).getLastPost(post.getTopic()); will(returnValue(post));
		}});

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.APPROVE);
		service.doApproval(1, this.asList(info));
		context.assertIsSatisfied();
		Assert.assertEquals(1, post.getUser().getTotalPosts());
		Assert.assertFalse(topic.isWaitingModeration());
		Assert.assertFalse(post.isWaitingModeration());
		Assert.assertEquals(0, topic.getTotalReplies());
		Assert.assertEquals(post, topic.getLastPost());
	}

	@Test
	public void approvePostInExistingTopicShouldIncrementTotalRepliesAndTotalUserPosts() {
		Topic topic = new Topic(); topic.setPendingModeration(false); topic.setLastPost(new Post() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(2); }});

		final Post post = new Post(); post.setId(1); post.setModerate(true);
		post.setTopic(topic); post.setUser(new User());

		context.checking(new Expectations() {{
			one(postRepository).get(1); will(returnValue(post));
			one(topicRepository).getLastPost(post.getTopic()); will(returnValue(post));
		}});

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.APPROVE);
		service.doApproval(1, this.asList(info));
		context.assertIsSatisfied();
		Assert.assertEquals(1, post.getUser().getTotalPosts());
		Assert.assertFalse(post.isWaitingModeration());
		Assert.assertEquals(1, topic.getTotalReplies());
		Assert.assertEquals(post, topic.getLastPost());
	}

	@Test
	public void deferShouldDoNothing() {
		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.DEFER);
		service.doApproval(1, this.asList(info));
	}

	@Test
	public void approveNullInfoShouldIgnore() {
		service.doApproval(1, null);
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(forumRepository).get(1); will(returnValue(new Forum())); when(state.isNot("move"));
			allowing(forumRepository); when(state.isNot("move"));
		}});
	}

	private List<ApproveInfo> asList(ApproveInfo info) {
		return Arrays.asList(info);
	}
}
