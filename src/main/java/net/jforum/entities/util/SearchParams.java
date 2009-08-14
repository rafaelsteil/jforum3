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

import java.text.MessageFormat;

import net.jforum.entities.Forum;

import org.apache.commons.lang.StringUtils;

/**
 * Arguments for a search.
 *
 * By default, each instance is built with {@link SearchMatchType#AND},
 * {@link SearchSort#DATE}, {@link SearchSortType#DESC} in all forums
 *
 * @author Filipe Sabella
 */
public class SearchParams {
	private final String regex = "(^| (?!\\w.*[\'\"]))";

	private String query, user;
	private Forum forum = new Forum();
	private SearchMatchType matchType = SearchMatchType.AND;
	private SearchSort sort = SearchSort.DATE;
	private SearchSortType sortType = SearchSortType.DESC;
	private int maxResults;
	private int start;

	/**
	 * Set the forum to filter
	 * @param forum
	 */
	public void setForum(Forum forum) {
		this.forum = forum;
	}

	/**
	 * Set the sort type
	 * @param sortType
	 */
	public void setSortType(SearchSortType sortType) {
		this.sortType = sortType;
	}

	/**
	 * Set the sort
	 * @param sort
	 */
	public void setSort(SearchSort sort) {
		this.sort = sort;
	}

	/**
	 * Set the result match type
	 * @param matchType
	 */
	public void setMatchType(SearchMatchType matchType) {
		this.matchType = matchType;
	}

	/**
	 * Set the query itself
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Set the max number of results to fetch on each iteration
	 * @param max
	 */
	public void setMaxResults(int max) {
		this.maxResults = max;
	}

	/**
	 * Set the first record to retrieve
	 * @param start
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Return the match type
	 * @return
	 */
	public SearchMatchType getMatchType() {
		return this.matchType;
	}

	public String getQuery() {
		return this.query;
	}

	/**
	 * Return the sort type
	 * @return
	 */
	public SearchSortType getSortType() {
		return this.sortType;
	}

	/**
	 * Return the sort
	 * @return
	 */
	public SearchSort getSort() {
		return this.sort;
	}

	/**
	 * Get the number of results to show on each page
	 * @return
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	/**
	 * Return the forum to filter
	 * @return
	 */
	public Forum getForum() {
		return this.forum;
	}

	/**
	 * @return
	 */
	public int getStart() {
		return this.start;
	}

	/**
	 * @return the username, firstName, lastName or email to filter
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the username, firstName, lastName or email to filter
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Builds the query itself.
	 *
	 *<p>
	 * This is complicated, so I will explain with examples. It is presumed that
	 * you know how the Lucene query engine works. The default search field is
	 * Post.text
	 * </p>
	 *
	 *<p>
	 * query ('and' option off): how to parse a date result: (how to parse a
	 * date) OR (subject:how subject:to subject:parse subject:a subject:date)
	 * </p>
	 *
	 *<p>
	 * query ('and' option on): how to parse a date result: (+how +to +parse +a
	 * +date) OR (+subject:how +subject:to +subject:parse +subject:a
	 * +subject:date)
	 * </p>
	 *
	 * @return the query text
	 */
	public String buildQuery() {
		String userQuery = StringUtils.isNotEmpty(user) ? MessageFormat.format("+(user.username:{0} user.firstName:{0} user.lastName:{0} user.email:{0})", user) : "";

		String text = "";
		if (StringUtils.isNotEmpty(query)) {
			text = this.matchType == SearchMatchType.AND
				? this.query.replaceAll(this.regex, " +")
				: this.query;
		}

		String subject = "";
		if (StringUtils.isNotEmpty(query)) {
			subject = this.query.replaceAll(this.regex,
					(this.matchType == SearchMatchType.AND ? " +subject:" : " subject:"));
		}

		String forumQuery = "";
		if (this.forum != null && this.forum.getId() > 0) {
			forumQuery = "and +topic.forum.id:" + this.forum.getId();
		}

		return String.format("(%s %s %s) or (%s %s %s)", text.trim(), userQuery, forumQuery, subject.trim(), userQuery, forumQuery).trim();
	}
}
