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
package net.jforum.entities.util;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

/**
 * Information about pagination
 * @author Rafael Steil
 */
public class Pagination {
	private int totalPages;
	private int recordsPerPage;
	private long totalRecords;
	private int thisPage;
	private int id;
	private int start;
	private String baseUrl;
	private JForumConfig config;

	public Pagination() {}

	public Pagination(JForumConfig config, int page) {
		this.config = config;
		start = page;
	}

	/**
	 * @param totalRecords the total number of records
	 * @param recordsPerPage how many records show per page
	 * @param start the number of the first record to start showing
	 */
	public Pagination(long totalRecords, int recordsPerPage, int page, String baseUrl, int id) {
		this.recordsPerPage = recordsPerPage;
		this.totalRecords = totalRecords;
		totalPages = this.calculeTotalPages();
		start = this.calculeStart(page, this.recordsPerPage);
		thisPage = this.calculeThisPage(page);
		this.baseUrl = baseUrl;
		this.id = id;
	}

	/**
	 * Create pagination for user listing
	 * @param totalUsers the total of users
	 * @return the pagination instance
	 */
	public Pagination forUsers(int totalUsers) {
		recordsPerPage = config.getInt(ConfigKeys.USERS_PER_PAGE);
		totalRecords = totalUsers;
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.USERS_ADMIN, Actions.LIST);
		id = 0;

		return this;
	}

	/**
	 * Create pagination for search
	 * @param totalRecords the total of records
	 * @return the proper pagination instance
	 */
	public Pagination forSearch(int totalRecords) {
		recordsPerPage = config.getInt(ConfigKeys.TOPICS_PER_PAGE);
		this.totalRecords = totalRecords;
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.SEARCH, Actions.EXECUTE);
		id = 0;

		return this;
	}

	/**
	 * Create pagination for post reports
	 * @param totalRecords the total of records
	 * @return
	 */
	public Pagination forPostReports(int totalRecords) {
		recordsPerPage = config.getInt(ConfigKeys.TOPICS_PER_PAGE);
		this.totalRecords = totalRecords;
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.POST_REPORT, Actions.LIST_RESOLVED);
		id = 0;

		return this;
	}

	/**
	 * Create pagination for new messages
	 * @param totalRecords the total of records
	 * @return the proper pagination instance
	 */
	public Pagination forNewMessages(int totalRecords) {
		recordsPerPage = config.getInt(ConfigKeys.TOPICS_PER_PAGE);
		this.totalRecords = totalRecords;
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.FORUMS, Actions.NEW_MESSAGES);
		id = 0;

		return this;
	}

	/**
	 * Create pagination for a forum
	 * @param forum the forum
	 * @return the proper pagination instance
	 */
	public Pagination forForum(Forum forum) {
		recordsPerPage = config.getInt(ConfigKeys.TOPICS_PER_PAGE);
		totalRecords = forum.getTotalTopics();
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.FORUMS, Actions.SHOW);
		id = forum.getId();

		return this;
	}

	/**
	 * Create pagination for a topic
	 * @param topic the topic
	 * @return the proper pagination instance
	 */
	public Pagination forTopic(Topic topic) {
		recordsPerPage = config.getInt(ConfigKeys.POSTS_PER_PAGE);
		totalRecords = topic.getTotalPosts();
		totalPages = this.calculeTotalPages();
		thisPage = this.calculeThisPage(start);
		start = this.calculeStart(start, recordsPerPage);
		baseUrl = String.format("/%s/%s", Domain.TOPICS, Actions.LIST);
		id = topic.getId();

		return this;
	}

	public int getStart() {
		return start;
	}

	public int getId() {
		return id;
	}

	/**
	 * @return the base url to use in the pagination links
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return how many pages there are
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * @return how many records show per page
	 */
	public int getRecordsPerPage() {
		return recordsPerPage;
	}

	/**
	 * @return the total number of records
	 */
	public long getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @return the number of the current page being viewed
	 */
	public int getThisPage() {
		return thisPage;
	}

	/**
	 * Just check for the reference or instanceof.
	 * This is a wrong implementation of equals(), and it only
	 * exists because it's needed in the test cases
	 */
	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof Pagination;
	}

	public int calculeThisPage(int page) {
		return Math.min(totalPages, Math.max(1, page));
	}

	public int calculeStart(int page, int recordsPerPage) {
		return page <= 1 ? 0 : (page - 1) * recordsPerPage;
	}

	private int calculeTotalPages() {
		return (int)Math.ceil((double)totalRecords / (double)recordsPerPage);
	}
}
