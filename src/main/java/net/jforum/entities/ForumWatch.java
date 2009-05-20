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
package net.jforum.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Rafael Steil
 * @version $Id: ForumWatch.java,v 1.1.2.1 2007/02/25 18:53:16 rafaelsteil Exp $
 */
@Entity
@Table(name = "jforum_forums_watch")
public class ForumWatch implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "forum_id")
	private int forumId;

	@Column(name = "user_id")
	private int userId;

	public ForumWatch() {}

	public ForumWatch(int forumId, int userId) {
		this.setUserId(userId);
		this.setForumId(forumId);
	}

	/**
	 * @return the forumId
	 */
	public int getForumId() {
		return forumId;
	}

	/**
	 * @param forumId the forumId to set
	 */
	public void setForumId(int forumId) {
		this.forumId = forumId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof ForumWatch)) {
			return false;
		}

		ForumWatch fw = (ForumWatch)o;
		return fw.getForumId() == this.getForumId()
			&& fw.getUserId() == this.getUserId();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new StringBuffer()
			.append(this.getForumId())
			.append(this.getUserId())
			.hashCode();
	}
}
