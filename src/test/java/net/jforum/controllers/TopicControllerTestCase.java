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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Ranking;
import net.jforum.entities.Smilie;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
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
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
@SuppressWarnings("unchecked")
public class TopicControllerTestCase {

	private Mockery context = TestCaseUtils.newMockery();
	private JForumConfig config = context.mock(JForumConfig.class);
	private TopicService topicService = context.mock(TopicService.class);
	private UserSession userSession = context.mock(UserSession.class);
	private ForumRepository forumRepository = context.mock(ForumRepository.class);
	private SmilieRepository smilieRepository = context.mock(SmilieRepository.class);
	private PostRepository postRepository = context.mock(PostRepository.class);
	private TopicRepository topicRepository = context.mock(TopicRepository.class);
	private CategoryRepository categoryRepository = context.mock(CategoryRepository.class);
	private RoleManager roleManager = context.mock(RoleManager.class);
	private RankingRepository rankingRepository = context.mock(RankingRepository.class);
	private SessionManager sessionManager = context.mock(SessionManager.class);
	private PollRepository pollRepository = context.mock(PollRepository.class);
	private AttachmentService attachmentService = context.mock(AttachmentService.class);
	private HttpServletRequest request = context.mock(HttpServletRequest.class);
    private ForumLimitedTimeRepository forumLimitedTimeRepository = context.mock(ForumLimitedTimeRepository.class);
    private MockResult mockResult = new MockResult();
	private TopicController topicAction;

	@Test
	public void replyReview() {
		context.checking(new Expectations() {{
			Topic t = new Topic(topicRepository); t.setId(1);
			one(topicRepository).get(1); will(returnValue(t));
			allowing(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			one(topicRepository).getPosts(t, 0, 10); will(returnValue(new ArrayList<Post>()));
			allowing(topicRepository).getTotalPosts(t); will(returnValue(5));
			one(mockResult).include("topic", t);
			one(mockResult).include("posts", new ArrayList<Post>());
		}});

		topicAction.replyReview(1);
		context.assertIsSatisfied();
	}

	@Test
	public void listTopicIsWaitingModerationShouldRedirect() {
		final Topic topic = new Topic(); topic.setId(1); topic.getForum().setId(2); topic.setPendingModeration(true);

		context.checking(new Expectations() {{
			one(topicRepository).get(1); will(returnValue(topic));
			one(mockResult).redirectTo(MessageController.class).topicWaitingModeration(2);
		}});

		topicAction.list(1, 0, false);
		context.assertIsSatisfied();
	}

	@Test
	public void listShouldHaveAccessForumConstraint() throws Exception {
		Method method = topicAction.getClass().getMethod("list", int.class, int.class, boolean.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(AccessForumRule.class, method.getAnnotation(SecurityConstraint.class).value());
		Assert.assertTrue(method.getAnnotation(SecurityConstraint.class).displayLogin());
	}

	@Test
	public void addShouldHaveCreateNewTopicConstraint() throws Exception {
		Method method = topicAction.getClass().getMethod("add", int.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(CreateNewTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void addSaveShouldHaveCreateNewTopicConstraint() throws Exception {
		Method method = topicAction.getClass().getMethod("addSave", Topic.class, Post.class, PostFormOptions.class, List.class);
		Assert.assertNotNull(method);
		Assert.assertTrue(method.isAnnotationPresent(SecurityConstraint.class));
		Assert.assertEquals(CreateNewTopicRule.class, method.getAnnotation(SecurityConstraint.class).value());
	}

	@Test
	public void addSaveRedirectShouldSendToPage3() {
		final Topic topic = new Topic() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getTotalPosts() {
				return 14;
			}
		};

		this.addReplyPaginationRedirect(topic, 3);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(forumRepository).get(topic.getForum().getId()); will(returnValue(topic.getForum()));
			one(mockResult).include("topic", topic);
		}});

		topicAction.addSave(topic, new Post(), new PostFormOptions(), null);
		context.assertIsSatisfied();
	}

	@Test
	public void replySaveRedirectShouldSendToPage4() {
		Topic topic = new Topic() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getTotalPosts() {
				return 17;
			}
		};
		topic.setId(10);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
		}});

		this.addReplyPaginationRedirect(topic, 4);

		topicAction.replySave(topic, new Post(), new PostFormOptions());
		context.assertIsSatisfied();
	}

	private void addReplyPaginationRedirect(final Topic topic, final int pageExpected) {
		context.checking(new Expectations() {{
			if (topic.getId() > 0) {
				one(topicRepository).get(topic.getId()); will(returnValue(topic));
			}

			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			ignoring(roleManager); ignoring(userSession); ignoring(topicService);
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(5));

			String url = String.format("/%s/%s/%s/%s.page", Domain.TOPICS, Actions.LIST, pageExpected, topic.getId());
			one(mockResult).redirectTo(url + "#0");
		}});
	}

	@Test
	public void add() {
		context.checking(new Expectations() {{
			one(forumRepository).get(1); will(returnValue(new Forum()));
			one(mockResult).include("forum", new Forum());
			one(mockResult).include("post", new Post());
			one(mockResult).include("isNewTopic", true);
			one(smilieRepository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));
			one(mockResult).include("smilies", new ArrayList<Smilie>());
		}});

		topicAction.add(1);
		context.assertIsSatisfied();
	}

	@Test
	public void listSmilie() {
		context.checking(new Expectations() {{
			one(smilieRepository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));
			one(mockResult).include("smilies", new ArrayList<Smilie>());
		}});

		topicAction.listSmilies();
		context.assertIsSatisfied();
	}

	@Test
	public void addSaveCannotCreateStickyTypeShouldBeNormal() {
		final Topic topic = new Topic(topicRepository); topic.setType(Topic.TYPE_ANNOUNCE);
		topic.getForum().setId(3);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(forumRepository).get(3); will(returnValue(new Forum() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); setModerated(false); }}));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(3); will(returnValue(false));
			one(roleManager).getCanCreateStickyAnnouncementTopics(); will(returnValue(false));
			one(roleManager).getCanCreatePolls(); will(returnValue(false));
			ignoring(userSession); ignoring(topicService); ignoring(mockResult);
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			one(mockResult).include("topic", topic);
		}});

		topicAction.addSave(topic, new Post(), new PostFormOptions(), null);
		context.assertIsSatisfied();
		Assert.assertEquals(Topic.TYPE_NORMAL, topic.getType());
	}

	@Test
	public void addSaveForumModeratedIsModeratorTopicStatusShouldNotChange() {
		final Topic topic = new Topic(topicRepository); topic.setPendingModeration(false);
		topic.getForum().setId(3);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(forumRepository).get(3); will(returnValue(new Forum() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); setModerated(true); }}));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(3); will(returnValue(false));
			one(roleManager).isModerator(); will(returnValue(true));
			one(roleManager).getCanCreatePolls(); will(returnValue(false));
			ignoring(roleManager).getCanCreateStickyAnnouncementTopics();
			ignoring(userSession); ignoring(topicService);
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			one(mockResult).include("topic", topic);
		}});

		topicAction.addSave(topic, new Post(), new PostFormOptions(), null);
		context.assertIsSatisfied();
		Assert.assertFalse(topic.isWaitingModeration());
	}

	@Test
	public void addSaveForumModeratedNotModeratorStatusShouldBePending() {
		final Topic topic = new Topic(); topic.setPendingModeration(false);
		topic.getForum().setId(3);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(forumRepository).get(3); will(returnValue(new Forum() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); setModerated(true); }}));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(3); will(returnValue(false));
			one(roleManager).isModerator(); will(returnValue(false));
			one(roleManager).getCanCreatePolls(); will(returnValue(false));
			ignoring(roleManager).getCanCreateStickyAnnouncementTopics();
			ignoring(userSession); ignoring(topicService); ignoring(mockResult);
			one(mockResult).include("topic", topic);
		}});

		topicAction.addSave(topic, new Post(), new PostFormOptions(), null);
		context.assertIsSatisfied();
		Assert.assertTrue(topic.isWaitingModeration());
	}

	@Test
	public void addSave() {
		final Topic topic = new Topic(topicRepository); topic.getForum().setId(3);
		Post post = new Post();

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getUser(); will(returnValue(new User()));
			one(userSession).getIp(); will(returnValue("123"));
			one(topicService).addTopic(with(aNonNull(Topic.class)),
				with(aNull(List.class)), with(aNonNull(List.class)));
			one(forumRepository).get(3); will(returnValue(new Forum() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

			{ setId(3); setModerated(false); }}));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			ignoring(roleManager);
			String url = "/topics/list/0.page";
			one(mockResult).redirectTo(url + "#0");
			one(mockResult).include("topic", topic);
		}});

		topicAction.addSave(topic, post, new PostFormOptions(), null);
		context.assertIsSatisfied();

		Assert.assertNotNull(topic.getUser());
		Assert.assertEquals(new User(), topic.getUser());
		Assert.assertEquals("123", post.getUserIp());
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			Topic topic = new Topic(topicRepository) {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;
				@Override
				public Forum getForum() { return new Forum(); }
				@Override
				public int getTotalPosts() { return 10; }
			};

			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).isLogged(); will(returnValue(false));

			one(topicRepository).get(1); will(returnValue(topic));
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			one(categoryRepository).getAllCategories(); will(returnValue(new ArrayList<Category>()));
			one(topicRepository).getPosts(topic, 0, 10); will(returnValue(new ArrayList<Post>()));
			one(rankingRepository).getAllRankings(); will(returnValue(new ArrayList<Ranking>()));
			one(userSession).markTopicAsRead(1);

			one(sessionManager).isModeratorOnline(); will(returnValue(true));
			one(mockResult).include("isModeratorOnline", true);
			one(mockResult).include("topic", topic);
			one(mockResult).include("forum", topic.getForum());
			one(mockResult).include("pagination", new Pagination(0, 0, 0, "", 0));
			one(mockResult).include("categories", new ArrayList<Category>());
			one(mockResult).include("posts", new ArrayList<Post>());
			one(mockResult).include("rankings", new ArrayList<Ranking>());
			one(mockResult).include("canVoteOnPolls", false);
			one(mockResult).include("viewPollResults", false);
		}});

		topicAction.list(1, 0, false);
		context.assertIsSatisfied();
	}

	@Test
	public void replySave() {
		final Topic topic = new Topic(topicRepository) {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }}; topic.getForum().setId(1);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(userSession).getIp(); will(returnValue("123"));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(1); will(returnValue(false));
			one(userSession).getUser(); will(returnValue(new User()));
			one(topicRepository).get(1); will(returnValue(topic));
			one(topicService).reply(with(aNonNull(Topic.class)),
				with(aNonNull(Post.class)), with(aNonNull(List.class)));
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));

			String url = "/topics/list/1.page";
			one(mockResult).redirectTo(url + "#0");
		}});

		Post post = new Post();

		topicAction.replySave(topic, post, new PostFormOptions());
		context.assertIsSatisfied();

		Assert.assertEquals("123", post.getUserIp());
		Assert.assertNotNull(post.getUser());
		Assert.assertEquals(new User(), post.getUser());
	}

	@Test
	public void replySaveForumModeratedIsModeratorShouldPass() {
		final Topic topic = new Topic(topicRepository); topic.setId(2);
		topic.getForum().setId(1); topic.getForum().setModerated(true);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(topicRepository).get(2); will(returnValue(topic));
			allowing(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(1); will(returnValue(false));
			one(roleManager).isModerator(); will(returnValue(true));
			ignoring(userSession); ignoring(topicService);
			one(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			String url = "/topics/list/2.page";
			one(mockResult).redirectTo(url + "#0");
		}});

		Post post = new Post(); post.setModerate(false);

		topicAction.replySave(topic, post, new PostFormOptions());
		context.assertIsSatisfied();
		Assert.assertFalse(post.isWaitingModeration());
	}

	@Test
	public void replySaveForumModeratedPostStatusShouldBePending() {
		final Topic topic = new Topic(); topic.setId(2);
		topic.getForum().setId(1); topic.getForum().setModerated(true);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			one(topicRepository).get(2); will(returnValue(topic));
			one(userSession).getRoleManager(); will(returnValue(roleManager));
			one(roleManager).isAttachmentsAlllowed(1); will(returnValue(false));
			one(roleManager).isModerator(); will(returnValue(false));
			ignoring(userSession); ignoring(topicService);
			one(mockResult).redirectTo(MessageController.class).replyWaitingModeration(2);
		}});

		Post post = new Post(); post.setModerate(false);

		topicAction.replySave(topic, post, new PostFormOptions());
		context.assertIsSatisfied();
		Assert.assertTrue(post.isWaitingModeration());
	}

	@Test
	public void replySaveWaitingModerationShouldRedirect() {
		final Topic topic = new Topic(); topic.setId(1); topic.getForum().setId(1);

		context.checking(new Expectations() {{
			one(sessionManager).getUserSession(); will(returnValue(userSession));
			ignoring(userSession); ignoring(topicService);
			one(topicRepository).get(1); will(returnValue(topic));
			one(mockResult).redirectTo(MessageController.class).replyWaitingModeration(1);
		}});

		Post post = new Post(); post.setModerate(true);

		topicAction.replySave(topic, post, new PostFormOptions());
		context.assertIsSatisfied();
	}

	@Test
	public void reply() {
		context.checking(new Expectations() {{
			one(topicRepository).get(1); will(returnValue(new Topic() {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Forum getForum() {
					return new Forum();
				}
			}));

			one(smilieRepository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));

			one(mockResult).include("isReply", true);
			one(mockResult).include("post", new Post());
			one(mockResult).include("topic", new Topic());
			one(mockResult).include("forum", new Forum());
			one(mockResult).include("smilies", new ArrayList<Smilie>());

			//TODO pass zero?
			one(mockResult).redirectTo(TopicController.class).add(0);
		}});

		topicAction.reply(1);
		context.assertIsSatisfied();
	}

	@Test
	public void vote() {

	}

	@Test
	public void quote() {
		final Post post = new Post(); post.setId(1);
		post.setTopic(new Topic()); post.getTopic().setId(2);
		post.setForum(new Forum()); post.getForum().setId(3);

		context.checking(new Expectations() {{
			one(postRepository).get(1); will(returnValue(post));

			one(smilieRepository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));

			one(mockResult).include("post", post);
			one(mockResult).include("isQuote", true);
			one(mockResult).include("isReply", true);
			one(mockResult).include("topic", post.getTopic());
			one(mockResult).include("forum", post.getForum());
			one(mockResult).include("smilies", new ArrayList<Smilie>());
			//TODO pass zero?
			one(mockResult).redirectTo(TopicController.class).add(0);
		}});

		topicAction.quote(1);
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		topicAction = new TopicController(mockResult, config, topicService,
			forumRepository, smilieRepository, postRepository, topicRepository, categoryRepository,
			rankingRepository, sessionManager, pollRepository, forumLimitedTimeRepository, attachmentService, request);
	}
}
