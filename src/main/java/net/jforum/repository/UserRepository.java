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
import net.jforum.entities.Topic;
import net.jforum.entities.User;

/**
 * @author Rafael Steil
 */
public interface UserRepository extends Repository<User> {
	/**
	 * Change the state of the "allow avatar" field, by group
	 * @param allowAvatar the new state
	 * @param group the group the users belong to
	 */
	public void changeAllowAvatarState(boolean allowAvatar, Group group);

	/**
	 * Get the number of posts sent by an user
	 * @param user the user
	 * @return the number of posts
	 */
	public int getTotalPosts(User user);

	/**
	 * Get the number of topics sent by an user
	 * @param userId the user
	 * @return the number of posts
	 */
	public int getTotalTopics(int userId);

	/**
	 * Gets a specific <code>User</code>.
	 *
	 * @param username The User name to search
	 * @return <code>User</code> object containing all the information
	 * or <code>null</code> if no data was found.
	 */
	public User getByUsername(String username);

	/**
	 * Checks if the given username is available for registering
	 * @param username the username to check
	 * @param email the email of the given username.
	 * @return true if the username is available, of false if either
	 * the username or the email address is already taken
	 */
	public boolean isUsernameAvailable(String username, String email);

	/**
	 * Return all registered users
	 * @param start the first record to start fetching
	 * @param count how many records to fetch
	 * @return all registered users
	 */
	public List<User> getAllUsers(int start, int count);

	/**
	 * Return the registered users, filtering by group.
	 * This return all users that are associated with at least one of the
	 * groups passed as parameter
	 * @param start the first record to start fetching
	 * @param count how many records to fetch
	 * @param filterGroups the groups to filter
	 * @return all registered users found
	 */
	public List<User> getAllUsers(int start, int count, List<Group> filterGroups);

	/**
	 * Gets the last registered user in the forum
	 * @return the user
	 */
	public User getLastRegisteredUser();

	/**
	 * Get the number of users registered in the forum
	 * @return
	 */
	public int getTotalUsers();

	/**
	 * Find a set of users who match an input
	 * @param username the search input
	 * @return the list of users matching the search input
	 */
	public List<User> findByUserName(String username);

	/**
	 * Autheticates an user
	 * @param username the username
	 * @param password the password
	 * @return an {@link User} instance if sucess, or null otherwise
	 */
	public User validateLogin(String username, String password);

	/**
	 * @param user
	 * @return
	 */
	public int getTotalUnreadPrivateMessages(User user);

	/**
	 * Finds an user by his email
	 * @param email the email address to search for
	 * @return the user
	 */
	public User getByEmail(String email);

	/**
	 * Validates the lost password hash
	 * @param username the username associated with the hash
	 * @param hash the hash to validate
	 * @return the user instance if the provided infromation matches, or null if not.
	 */
	public User validateLostPasswordHash(String username, String hash);

	/**
	 * Find a list of users who match a search input.
	 * This method also filters the users by group, only bringing those who match
	 * any of the group passed as argument
	 * @param username
	 * @param filterGroups
	 * @return
	 */
	public List<User> findByUserName(String username, List<Group> filterGroups);

	public List<Post> getPosts(User user, int start, int recordsPerPage);

	public List<Topic> getTopics(User user, int start, int recordsPerPage);
}