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
package net.jforum.core.hibernate;

import java.util.Arrays;
import java.util.List;

import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.TopicRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

/**
 * @author Rafael Steil
 */
public class TopicDAO extends HibernateGenericDAO<Topic> implements TopicRepository {
	public TopicDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.core.hibernate.HibernateGenericDAO#remove(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void remove(Topic entity) {
		List<Integer> users = this.session().createQuery("select p.user.id from Post p where p.topic = :topic")
			.setParameter("topic", entity)
			.list();

		if (users.size() == 0) {
			// If no users were found, it means that all posts were already deleted,
			// probably by post delete instead of topic delete. In such case, consider
			// that the user whe should decrement from is the one who created the topic
			users = Arrays.asList(entity.getUser().getId());
		}

		this.session().createQuery("delete from Post where topic = :topic")
			.setParameter("topic", entity)
			.executeUpdate();

		this.session().createQuery("update User u set u.totalPosts = (select count(*) from Post p where p.user = u) " +
			"where u.id in (:users)")
			.setParameterList("users", users)
			.executeUpdate();

		super.remove(entity);
	}

	/**
	 * @see net.jforum.repository.TopicRepository#getLastPost(net.jforum.entities.Topic)
	 */
	public Post getLastPost(Topic topic) {
		DetachedCriteria lastPost = DetachedCriteria.forClass(Post.class)
			.setProjection(Projections.max("id"))
			.add(Restrictions.eq("topic", topic))
			.add(Restrictions.eq("moderate", false))
			.setComment("topicDAO.getLastPostID");

		return (Post)this.session().createCriteria(Post.class)
			.add(Subqueries.propertyEq("id", lastPost))
			.setComment("topicDAO.getLastPost")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.TopicRepository#getTotalPosts(net.jforum.entities.Topic)
	 */
	public int getTotalPosts(Topic topic) {
		return (Integer)this.session().createCriteria(Post.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("topic", topic))
			.add(Restrictions.eq("moderate", false))
			.setComment("topicDAO.getTotalPosts")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.TopicRepository#getFirstPost(net.jforum.entities.Topic)
	 */
	public Post getFirstPost(Topic topic) {
		DetachedCriteria firstPost = DetachedCriteria.forClass(Post.class)
			.setProjection(Projections.min("id"))
			.add(Restrictions.eq("topic", topic))
			.setComment("topicDAO.getFirstPostID");

		return (Post)this.session().createCriteria(Post.class)
			.add(Subqueries.propertyEq("id", firstPost))
			.setComment("topicDAO.getFirstPost")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.PostRepository#getAllPosts(net.jforum.entities.Topic, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Post> getPosts(Topic topic, int startFrom, int count) {
		return this.session().createQuery("from Post p join fetch p.user user left join fetch user.avatar " +
			" where p.topic = :topic and p.moderate = false order by p.date asc")
			.setParameter("topic", topic)
			.setFirstResult(startFrom)
			.setMaxResults(count)
			.setComment("topicDAO.getPosts")
			.list();
	}
}
