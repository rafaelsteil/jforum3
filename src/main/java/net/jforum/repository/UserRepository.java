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

import java.util.List;

import net.jforum.entities.Group;
import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.Topic;
import net.jforum.entities.User;

import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Guilherme Moreira
 * @author Rafael Steil
 */
@Component
public class UserRepository extends HibernateGenericDAO<User> implements Repository<User> {
	public UserRepository(Session session) {
		super(session);
	}

	/**
	 * Finds an user by his email
	 * @param email the email address to search for
	 * @return the user
	 */
	public User getByEmail(String email) {
		return (User)session.createCriteria(this.persistClass)
			.add(Restrictions.eq("email", email))
			.uniqueResult();
	}

	/**
	 * Checks if the given username is available for registering
	 * @param username the username to check
	 * @param email the email of the given username.
	 * @return true if the username is available, of false if either
	 * the username or the email address is already taken
	 */
	public boolean isUsernameAvailable(String username, String email) {
		return (Integer)session.createCriteria(User.class)
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
		return (Integer)session.createCriteria(PrivateMessage.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("toUser", user))
			.add(Restrictions.eq("type", PrivateMessageType.NEW))
			.uniqueResult();
	}

	/**
	 * Get the number of posts sent by an user
	 * @param user the user
	 * @return the number of posts
	 */
	public int getTotalPosts(User user) {
		return (Integer)session.createCriteria(Post.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("user", user))
			.uniqueResult();
	}

	/**
	 * Gets a specific <code>User</code>.
	 *
	 * @param username The User name to search
	 * @return <code>User</code> object containing all the information
	 * or <code>null</code> if no data was found.
	 */
	public User getByUsername(String username){
		return (User)session.createCriteria(this.persistClass)
			.add(Restrictions.eq("username", username))
			.setComment("userDAO.getByUsername")
			.uniqueResult();
	}

	/**
	 * Change the state of the "allow avatar" field, by group
	 * @param allowAvatar the new state
	 * @param group the group the users belong to
	 */
	public void changeAllowAvatarState(boolean allowAvatar, Group group) {
		session.createQuery("update User u set avatarEnabled = :allow where :group in elements(u.groups)")
			.setParameter("allow", allowAvatar)
			.setParameter("group", group)
			.executeUpdate();
	}

	/**
	 * Find a set of users who match an input
	 * @param username the search input
	 * @return the list of users matching the search input
	 */
	@SuppressWarnings("unchecked")
	public List<User> findByUserName(String username){
		return session.createCriteria(this.persistClass)
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
		return session.createQuery("select distinct u from User u left join fetch u.groups g " +
			"where lower(u.username) like lower(:username) " +
			"and g in (:groups) " +
			"order by u.username")
			.setParameter("username", "%" + username + "%")
			.setParameterList("groups", filterGroups)
			.list();
	}

	/**
	 * Return all registered users
	 * @param start the first record to start fetching
	 * @param count how many records to fetch
	 * @return all registered users
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers(int start, int count) {
		return session.createCriteria(this.persistClass)
			.addOrder(Order.asc("username"))
			.setFirstResult(start)
			.setMaxResults(count)
			.setComment("userDAO.getAllUsers")
			.list();
	}

	/**
	 * Return the registered users, filtering by group.
	 * This return all users that are associated with at least one of the
	 * groups passed as parameter
	 * @param start the first record to start fetching
	 * @param count how many records to fetch
	 * @param filterGroups the groups to filter
	 * @return all registered users found
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers(int start, int count, List<Group> filterGroups) {
		return session.createQuery("select distinct u from User u join fetch u.groups g where g in(:groups)")
			.setParameterList("groups", filterGroups)
			.list();
	}

	/**
	 * Gets the last registered user in the forum
	 * @return the user
	 */
	public User getLastRegisteredUser(){
		return (User)session.createCriteria(this.persistClass)
			.addOrder(Order.desc("registrationDate"))
			.setMaxResults(1)
			.setCacheable(true)
			.setCacheRegion("userDAO.getLastRegisteredUser")
			.setComment("userDAO.getLastRegisteredUser")
			.list().get(0);
	}

	/**
	 * Get the number of users registered in the forum
	 * @return
	 */
	public int getTotalUsers() {
		return (Integer)session.createCriteria(this.persistClass)
			.setProjection(Projections.rowCount())
			.setCacheable(true)
			.setCacheRegion("userDAO.getTotalUsers")
			.setComment("userDAO.getTotalUsers")
			.uniqueResult();
	}

	/**
	 * Autheticates an user
	 * @param username the username
	 * @param password the password
	 * @return an {@link User} instance if sucess, or null otherwise
	 */
	public User validateLogin(String username, String password) {
		return (User)session.createCriteria(this.persistClass)
			.add(Restrictions.eq("username", username))
			.add(Restrictions.eq("password", password))
			.setComment("userDAO.validateLogin")
			.uniqueResult();
	}

	/**
	 * Validates the lost password hash
	 * @param username the username associated with the hash
	 * @param hash the hash to validate
	 * @return the user instance if the provided infromation matches, or null if not.
	 */
	public User validateLostPasswordHash(String username, String hash) {
		return (User)session.createCriteria(this.persistClass)
			.add(Restrictions.eq("activationKey", hash))
			.add(Restrictions.eq("username", username))
			.uniqueResult();
	}

	/**
	 * Get the number of topics sent by an user
	 * @param userId the user
	 * @return the number of posts
	 */
	public int getTotalTopics(int userId) {
		return (Integer) session.createCriteria(Topic.class)
			.setProjection(Projections.rowCount())
			.add(Restrictions.eq("user.id", userId))
			.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Post> getPosts(User user, int start, int recordsPerPage) {
		return session.createCriteria(Post.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("moderate", false))
				.addOrder(Order.desc("id"))
				.setFirstResult(start)
				.setMaxResults(recordsPerPage)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopics(User user, int start, int recordsPerPage) {
		return session.createCriteria(Topic.class)
			.add(Restrictions.eq("user", user))
			.add(Restrictions.eq("pendingModeration", false))
			.addOrder(Order.desc("id"))
			.setFirstResult(start)
			.setMaxResults(recordsPerPage)
			.list();
	}
}
