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
package net.jforum.controllers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.AttachedFile;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.entities.Forum;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Smilie;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.RoleManager;
import net.jforum.services.AttachmentService;
import net.jforum.services.PostService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class PostControllerTestCase {
	@Mock private PostRepository postRepository;
	@Mock private SmilieRepository smilieRepository;
	@Mock private TopicRepository topicRepository;
	@Mock private PostService postService;
	@Mock private JForumConfig config;
	@Mock private UserSession userSession;
	@Mock private AttachmentService attachmentService;
	@Mock private HttpServletRequest mockResquest;
	@Spy private MockResult mockResult;
	
	@Mock private RoleManager roleManager;
	@Mock private TopicController mockTopicControllerRedirect;
	@Mock private TopicController mockTopicControllerForward;
	@Mock private ForumController mockForumControllerRedirect;
	@Mock private ForumController mockForumControllerForward;

	@InjectMocks private PostController controller;
	
	private ModerationLog moderationLog = new ModerationLog();
	private Post post;
	private Forum forum;
	@Spy private Topic topic;
	
	@Before
	public void setup() {
		forum = new Forum();
		forum.setId(3);
		
		topic = spy(new Topic(topicRepository));
		topic.setForum(forum);
		
		post = new Post();
		post.setId(2);
		post.setTopic(topic);
		post.setForum(forum);
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(mockResult.redirectTo(ForumController.class)).thenReturn(mockForumControllerRedirect);
		when(mockResult.forwardTo(ForumController.class)).thenReturn(mockForumControllerForward);
		when(mockResult.redirectTo(TopicController.class)).thenReturn(mockTopicControllerRedirect);
		when(mockResult.forwardTo(TopicController.class)).thenReturn(mockTopicControllerForward);
	}

	@Test
	public void deleteHasMorePostsShouldRedirectToTopicListing() {
		this.deleteRedirect(1, 0);
	}

	@Test
	public void deleteHasMorePostsShouldRedirectToPage3() {
		this.deleteRedirect(14, 3);
	}

	@Test
	public void deleteLastMessageShouldRedirectToForum() {
		when(postRepository.get(2)).thenReturn(post);
		topic.decrementTotalReplies(); // we simulate the event dispatch

		controller.delete(2);
		
		verify(postService).delete(post);
		verify(mockForumControllerRedirect).show(topic.getForum().getId(), 0);
		
	}

	@Test
	public void editSave() {
		PostFormOptions options = new PostFormOptions();
		
		when(postRepository.get(2)).thenReturn(post);

		controller.editSave(post, options, null, moderationLog);

		verify(postService).update(post, false, new ArrayList<PollOption>(),
				new ArrayList<AttachedFile>(), moderationLog);
		verify(mockTopicControllerRedirect).list(topic.getId(), 0, true);

	}

	@Test
	public void edit() {
		ArrayList<Smilie> smilies = new ArrayList<Smilie>();
		
		when(postRepository.get(1)).thenReturn(post);
		when(smilieRepository.getAllSmilies()).thenReturn(smilies);

		controller.edit(1);
		
		assertEquals(post, mockResult.included("post"));
		assertEquals(true, mockResult.included("isEdit"));
		assertEquals(new Topic(), mockResult.included("topic"));
		assertEquals(forum, mockResult.included("forum"));
		assertEquals(smilies, mockResult.included("smilies"));

		verify(mockTopicControllerForward).add(0);

	}

	private void deleteRedirect(final int totalPosts, final int expectedPage) {
		when(topic.getTotalPosts()).thenReturn(totalPosts);
		topic.setId(7);
	
		when(postRepository.get(2)).thenReturn(post);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(5);
	
		controller.delete(2);
		
		this.redirectToPage(topic, expectedPage);
		verify(postService).delete(post);
	}

	private void redirectToPage(final Topic topic, final int expectedPage) {
		String url;
		

		if (expectedPage > 0) {
			url = String.format("/%s/%s/%s/%s", Domain.TOPICS, Actions.LIST, expectedPage, topic.getId());
		} else {
			url = String.format("/%s/%s/%s", Domain.TOPICS, Actions.LIST, topic.getId());
		}

		verify(mockResult).redirectTo(url);
	}
}
