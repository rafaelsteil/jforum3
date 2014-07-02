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
package net.jforum.core.support.hibernate;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jforum.actions.helpers.AttachedFile;
import net.jforum.entities.Forum;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ConfigRepository;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.repository.UserRepository;
import net.jforum.services.CategoryService;
import net.jforum.services.ForumService;
import net.jforum.services.GroupService;
import net.jforum.services.ModerationService;
import net.jforum.services.PostService;
import net.jforum.services.TopicService;

import org.hibernate.cache.Cache;
import org.hibernate.classic.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@Ignore("check if this tests are still usefull")
@RunWith(MockitoJUnitRunner.class)
public class CacheEvictionRulesTestCase extends AbstractDependencyInjectionSpringContextTests {
	
	@Mock private SessionFactoryImplementor sessionFactory;

	/*
	 * ****************
	 * Group Service
	 * ****************
	 */
	public void testPermissionsChaged() throws Exception {
		this.expectQueryCacheEviction("forumDAO.getModerators");
		this.executeTargetMethod(GroupService.class, "savePermissions", 0, null);
		
	}

	/*
	 * **************
	 * Ranking DAO
	 * **************
	 */
	public void testRankingAdded() throws Exception {
		this.runRankingRepository("add");
	}

	public void testRankingUpdated() throws Exception {
		this.runRankingRepository("update");
	}

	public void testRankingDeleted() throws Exception {
		this.runRankingRepository("remove");
	}

	private void runRankingRepository(String methodName) throws Exception {
		this.expectQueryCacheEviction("rankingDAO");
		this.executeTargetMethod(RankingRepository.class, methodName);
		
	}

	/*
	 * **************
	 * Smilie DAO
	 * **************
	 */
	public void testSmilieAdded() throws Exception {
		this.runSmilieRepository("add");
	}

	public void testSmilieUpdated() throws Exception {
		this.runSmilieRepository("update");
	}

	public void testSmilieDeleted() throws Exception {
		this.runSmilieRepository("remove");
	}

	private void runSmilieRepository(String methodName) throws Exception {
		this.expectQueryCacheEviction("smilieDAO");
		this.executeTargetMethod(SmilieRepository.class, methodName);
		
	}

	/*
	 * ***********
	 * User DAO
	 * ***********
	 */
	public void testUserRepositoryAdd() throws Exception {
		this.expectQueryCacheEviction("userDAO.getTotalUsers");
		this.expectQueryCacheEviction("userDAO.getLastRegisteredUser");

		this.executeTargetMethod(UserRepository.class, "add");

		
	}

	/*
	 * *******************
	 * Config DAO Tests
	 * *******************
	 */
	public void testConfigRepositoryAdd() throws Exception {
		this.runConfigRepositoryAddOrUpdate("add");
	}

	public void testConfigRepositoryUpdate() throws Exception {
		this.runConfigRepositoryAddOrUpdate("update");
	}

	private void runConfigRepositoryAddOrUpdate(String methodName) throws Exception {
		this.expectQueryCacheEviction("configDAO");
		this.executeTargetMethod(ConfigRepository.class, methodName);
		
	}

	/*
	 * *************************
	 * Forum Related Tests
	 * *************************
	 */
	public void testForumServiceAdd() throws Exception {
		this.runForumServiceTest("add");
	}

	public void testForumServiceUpdate() throws Exception {
		this.runForumServiceTest("update");
	}

	public void testForumServiceDelete() throws Exception {
		this.runForumServiceTest("delete");
	}

	public void testForumServiceUpForumOrder() throws Exception {
		this.runForumServiceTest("upForumOrder");
	}

	public void testForumServiceDownForumOrder() throws Exception {
		this.runForumServiceTest("downForumOrder");
	}

	private void runForumServiceTest(String methodName) throws Exception {
		this.expectQueryCacheEviction("categoryDAO.getForums");
		this.expect2ndLevelCacheEviction("net.jforum.entities.Forum");
		this.executeTargetMethod(ForumService.class, methodName);
		
	}

	/*
	 * ******************
	 * Posts / Topics
	 * ******************
	 */

	public void testDeletePost() throws Exception {
		Forum forum = new Forum(); forum.setId(1);
		Post post = new Post(); post.setForum(forum);

		this.expectQueryCacheEviction("forumDAO.getTotalMessages");
		this.expectQueryCacheEviction("forumDAO.getTotalPosts#" + forum.getId());
		this.expectQueryCacheEviction("forumDAO.getTotalTopics#" + forum.getId());
		this.expectQueryCacheEviction("recentTopicsDAO");
		this.expectQueryCacheEviction("forumDAO.getTopics#" + forum.getId());

		this.executeTargetMethod(PostService.class, "delete", post);

		
	}

	public void testTopicDeleted() throws Exception {
		Topic topic = new Topic(); topic.getForum().setId(1);

		List<Topic> topics = new ArrayList<Topic>();
		topics.add(topic);

		this.expectQueryCacheEviction("forumDAO.getTotalMessages");
		this.expectQueryCacheEviction("forumDAO.getTotalPosts#1");
		this.expectQueryCacheEviction("forumDAO.getTotalTopics#1");
		this.expectQueryCacheEviction("forumDAO.getTopics#1");
		this.expectQueryCacheEviction("rssDAO.getForumTopics#1");
		this.expectQueryCacheEviction("recentTopicsDAO");

		this.executeVerySpecificTargetMethod(ModerationService.class, "deleteTopics", List.class, topics);

		
	}

	public void testModerationApprovedPost() throws Exception {
		this.newTopicOrPostCommonAssertions();

		Forum forum = new Forum(); forum.setId(1);
		Topic topic = new Topic(); topic.setPendingModeration(false); topic.setForum(forum);
		Post post = new Post(); post.setTopic(topic);

		this.executeTargetMethod(ModerationService.class, "approvePost", post);

		
	}

	public void testModerationTopicMoved() throws Exception {
		this.expectQueryCacheEviction("forumDAO.getTotalPosts#1");
		this.expectQueryCacheEviction("forumDAO.getTotalPosts#2");
		this.expectQueryCacheEviction("forumDAO.getTotalTopics#1");
		this.expectQueryCacheEviction("forumDAO.getTotalTopics#2");
		
		Session session = mock(Session.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);

		Topic topic = new Topic(); topic.getForum().setId(2);
		when(session.get(Topic.class, 5)).thenReturn(topic);

		Cache cache = mock(Cache.class);
		when(sessionFactory.getSecondLevelCacheRegion("net.jforum.entities.Forum")).thenReturn(cache);
		
		this.executeTargetMethod(ModerationService.class, "moveTopics", 1, new int[] { 5 });
		
		verify(cache).remove("net.jforum.entities.Forum#1");
		verify(cache).remove("net.jforum.entities.Forum#2");
	}

	public void testTopicServiceAdd() throws Exception {
		this.runTopicServiceAddOrReply("addTopic");
	}

	public void testTopicServiceReply() throws Exception {
		this.runTopicServiceAddOrReply("reply");
	}

	private void runTopicServiceAddOrReply(String methodName) throws Exception {
		this.newTopicOrPostCommonAssertions();

		Forum forum = new Forum(); forum.setId(1);
		Topic topic = new Topic(); topic.setPendingModeration(false); topic.setForum(forum);

		if ("addTopic".equals(methodName)) {
			this.executeTargetMethod(TopicService.class, methodName, topic, new ArrayList<PollOption>(), new ArrayList<AttachedFile>());
		}
		else {
			this.executeTargetMethod(TopicService.class, methodName, topic, new Post(), new ArrayList<AttachedFile>());
		}

		
	}

	private void newTopicOrPostCommonAssertions() {
		this.expectQueryCacheEviction("forumDAO.getTotalPosts#1");
		this.expectQueryCacheEviction("forumDAO.getTotalTopics#1");
		this.expectQueryCacheEviction("recentTopicsDAO");
		this.expectQueryCacheEviction("forumDAO.getTotalMessages");
		this.expectQueryCacheEviction("forumDAO.getTopics#1");
	}

	/*
	 * *************************
	 * Category Related Tets
	 * *************************
	 */
	public void testCategoryServiceAdd() throws Exception {
		this.runCategoryServiceTest("add");
	}

	public void testCategoryServiceUpdate() throws Exception {
		this.runCategoryServiceTest("update");
	}

	public void testCategoryServiceDelete() throws Exception {
		this.runCategoryServiceTest("delete");
	}

	public void testCategoryServiceUpCategoryOrder() throws Exception {
		this.runCategoryServiceTest("upCategoryOrder");
	}

	public void testCategoryServiceDownCategoryOrder() throws Exception {
		this.runCategoryServiceTest("downCategoryOrder");
	}

	private void runCategoryServiceTest(String methodName) throws Exception {
		this.expectQueryCacheEviction("categoryDAO.getAllCategories");
		this.expect2ndLevelCacheEviction("net.jforum.entities.Category");

		this.executeTargetMethod(CategoryService.class, methodName);

		
	}

	/*
	 * ***************************
	 * General utility methods
	 * ***************************
	 */
	private Object getBean(String name) {
		return this.getApplicationContext().getBean(name);
	}

	private void executeTargetMethod(Class<?> entityClass, String methodName, Object... args) throws Exception {
		Object entity = this.getBean(entityClass.getName());

		Set<Method> methods = new HashSet<Method>(Arrays.asList(entityClass.getMethods()));
		methods.addAll(Arrays.asList(entityClass.getDeclaredMethods()));

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				if (args != null && args.length > 0) {
					method.invoke(entity, args);
				}
				else {
					Class<?>[] parameterTypes = method.getParameterTypes();

					if (parameterTypes.length == 1 && parameterTypes[0] == int.class) {
						//method.setAccessible(true);
						method.invoke(entity, 0);
					}
					else {
						args = new Object[parameterTypes.length];
						//method.setAccessible(true);
						method.invoke(entity, args);
					}
				}
			}
		}
	}

	private void executeVerySpecificTargetMethod(Class<?> entityClass, String methodName, Class<?> argumentType, Object... args) throws Exception {
		Object entity = this.getBean(entityClass.getName());
		Method method = entity.getClass().getMethod(methodName, argumentType);
		method.invoke(entity, args);
	}

	private void expectQueryCacheEviction(final String regionName) {
		org.hibernate.cache.QueryCache cache = mock(org.hibernate.cache.QueryCache.class, regionName);
		when(sessionFactory.getQueryCache(regionName)).thenReturn(cache);
		verify(cache).clear();
	}

	private void expect2ndLevelCacheEviction(final String regionName) {
		Cache secondLevelCache = mock(Cache.class, regionName);
		when(sessionFactory.getSecondLevelCacheRegion(regionName)).thenReturn(secondLevelCache);
		verify(secondLevelCache).clear();
	}

	/**
	 * @see org.springframework.test.AbstractSingleSpringContextTests#onSetUp()
	 */
	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();

		Object o = this.getBean("evictionRules");
		Field[] fields = o.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("factoryImplementor")) {
				field.setAccessible(true);
				field.set(o, sessionFactory);
			}
			else if (field.getName().equals("sessionFactory")) {
				field.setAccessible(true);
				field.set(o, sessionFactory);
			}
		}
	}

	/**
	 * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
	 */
	@Override
	protected String[] getConfigLocations() {
		return new String[]{ "/cache/cache-eviction-rules-test.xml" };                          
	}
}
