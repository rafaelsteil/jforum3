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
package net.jforum.repository;

import java.util.Date;
import java.util.List;

import net.jforum.entities.Forum;
import net.jforum.entities.ForumStats;
import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ForumRepository extends HibernateGenericDAO<Forum> implements Repository<Forum> {
	private JForumConfig config;

	public ForumRepository(Session session) {
		super(session);
	}

	public void setJforumConfig(JForumConfig config) {
		this.config = config;
	}

	public void moveTopics(Forum toForum, int... topicIds) {
		session.createQuery("update Topic t set t.movedId = t.forum.id, t.forum = :newForum " +
			" where t.id in (:ids)")
			.setParameterList("ids", ArrayUtils.toObject(topicIds))
			.setParameter("newForum", toForum)
			.executeUpdate();

		session.createQuery("update Post p set p.forum = :forum where p.topic.id in (:ids)")
			.setParameterList("ids", ArrayUtils.toObject(topicIds))
			.setParameter("forum", toForum)
			.executeUpdate();
	}

	@Override
	public void add(Forum entity) {
		entity.setDisplayOrder(this.getMaxDisplayOrder());
		super.add(entity);
	}

	@SuppressWarnings("unchecked")
	public List<Group> getModerators(Forum forum) {
		return session.createQuery("select distinct r.group from Role r " +
			" join r.roleValues rv " +
			" where r.name = 'moderate_forum' " +
			" and rv = :forum")
			.setEntity("forum", forum)
			.setComment("forumDAO.getModerators")
			.setCacheable(true)
			.setCacheRegion("forumDAO.getModerators")
			.list();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopicsPendingModeration(Forum forum) {
		return session.createQuery("select t from Topic t left join fetch t.posts post" +
			" where post.moderate = true" +
			" or t.pendingModeration = true" +
			" and t.forum = :forum" +
			" order by t.id, post.id")
			.setEntity("forum", forum)
			.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
			.setComment("forumDAO.getTopicsPendingModeration")
			.list();
	}

	public Post getLastPost(Forum forum) {
		return (Post)session.createQuery("from Post p where p.id = (select max(p2.id) from Post p2" +
			" where p2.forum = :forum and p2.moderate = false)")
			.setParameter("forum", forum)
			.setComment("forumDao.getLastPost")
			.uniqueResult();
	}

	public int getTotalMessages() {
		return (Integer)session.createCriteria(Post.class)
			.setProjection(Projections.rowCount())
			.setCacheable(true)
			.setCacheRegion("forumDAO.getTotalMessages")
			.setComment("forumDAO.getTotalMessages")
			.uniqueResult();
	}

	public int getTotalPosts(Forum forum) {
		return (Integer)session.createCriteria(Post.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("forum", forum))
			.setCacheable(true)
			.setCacheRegion("forumDAO.getTotalPosts#" + forum.getId())
			.setComment("forumDAO.getTotalPosts")
			.uniqueResult();
	}

	public int getTotalTopics(Forum forum) {
		return (Integer)session.createCriteria(Topic.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("pendingModeration", false))
			.add(Restrictions.eq("forum", forum))
			.setCacheable(true)
			.setCacheRegion("forumDAO.getTotalTopics#" + forum.getId())
			.setComment("forumDAO.getTotalTopics")
			.uniqueResult();
	}

	/**
	 * Selects all topics associated to a specific forum.
	 *
	 * @param forum The forum to select the topics
	 * @return <code>List</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
     * @param startFrom int
     * @param count int
	 */
	@SuppressWarnings("unchecked")
	public List<Topic> getTopics(Forum forum, int startFrom, int count) {
		boolean includeMoved = this.config == null || !this.config.getBoolean(ConfigKeys.QUERY_IGNORE_TOPIC_MOVED);

		Criteria criteria = session.createCriteria(Topic.class)
			.createAlias("firstPost", "fp")
			.createAlias("lastPost", "lp");

		if (includeMoved) {
			criteria.add(Restrictions.or(Restrictions.eq("forum", forum), Restrictions.eq("movedId", forum.getId())));
		}
		else {
			criteria.add(Restrictions.eq("forum", forum));
		}

		return criteria.add(Restrictions.eq("pendingModeration", false))
			.addOrder(Order.desc("type"))
			.addOrder(Order.desc("lastPost"))
			.setFirstResult(startFrom)
			.setMaxResults(count)
			.setCacheable(startFrom == 0) // FIXME cache other pages? should find a good solution. Also, check the eviction rules if changing this
			.setCacheRegion("forumDAO.getTopics#" + forum.getId()) // Related to the fixme above
			.setComment("forumDAO.getTopics")
			.list();
	}

	@SuppressWarnings("unchecked")
	public PaginatedResult<Topic> getNewMessages(Date from, int start, int recordsPerPage) {
		int total = ((Number)session.createQuery("select count(*) from Topic t " +
			"where t.pendingModeration = false and t.lastPost.date >= :date")
			.setParameter("date", from)
			.setComment("forumDao.getNewMessagesCount")
			.uniqueResult()).intValue();

		List<Topic> results = session.createQuery("from Topic t " +
			"join fetch t.lastPost lp where t.pendingModeration = false and lp.date >= :date")
			.setParameter("date", from)
			.setFirstResult(start)
			.setMaxResults(recordsPerPage)
			.setComment("forumDao.getNewMessages")
			.list();

		return new PaginatedResult<Topic>(results, total);
	}

	@SuppressWarnings("unchecked")
	public List<Forum> findAll() {
		return session.createQuery("select new Forum(forum.id) from Forum as forum)").list();
	}

	private int getMaxDisplayOrder() {
		Integer displayOrder = (Integer)session.createCriteria(this.persistClass)
			.setProjection(Projections.max("displayOrder"))
			.uniqueResult();

		return displayOrder == null ? 1 : displayOrder + 1;
	}

	public ForumStats getForumStats() {
		ForumStats s = new ForumStats();

		s.setPosts(this.getTotalMessages());
		s.setTotalUsers(((Number)session.createQuery("select count(*) from User").uniqueResult()).intValue());
		s.setTotalTopics(((Number)session.createQuery("select count(*) from Topic").uniqueResult()).intValue());

		Date today = new Date();
		Date firstPostDate = (Date)session.createQuery("select min(p.date) from Post p").uniqueResult();

		s.setPostsPerDay(firstPostDate != null ? (double)s.getPosts() / this.daysUntilToday(today, firstPostDate) : 0);
		s.setTopicsPerDay(firstPostDate != null ? (double)s.getTopics() / this.daysUntilToday(today, firstPostDate) : 0);

		Date firstRegisteredUserDate = (Date)session.createQuery("select min(u.registrationDate) from User u").uniqueResult();
		s.setUsersPerDay(firstRegisteredUserDate != null ? (double)s.getUsers() / this.daysUntilToday(today, firstRegisteredUserDate) : 0);

		return s;
	}

	public ForumStats getForumStats(List<Group> groups) {
		ForumStats s = new ForumStats();

		// Total users
		s.setTotalUsers(((Number)session.createQuery("select count(*) from User u where u.groups in (:groups)")
			.setParameterList("groups", groups)
			.uniqueResult()).intValue());

		return s;
	}

	private int daysUntilToday(Date today, Date from)
	{
		int days = (int) ((today.getTime() - from.getTime()) / (24 * 60 * 60 * 1000));
		return days == 0 ? 1 : days;
	}
}
