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

import java.util.List;

import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.UserRepository;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * @author Guilherme Moreira
 * @author Rafael Steil
 */
public class UserDAO extends HibernateGenericDAO<User> implements UserRepository {
	public UserDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.UserRepository#getByEmail(java.lang.String)
	 */
	public User getByEmail(String email) {
		return (User)this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("email", email))
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#isUsernameAvailable(java.lang.String, java.lang.String)
	 */
	public boolean isUsernameAvailable(String username, String email) {
		return (Integer)this.session().createCriteria(User.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.or(
				Restrictions.eq("username", username).ignoreCase(),
				Restrictions.eq("email", email).ignoreCase())
			).uniqueResult() == 0;
	}

	/**
	 * @see net.jforum.repository.UserRepository#getTotalUnreadPrivateMessages(net.jforum.entities.User)
	 */
	public int getTotalUnreadPrivateMessages(User user) {
		return (Integer)this.session().createCriteria(PrivateMessage.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("toUser", user))
			.add(Restrictions.eq("type", PrivateMessageType.NEW))
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#getTotalPosts(net.jforum.entities.User)
	 */
	public int getTotalPosts(User user) {
		return (Integer)this.session().createCriteria(Post.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("user", user))
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#getByUsername(java.lang.String)
	 */
	public User getByUsername(String username){
		return (User)this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("username", username))
			.setComment("userDAO.getByUsername")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#changeAllowAvatarState(boolean, net.jforum.entities.Group)
	 */
	public void changeAllowAvatarState(boolean allowAvatar, Group group) {
		this.session().createQuery("update User u set avatarEnabled = :allow where :group in elements(u.groups)")
			.setParameter("allow", allowAvatar)
			.setParameter("group", group)
			.executeUpdate();
	}

	/**
	 * @see {@link UserRepository#findByUserName(String)}
	 */
	@SuppressWarnings("unchecked")
	public List<User> findByUserName(String username){
		return this.session().createCriteria(this.persistClass)
			.add(Restrictions.ilike("username", username, MatchMode.ANYWHERE))
			.addOrder(Order.asc("username"))
			.setComment("userDAO.findByUsername")
			.list();
	}

	/**
	 * @see net.jforum.repository.UserRepository#findByUserName(java.lang.String, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<User> findByUserName(String username, List<Group> filterGroups) {
		return this.session().createQuery("select distinct u from User u left join fetch u.groups g " +
			"where lower(u.username) like lower(:username) " +
			"and g in (:groups) " +
			"order by u.username")
			.setParameter("username", "%" + username + "%")
			.setParameterList("groups", filterGroups)
			.list();
	}

	/**
	 * @see net.jforum.repository.UserRepository#getAllUsers(int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers(int start, int count) {
		return this.session().createCriteria(this.persistClass)
			.addOrder(Order.asc("username"))
			.setFirstResult(start)
			.setMaxResults(count)
			.setComment("userDAO.getAllUsers")
			.list();
	}

	/**
	 * @see net.jforum.repository.UserRepository#getAllUsers(int, int, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers(int start, int count, List<Group> filterGroups) {
//		"from User u where exists (from u.groups g where g in (:groups))"
		return this.session().createQuery("select distinct u from User u join fetch u.groups g where g in(:groups)")
			.setParameterList("groups", filterGroups)
			.list();
	}

	/**
	 * @see net.jforum.repository.UserRepository#getLastRegisteredUser()
	 */
	public User getLastRegisteredUser(){
		return (User)this.session().createCriteria(this.persistClass)
			.addOrder(Order.desc("registrationDate"))
			.setMaxResults(1)
			.setCacheable(true)
			.setCacheRegion("userDAO.getLastRegisteredUser")
			.setComment("userDAO.getLastRegisteredUser")
			.list().get(0);
	}

	/**
	 * @see net.jforum.repository.UserRepository#getTotalUsers()
	 */
	public int getTotalUsers() {
		return (Integer)this.session().createCriteria(this.persistClass)
			.setProjection(Projections.rowCount())
			.setCacheable(true)
			.setCacheRegion("userDAO.getTotalUsers")
			.setComment("userDAO.getTotalUsers")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#validateLogin(String, String)
	 */
	public User validateLogin(String username, String password) {
		return (User)this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("username", username))
			.add(Restrictions.eq("password", password))
			.setComment("userDAO.validateLogin")
			.uniqueResult();
	}

	/**
	 * @see net.jforum.repository.UserRepository#validateLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public User validateLostPasswordHash(String username, String hash) {
		return (User)this.session().createCriteria(this.persistClass)
			.add(Restrictions.eq("activationKey", hash))
			.add(Restrictions.eq("username", username))
			.uniqueResult();
	}

	public int getTotalTopics(int userId) {
		return (Integer) this.session().createCriteria(Topic.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("user.id", userId))
			.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Post> getPosts(User user, int start, int recordsPerPage) {
		return this.session().createCriteria(Post.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("moderate", false))
				.addOrder(Order.desc("id"))
				.setFirstResult(start)
				.setMaxResults(recordsPerPage)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopics(User user, int start, int recordsPerPage) {
		return this.session().createCriteria(Topic.class)
			.add(Restrictions.eq("user", user))
			.add(Restrictions.eq("pendingModeration", false))
			.addOrder(Order.desc("id"))
			.setFirstResult(start)
			.setMaxResults(recordsPerPage)
			.list();
	}
}
