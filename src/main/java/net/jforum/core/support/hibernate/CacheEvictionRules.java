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


import java.util.List;

import net.jforum.entities.Attachment;
import net.jforum.entities.Forum;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.cache.Cache;
import org.hibernate.cache.QueryCache;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hinjector.spring.SpringSessionFactory;

/**
 * @author Rafael Steil
 */
@Aspect
public class CacheEvictionRules {
	private SessionFactoryImplementor factoryImplementor;
	private SessionFactory sessionFactory;

	public CacheEvictionRules(SessionFactory factory) {
		if (factory instanceof SessionFactoryImplementor) {
			sessionFactory = factory;
			factoryImplementor = (SessionFactoryImplementor)factory;
		}
		else {
			sessionFactory = factory;
			factoryImplementor = (SessionFactoryImplementor)((SpringSessionFactory)factory).getOriginal();
		}
	}

	/*
	 * ******************
	 * GROUPS SERVICE
	 * ******************
	 */
	@AfterReturning("execution (* net.jforum.services.GroupService.savePermissions(..))")
	public void permissionsChanged() {
		this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getModerators"));
	}

	/*
	 * ****************************
	 * 		RANKING REPOSITORY
	 * ****************************
	 */
	@AfterReturning("(execution (* net.jforum.repository.Repository.add(..))" +
		" || execution (* net.jforum.repository.Repository.update(..))" +
		" || execution (* net.jforum.repository.Repository.remove(..)))" +
		" && target(net.jforum.repository.RankingRepository)")
	public void rankingChanged() {
		this.clearCacheRegion(factoryImplementor.getQueryCache("rankingDAO"));
	}

	/*
	 * **************************
	 * 		SMILIE REPOSITORY
	 * **************************
	 *
	 */
	@AfterReturning("(execution (* net.jforum.repository.Repository.add(..))" +
		" || execution (* net.jforum.repository.Repository.update(..))" +
		" || execution (* net.jforum.repository.Repository.remove(..)))" +
		" && target(net.jforum.repository.SmilieRepository)")
	public void smilieChanged() {
		this.clearCacheRegion(factoryImplementor.getQueryCache("smilieDAO"));
	}

	/*
	 * *************************
	 * 		USER REPOSITORY
	 * *************************
	 */
	@AfterReturning("execution (* net.jforum.repository.Repository.add(..)) && target(net.jforum.repository.UserRepository)")
	public void newUserRegistered() {
		this.clearCacheRegion(factoryImplementor.getQueryCache("userDAO.getTotalUsers"));
		this.clearCacheRegion(factoryImplementor.getQueryCache("userDAO.getLastRegisteredUser"));
	}

	/*
	 * *********************
	 *		 CONFIG
	 * *********************
	 * Changes to configurations stored in the database are very rare
	 */
	@AfterReturning("(execution (* net.jforum.repository.Repository.add(..)) " +
		" || execution (* net.jforum.repository.Repository.update(..)))" +
		" && target(net.jforum.repository.ConfigRepository)")
	public void configChanged() {
		this.clearCacheRegion(factoryImplementor.getQueryCache("configDAO"));
	}

	/*
	 * *********************
	 *		 FORUM
	 * *********************
	 */
	@AfterReturning("execution (* net.jforum.services.ModerationService.moveTopics(..)) && args(toForumId, topicIds)")
	public void moveTopics(int toForumId, int... topicIds) {
		if (!ArrayUtils.isEmpty(topicIds)) {
			this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalPosts#" + toForumId));
			this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalTopics#" + toForumId));
			Cache cache = factoryImplementor.getSecondLevelCacheRegion("net.jforum.entities.Forum");

			if (cache != null) {
				cache.remove("net.jforum.entities.Forum#" + toForumId);
			}

			Topic topic = (Topic)sessionFactory.getCurrentSession().get(Topic.class, topicIds[0]);
			Forum forum = topic.getForum();

			this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalPosts#" + forum.getId()));
			this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalTopics#" + forum.getId()));
			Cache cache2 = factoryImplementor.getSecondLevelCacheRegion("net.jforum.entities.Forum");

			if (cache2 != null) {
				cache2.remove("net.jforum.entities.Forum#" + forum.getId());
			}
		}
	}

	/**
	 * Any change made by the Administrator. It's rare, to evict everything (easier)
	 * Admin operation on Forums are rare, so just evict the entire region, as it
	 * only happens rarely. Regular board usage may need to update a Forum
	 * when a new Post is created, so we only need to evict that specific instance
	 */
	@AfterReturning("execution (* net.jforum.services.ForumService.*(..)) ")
	public void forumChangedByAdministration() {
		this.clearCacheRegion(factoryImplementor.getSecondLevelCacheRegion("net.jforum.entities.Forum"));
		this.clearCacheRegion(factoryImplementor.getQueryCache("categoryDAO.getForums"));
	}

	@AfterReturning("execution (* net.jforum.services.PostService.delete(..)) && args(post)")
	public void postDeleted(Post post) {
		// We force the eviction of both totalPosts and totalTopics as removing a post
		// may trigger the deletion of a topic as well
		this.postOrTopicAddedOrDeletedRules(post.getForum().getId());
	}

	@AfterReturning("execution (* net.jforum.services.ModerationService.deleteTopics(..)) && args(topics)")
	public void topicDeleted(List<Topic> topics) {
		if (topics.size() > 0) {
			// FIXME We're considering that all topics belong to the same forum
			Forum forum = topics.get(0).getForum();
			this.postOrTopicAddedOrDeletedRules(forum.getId());
			this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTopics#" + forum.getId()));
			this.clearCacheRegion(factoryImplementor.getQueryCache("rssDAO.getForumTopics#" + forum.getId()));
		}
	}

	/**
	 * New post in the forum, so we must reload to it get the latest post instance
	 */
	@AfterReturning("execution (* net.jforum.services.TopicService.addTopic(..)) && args(topic, pollOptions, attachments)")
	public void forumNewTopic(Topic topic, List<PollOption> pollOptions, List<Attachment> attachments) {
		if (!topic.isWaitingModeration()) {
			this.newForumPostRule(topic);
		}
	}

	/**
	 * A new reply to an existing topic
	 */
	@AfterReturning("execution (* net.jforum.services.TopicService.reply(..)) && args(topic, post, attachments)")
	public void forumNewPost(Topic topic, Post post, List<Attachment> attachments) {
		if (!post.isWaitingModeration()) {
			this.newForumPostRule(topic);
		}
	}

	@AfterReturning("execution (* net.jforum.services.ModerationService.approvePost(..)) && args(post)")
	public void postApproved(Post post) {
		this.newForumPostRule(post.getTopic());
	}

	/*
	 * *********************
	 *		 CATEGORY
	 * *********************
	 * The rules for categories are simple, as they only change in the Admin,
	 * so it's not a problem to evict the entire region
	 */

	@AfterReturning("execution(* net.jforum.services.CategoryService.*(..))")
	public void categoryChanged() {
		this.clearCacheRegion(factoryImplementor.getSecondLevelCacheRegion("net.jforum.entities.Category"));
		this.clearCacheRegion(factoryImplementor.getQueryCache("categoryDAO.getAllCategories"));
	}

	private void newForumPostRule(Topic topic) {
		int forumId = topic.getForum().getId();
		this.postOrTopicAddedOrDeletedRules(forumId);
	}

	private void postOrTopicAddedOrDeletedRules(int forumId) {
		this.clearCacheRegion(factoryImplementor.getQueryCache("recentTopicsDAO"));
		this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalPosts#" + forumId));
		this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalTopics#" + forumId));
		this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTotalMessages"));
		this.clearCacheRegion(factoryImplementor.getQueryCache("forumDAO.getTopics#" + forumId));
	}

	private void clearCacheRegion(Cache cache) {
		if (cache != null) {
			cache.clear();
		}
	}

	private void clearCacheRegion(QueryCache cache) {
		if (cache != null) {
			cache.clear();
		}
	}
}
