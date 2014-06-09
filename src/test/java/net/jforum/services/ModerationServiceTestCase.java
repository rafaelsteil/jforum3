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

import java.util.Arrays;
import java.util.List;

import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.entities.Forum;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.ModerationLogRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class ModerationServiceTestCase {

	@Mock private ForumRepository forumRepository;
	@Mock private PostRepository postRepository;
	@Mock private TopicRepository topicRepository;
	@Mock private JForumConfig config;
	private ModerationLog moderationLog = new ModerationLog();
	@Mock private ModerationLogRepository moderationLogRepository;
	private ModerationLogService moderationLogService;
	private ModerationService service;
	//private States state = context.states("state");
	private Post post1 = new Post();
	private Post post2 = new Post();
	private Post post5 = new Post();
	private Post post6 = new Post();

	@Before
	public void setup() {
		moderationLogService = new ModerationLogService(config, moderationLogRepository, topicRepository);
		service = new ModerationService(postRepository, forumRepository, topicRepository, moderationLogService);
		
		post1.setId(1);
		post2.setId(2);
		post5.setId(5);
		post6.setId(6);
		
		when(forumRepository.get(1)).thenReturn(new Forum()); //when(state.isNot("move");
	//	allowing(forumRepository); when(state.isNot("move"));
	}

	@Test
	public void moveTopics() {
	//	state.become("move");

		final Forum oldForum = new Forum(); oldForum.setId(1); oldForum.setLastPost(null);
		final Forum targetForum = new Forum(); targetForum.setId(2); targetForum.setLastPost(null);
		final Topic topic = new Topic(); topic.setId(3); topic.setMovedId(0); topic.setForum(oldForum);

		when(config.getBoolean(ConfigKeys.MODERATION_LOGGING_ENABLED)).thenReturn(true);
		when(forumRepository.get(2)).thenReturn(targetForum);
		when(topicRepository.get(3)).thenReturn(topic);
		when(forumRepository.getLastPost(oldForum)).thenReturn(post5);
		when(forumRepository.getLastPost(targetForum)).thenReturn(post6);

		service.moveTopics(2, moderationLog, 3);

		verify(moderationLogRepository).add(any(ModerationLog.class));
		verify(forumRepository).moveTopics(targetForum, topic.getId());
		assertEquals(targetForum.getLastPost(), post6); 
		assertEquals(oldForum.getLastPost(), post5); 
	}

	@Test
	public void moveTopicsEmptyListShouldIgnore() {
		service.moveTopics(1, null);
		
		verifyZeroInteractions(forumRepository);
	}

	@Test
	public void lockUnlock() {
		final Topic lockedTopic = new Topic(); lockedTopic.lock();
		final Topic unlockedTopic = new Topic(); unlockedTopic.unlock();


		when(config.getBoolean(ConfigKeys.MODERATION_LOGGING_ENABLED)).thenReturn(false);
		when(topicRepository.get(1)).thenReturn(lockedTopic);
		when(topicRepository.get(2)).thenReturn(unlockedTopic);


		int[] ids = {1, 2};
		service.lockUnlock(ids, moderationLog);

		assertFalse(lockedTopic.isLocked());
		assertTrue(unlockedTopic.isLocked());
	}

	@Test
	public void lockUnlockNullIdsShouldIgnore() {
		service.lockUnlock(null, moderationLog);
	}

	@Test
	public void deleteTopics() {
		final Topic topic = new Topic(); topic.setId(1);

		when(config.getBoolean(ConfigKeys.MODERATION_LOGGING_ENABLED)).thenReturn(false);
		when(topicRepository.get(1)).thenReturn(topic);

		service.deleteTopics(Arrays.asList(topic), moderationLog);
		
		verify(topicRepository).remove(topic);
	}

	@Test
	public void reject() {
		when(postRepository.get(1)).thenReturn(post1);

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.REJECT);
		service.doApproval(1, this.asList(info));

		verify(postRepository).remove(post1);
	}

	@Test
	public void approveEntireTopicIsWaitingModerationShouldChangeTopicStatusAndNotIncrementTotalRepliesAndTotalUserPosts() {
		final Post post = new Post(); post.setId(1); post.setModerate(true); post.setUser(new User());
		Topic topic = new Topic(); topic.setPendingModeration(true); topic.setLastPost(null);
		post.setTopic(topic);

		when(postRepository.get(1)).thenReturn(post);
		when(topicRepository.getLastPost(post.getTopic())).thenReturn(post);

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.APPROVE);
		service.doApproval(1, this.asList(info));

		assertEquals(1, post.getUser().getTotalPosts());
		assertFalse(topic.isWaitingModeration());
		assertFalse(post.isWaitingModeration());
		assertEquals(0, topic.getTotalReplies());
		assertEquals(post, topic.getLastPost());
	}

	@Test
	public void approvePostInExistingTopicShouldIncrementTotalRepliesAndTotalUserPosts() {
		Topic topic = new Topic(); topic.setPendingModeration(false); topic.setLastPost(post2); 

		final Post post = new Post(); post.setId(1); post.setModerate(true);
		post.setTopic(topic); post.setUser(new User());

		when(postRepository.get(1)).thenReturn(post);
		when(topicRepository.getLastPost(post.getTopic())).thenReturn(post);

		ApproveInfo info = new ApproveInfo();
		info.setPostId(1); info.setStatus(ApproveInfo.APPROVE);
		service.doApproval(1, this.asList(info));

		assertEquals(1, post.getUser().getTotalPosts());
		assertFalse(post.isWaitingModeration());
		assertEquals(1, topic.getTotalReplies());
		assertEquals(post, topic.getLastPost());
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

	private List<ApproveInfo> asList(ApproveInfo info) {
		return Arrays.asList(info);
	}
}
