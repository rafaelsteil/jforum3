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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Ranking;
import net.jforum.entities.Smilie;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.plugins.post.ForumLimitedTimeRepository;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PollRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.AccessForumRule;
import net.jforum.security.CreateNewTopicRule;
import net.jforum.security.RoleManager;
import net.jforum.services.AttachmentService;
import net.jforum.services.TopicService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.junit.Before;
import org.junit.Ignore;
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
public class TopicControllerTestCase {
	@Spy private MockResult mockResult;
	@Mock private JForumConfig config;
	@Mock private TopicService topicService;
	@Mock private ForumRepository forumRepository;
	@Mock private SmilieRepository smilieRepository;
	@Mock private PostRepository postRepository;
	@Mock private TopicRepository topicRepository;
	@Mock private CategoryRepository categoryRepository;
	@Mock private RankingRepository rankingRepository;
	@Mock private SessionManager sessionManager;
	@Mock private PollRepository pollRepository;
	@Mock private ForumLimitedTimeRepository forumLimitedTimeRepository;
	@Mock private AttachmentService attachmentService;
	@Mock private HttpServletRequest request;
	@Mock private UserSession userSession;
	
	@InjectMocks private TopicController topicController;
	
	@Mock private RoleManager roleManager;
	@Mock private MessageController mockMessageControllerRedirect;
	@Spy private Topic topic;

	@Before
	public void setup() {
		topic = spy(new Topic(topicRepository));
		when(mockResult.redirectTo(MessageController.class)).thenReturn(mockMessageControllerRedirect);
	}

	@Test
	public void replyReview() {
		topic.setId(1);
		
		when(topicRepository.get(1)).thenReturn(topic);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		when(topicRepository.getPosts(topic, 0, 10)).thenReturn(new ArrayList<Post>());
		when(topicRepository.getTotalPosts(topic)).thenReturn(5);
		

		topicController.replyReview(1);
		
		assertEquals(topic, mockResult.included("topic"));
		assertEquals(new ArrayList<Post>(), mockResult.included("posts"));
	}

	@Test
	public void listTopicIsWaitingModerationShouldRedirect() {
		topic.setId(1);
		topic.getForum().setId(2);
		topic.setPendingModeration(true);
		
		when(topicRepository.get(1)).thenReturn(topic);
		
		topicController.list(1, 0, false);
		
		verify(mockMessageControllerRedirect).topicWaitingModeration(2);
		
	}

	@Test
	public void listShouldHaveAccessForumConstraint() throws Exception {
		Method method = topicController.getClass().getMethod("list", int.class, int.class, boolean.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(AccessForumRule.class, method.getAnnotation(SecurityConstraint.class).value());
		assertTrue(method.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void addShouldHaveCreateNewTopicConstraint() throws Exception {
		Method method = topicController.getClass().getMethod("add", int.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(CreateNewTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void addSaveShouldHaveCreateNewTopicConstraint() throws Exception {
		Method method = topicController.getClass().getMethod("addSave", Topic.class, Post.class, PostFormOptions.class, List.class);
		assertNotNull(method);
		assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		assertEquals(CreateNewTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void addSaveRedirectShouldSendToPage3() {
		when(topic.getTotalPosts()).thenReturn(14);
		setupAddReplyPaginationRedirect(topic);
		when(forumRepository.get(anyInt())).thenReturn(new Forum());
		
		topicController.addSave(topic, new Post(), new PostFormOptions(), null);
	
		checkAddReplyPaginationRedirect(topic, 3);
		assertEquals(topic, mockResult.included("topic"));
		
	}

	@Test
	public void replySaveRedirectShouldSendToPage4() {
		topic.setId(10);
		when(topic.getTotalPosts()).thenReturn(17);
		setupAddReplyPaginationRedirect(topic);

		topicController.replySave(topic, new Post(), new PostFormOptions());
		
		checkAddReplyPaginationRedirect(topic, 4);
	}

	private void setupAddReplyPaginationRedirect(final Topic topic) {
		if (topic.getId() > 0) {
			when(topicRepository.get(topic.getId())).thenReturn(topic);
		}
		
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(5);
		when(userSession.getRoleManager()).thenReturn(roleManager);
	}

	private void checkAddReplyPaginationRedirect(final Topic topic, final int pageExpected) {
		String url = String.format("/%s/%s/%s/%s", Domain.TOPICS, Actions.LIST, pageExpected, topic.getId());
		verify(mockResult).redirectTo(url + "#0");
	
	}

	@Test
	public void add() {
		ArrayList<Smilie> smilies = new ArrayList<Smilie>();
		Forum forum = new Forum();
		
		when(forumRepository.get(1)).thenReturn(forum);
		when(smilieRepository.getAllSmilies()).thenReturn(smilies);
		

		topicController.add(1);
	
		assertEquals(forum, mockResult.included("forum"));
		assertEquals(new Post(), mockResult.included("post"));
		assertEquals(true, mockResult.included("isNewTopic"));
		assertEquals(smilies, mockResult.included("smilies"));
	}

	@Test
	public void listSmilie() {
		ArrayList<Smilie> smilies = new ArrayList<Smilie>();
		when(smilieRepository.getAllSmilies()).thenReturn(smilies);
		
		topicController.listSmilies();
		
		assertEquals(smilies, mockResult.included("smilies"));
	}

	@Test
	public void addSaveCannotCreateStickyTypeShouldBeNormal() {
		Forum forum = new Forum();
		forum.setId(3);
		forum.setModerated(false);
		
		topic.setType(Topic.TYPE_ANNOUNCE);
		topic.setForum(forum);
		
		when(forumRepository.get(3)).thenReturn(forum);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(3)).thenReturn(false);
		when(roleManager.getCanCreateStickyAnnouncementTopics()).thenReturn(false);
		when(roleManager.getCanCreatePolls()).thenReturn(false);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		
		topicController.addSave(topic, new Post(), new PostFormOptions(), null);
		
		assertEquals(topic, mockResult.included("topic"));
		verify(mockResult).redirectTo("/topics/list/0#0");
		assertEquals(Topic.TYPE_NORMAL, topic.getType());
	}

	@Test
	public void addSaveForumModeratedIsModeratorTopicStatusShouldNotChange() {
		Forum forum = new Forum();
		forum.setId(3);
		forum.setModerated(true);
		
		topic.setPendingModeration(false);
		topic.setForum(forum);

		when(forumRepository.get(3)).thenReturn(forum);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(3)).thenReturn(false);
		when(roleManager.isModerator()).thenReturn(true);
		when(roleManager.getCanCreatePolls()).thenReturn(false);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		
		topicController.addSave(topic, new Post(), new PostFormOptions(), null);
		
		assertEquals(topic, mockResult.included("topic"));
		verify(mockResult).redirectTo("/topics/list/0#0");
		assertFalse(topic.isWaitingModeration());
	}

	@Test
	public void addSaveForumModeratedNotModeratorStatusShouldBePending() {
		Forum forum = new Forum();
		forum.setId(3);
		forum.setModerated(true);
		
		topic.setPendingModeration(false);
		topic.setForum(forum);

		when(forumRepository.get(3)).thenReturn(forum);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(3)).thenReturn(false);
		when(roleManager.isModerator()).thenReturn(false);
		when(roleManager.getCanCreatePolls()).thenReturn(false);

		topicController.addSave(topic, new Post(), new PostFormOptions(), null);
	
		verify(mockMessageControllerRedirect).topicWaitingModeration(topic.getForum().getId());
		assertEquals(topic, mockResult.included("topic"));
		assertTrue(topic.isWaitingModeration());
	}

	@Test
	public void addSave() {
		Forum forum = new Forum();
		forum.setId(3);
		forum.setModerated(false);
		
		topic.setForum(forum);
		
		Post post = new Post();

		when(userSession.getUser()).thenReturn(new User());
		when(userSession.getIp()).thenReturn("123");
		when(forumRepository.get(3)).thenReturn(forum);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
			
		topicController.addSave(topic, post, new PostFormOptions(), Collections.<PollOption>emptyList());
	
		String url = "/topics/list/0";
		verify(mockResult).redirectTo(url + "#0");
		verify(topicService).addTopic(eq(topic), notNull(List.class), notNull(List.class));
		
		assertEquals(topic, mockResult.included("topic"));
		assertNotNull(topic.getUser());
		assertEquals(new User(), topic.getUser());
		assertEquals("123", post.getUserIp());
	}

	@Test
	public void list() {
		when(topic.getTotalPosts()).thenReturn(10);
		ArrayList<Category> categories = new ArrayList<Category>();
		ArrayList<Post> posts = new ArrayList<Post>();
		ArrayList<Ranking> rankings = new ArrayList<Ranking>();
		
		when(userSession.isLogged()).thenReturn(false);
		when(topicRepository.get(1)).thenReturn(topic);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		when(categoryRepository.getAllCategories()).thenReturn(categories);
		when(topicRepository.getPosts(topic, 0, 10)).thenReturn(posts);
		when(rankingRepository.getAllRankings()).thenReturn(rankings);
		when(sessionManager.isModeratorOnline()).thenReturn(true);

		topicController.list(1, 0, false);
		
		verify(userSession).markTopicAsRead(1);
		assertEquals(true, mockResult.included("isModeratorOnline"));
		assertEquals(topic, mockResult.included("topic"));
		assertEquals(topic.getForum(), mockResult.included("forum"));
		assertNotNull(mockResult.included("pagination"));
		assertEquals(categories, mockResult.included("categories"));
		assertEquals(posts, mockResult.included("posts"));
		assertEquals(rankings, mockResult.included("rankings"));
		assertEquals(false, mockResult.included("canVoteOnPolls"));
		assertEquals(false, mockResult.included("viewPollResults"));
	}

	@Test
	public void replySave() {
		topic.setId(1);
		topic.getForum().setId(1);
		Post post = new Post();
		User user = new User();
		
		when(userSession.getIp()).thenReturn("123");
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(1)).thenReturn(false);
		when(userSession.getUser()).thenReturn(user);
		when(topicRepository.get(1)).thenReturn(topic);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		
		topicController.replySave(topic, post, new PostFormOptions());
		
		String url = "/topics/list/1";
		verify(mockResult).redirectTo(url + "#0");
		verify(topicService).reply(notNull(Topic.class), notNull(Post.class), notNull(List.class));
		assertEquals("123", post.getUserIp());
		assertNotNull(post.getUser());
		assertEquals(user, post.getUser());
	}

	@Test
	public void replySaveForumModeratedIsModeratorShouldPass() {
		topic.setId(2);
		topic.getForum().setId(1);
		topic.getForum().setModerated(true);
		
		Post post = new Post();
		post.setModerate(false);
	
		when(topicRepository.get(2)).thenReturn(topic);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(1)).thenReturn(false);
		when(roleManager.isModerator()).thenReturn(true);
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
	
		topicController.replySave(topic, post, new PostFormOptions());
	
		String url = "/topics/list/2";
		verify(mockResult).redirectTo(url + "#0");
		assertFalse(post.isWaitingModeration());
	}

	@Test
	public void replySaveForumModeratedPostStatusShouldBePending() {
		topic.setId(2);
		topic.getForum().setId(1);
		topic.getForum().setModerated(true);

		Post post = new Post(); post.setModerate(false);
		
		when(topicRepository.get(2)).thenReturn(topic);
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(roleManager.isAttachmentsAlllowed(1)).thenReturn(false);
		when(roleManager.isModerator()).thenReturn(false);
		
		topicController.replySave(topic, post, new PostFormOptions());
		
		verify(mockMessageControllerRedirect).replyWaitingModeration(2);
		assertTrue(post.isWaitingModeration());
	}

	@Test
	public void replySaveWaitingModerationShouldRedirect() {
		topic.setId(1);
		topic.getForum().setId(1);

		Post post = new Post();
		post.setModerate(true);
		
		when(userSession.getRoleManager()).thenReturn(roleManager);
		when(topicRepository.get(1)).thenReturn(topic);

		topicController.replySave(topic, post, new PostFormOptions());
		
		verify(mockMessageControllerRedirect).replyWaitingModeration(1);
	}

	@Test
	public void reply() {
		ArrayList<Smilie> smilies = new ArrayList<Smilie>();
		
		when(topicRepository.get(1)).thenReturn(topic);
		when(smilieRepository.getAllSmilies()).thenReturn(smilies);
	
		topicController.reply(1);
		
		assertEquals(true, mockResult.included("isReply"));
		assertEquals(new Post(), mockResult.included("post"));
		assertEquals(topic, mockResult.included("topic"));
		assertEquals(new Forum(), mockResult.included("forum"));
		assertEquals(smilies, mockResult.included("smilies"));
	}

	@Test
	@Ignore("to implement")
	public void vote() {
		fail("to implement");
	}

	@Test
	public void quote() {
		topic.setId(2);
		
		Forum forum = new Forum();
		forum.setId(3);
		
		Post post = new Post();
		post.setId(1);
		post.setTopic(topic);
		post.setForum(forum);
		
		ArrayList<Smilie> smilies = new ArrayList<Smilie>();
		
		when(postRepository.get(1)).thenReturn(post);
		when(smilieRepository.getAllSmilies()).thenReturn(smilies);

		topicController.quote(1);

		assertEquals(post, mockResult.included("post"));
		assertEquals(true, mockResult.included("isQuote"));
		assertEquals(true, mockResult.included("isReply"));
		assertEquals(post.getTopic(), mockResult.included("topic"));
		assertEquals(post.getForum(), mockResult.included("forum"));
		assertEquals(smilies, mockResult.included("smilies"));
	}
}
