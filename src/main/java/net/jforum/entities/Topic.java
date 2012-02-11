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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.jforum.repository.TopicRepository;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * Represents every topic in the forum.
 *
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_topics")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Component
@PrototypeScoped
public class Topic implements Serializable {
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_STICKY = 1;
	public static final int TYPE_ANNOUNCE = 2;
	public static final int STATUS_UNLOCKED = 0;
	public static final int STATUS_LOCKED = 1;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_topics_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "topic_id")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forum_id")
	@IndexedEmbedded
	private Forum forum = new Forum();

	@Column(name = "topic_views")
	private int totalViews;

	@Column(name = "topic_replies")
	private int totalReplies;

	@Column(name = "topic_status")
	private int status;

	@Column(name = "topic_type")
	private int type;

	@Column(name = "has_attachment")
	private boolean hasAttachment;

	@Transient
	private boolean paginate;

	@Column(name = "topic_subject")
	private String subject;

	@Column(name = "topic_date")
	private Date date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_first_post_id")
	private Post firstPost;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_last_post_id")
	private Post lastPost;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "topic_vote_id")
	private Poll poll;

	@Column(name = "topic_vote_id", updatable = false, insertable = false)
	private Integer pollId;

	@ContainedIn
	@OneToMany(mappedBy = "topic")
	private List<Post> posts = new ArrayList<Post>();

	@Column(name = "need_moderate")
	private boolean pendingModeration;

	@Column(name = "topic_moved_id")
	private int movedId;

	@Transient
	private TopicRepository repository;

	public Topic() {}

	/**
	 * sometimes,in HQL, if we just want to load the id of the topic
	 * rather all the properites, this is quit usefull
	 * @param id
	 */
	public Topic(int id) {
		this.id = id;
	}

	@Autowired
	public Topic(TopicRepository repository) {
		this.repository = repository;
	}

	public void setPendingModeration(boolean status) {
		this.pendingModeration = status;
	}

	public void setRepository(TopicRepository repository) {
		this.repository = repository;
	}

	/**
	 * @return the movedId
	 */
	public int getMovedId() {
		return this.movedId;
	}

	/**
	 * Check if this topic was moved to another forum
	 * @return true if it was moved to another forum
	 */
	public boolean getHasMoved() {
		return this.movedId > 0;
	}

	/**
	 * @param movedId the movedId to set
	 */
	public void setMovedId(int movedId) {
		this.movedId = movedId;
	}

	/**
	 * Returns the ID of the topic
	 *
	 * @return int value with the ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Return all posts from this topic
	 * @return all non-pending moderation posts
	 */
	public List<Post> getPosts() {
		return this.posts;
	}

	/**
	 * Get all posts from this topic
	 * @param start the first record to start fetching
	 * @param count how many records to fetch
	 * @return all non-pending moderation posts in the specified range
	 */
	public List<Post> getPosts(int start, int count) {
		this.assertRepository();
		return this.repository.getPosts(this, start, count);
	}

	/**
	 * Returns the Forum this topic belongs to
	 *
	 * @return Forum this topic belongs to
	 */
	public Forum getForum() {
		return this.forum;
	}

	/**
	 * Teturns the ID of the last post in the topic
	 *
	 * @return int value with the ID
	 */
	public Post getLastPost() {
		return this.lastPost;
	}

	/**
	 * Returns the status
	 *
	 * @return int value with the status
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * Returns the time the topic was posted
	 *
	 * @return int value representing the time
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Returns the title of the topci
	 *
	 * @return String with the topic title
	 */
	public String getSubject() {
		return this.subject == null ? "" : this.subject;
	}

	/**
	 * Returns the total number of replies
	 *
	 * @return int value with the total
	 */
	public int getTotalReplies() {
		return this.totalReplies;
	}

	/**
	 * Returns the number of posts in this topic.
	 * This includes only non-pending moderation posts.
	 * In fact, the result of this method is a call to {@link #getTotalReplies()} + 1
	 * @return the number of posts
	 */
	public int getTotalPosts() {
		return this.getTotalReplies() + 1;
	}

	/**
	 * Returns the total number of views
	 *
	 * @return int value with the total number of views
	 */
	public int getTotalViews() {
		return this.totalViews;
	}

	public User getUser() {
		return this.user;
	}

	/**
	 * Returns the type
	 *
	 * @return int value representing the type
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Sets the id to the topic
	 *
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the Forum associeted with this topic
	 *
	 * @param Forum The Forum to set
	 */
	public void setForum(Forum forum) {
		this.forum = forum;
	}

	/**
	 * Sets the status.
	 *
	 * @param status The status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Sets the time.
	 *
	 * @param date The time to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the title.
	 *
	 * @param title The title to set
	 */
	public void setSubject(String title) {
		this.subject = title;
	}

	/**
	 * Increment by 1 the number of replies of this topic
	 */
	public void incrementTotalReplies() {
		this.totalReplies++;
	}

	/**
	 * Decrement by 1 the number of replies of this topic
	 */
	public void decrementTotalReplies() {
		this.totalReplies--;
	}

	/**
	 * Sets the type.
	 *
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	public void setUser(User u) {
		this.user = u;
	}

	public void setPaginate(boolean paginate) {
		this.paginate = paginate;
	}

	public boolean getPaginate() {
		return this.paginate;
	}

	public void setHasAttachment(boolean b) {
		this.hasAttachment = b;
	}

	public boolean getHasAttachment() {
		return this.hasAttachment;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Topic)) {
			return false;
		}

		return ((Topic) o).getId() == this.id;
	}

	/**
	 * @return true if {@link #getType()} == {@link #TYPE_NORMAL}
	 */
	public boolean isNormal() {
		return this.getType() == TYPE_NORMAL;
	}

	/**
	 * @return true if {@link #getType()} == {@link #TYPE_STICKY}
	 */
	public boolean isSticky() {
		return this.getType() == TYPE_STICKY;
	}

	/**
	 * @return true if {@link #getType()} == {@link #TYPE_ANNOUNCE}
	 */
	public boolean isAnnounce() {
		return this.getType() == TYPE_ANNOUNCE;
	}

	/**
	 * @return true if {@link #getStatus()} == {@link #STATUS_LOCKED}
	 */
	public boolean isLocked() {
		return this.getStatus() == STATUS_LOCKED;
	}

	/**
	 * Unlock this topic, if locked.
	 */
	public void unlock() {
		this.status = STATUS_UNLOCKED;
	}

	/**
	 * Lock this topic
	 */
	public void lock() {
		this.status = STATUS_LOCKED;
	}

	/**
	 * Get the first post in this topic
	 * @return the first post
	 */
	public Post getFirstPost() {
		return this.firstPost;
	}

	public void setFirstPost(Post firstPost) {
		this.firstPost = firstPost;
	}

	public void setLastPost(Post lastPost) {
		this.lastPost = lastPost;
	}

	/**
	 * Increment by 1 the number of views of this topic
	 */
	public void incrementViews() {
		this.totalViews++;
	}

	/**
	 * Check is this topic is waiting for moderation
	 * @return true if moderation is needed
	 */
	public boolean isWaitingModeration() {
		return this.pendingModeration;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(64)
			.append('[')
			.append(this.id)
			.append(", ").append(this.subject)
			.append(']')
			.toString();
	}

	private void assertRepository() {
		if (this.repository == null) {
			throw new IllegalStateException("repository was not initialized");
		}
	}

	/**
	 * @param poll the poll to set
	 */
	public void setPoll(Poll poll) {
		this.poll = poll;

		if (poll != null) {
			this.pollId = poll.getId();
		}
	}

	/**
	 * @return the poll
	 */
	public Poll getPoll() {
		return poll;
	}

	public boolean isPollEnabled() {
		return this.pollId != null && this.pollId > 0;
	}
}
