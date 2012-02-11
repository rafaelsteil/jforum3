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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Indexed
@Table(name = "jforum_posts")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class Post implements Serializable {
	@Id
	@DocumentId
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_posts_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "post_id")
	private int id;

	@Column(name = "post_date")
	@Field(index=Index.UN_TOKENIZED, store=Store.NO)
	@DateBridge(resolution=Resolution.MINUTE)
	private Date date;

	@Column(name = "post_text")
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String text;

	@Column(name = "post_subject")
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String subject;

	@Column(name = "enable_bbcode")
	private boolean bbCodeEnabled = true;

	@Column(name = "enable_html")
	private boolean htmlEnabled = false;

	@Column(name = "enable_smilies")
	private boolean smiliesEnabled = true;

	@Column(name = "enable_sig")
	private boolean signatureEnabled = true;

	@Column(name = "poster_ip")
	private String userIp;

	@Column(name = "attach")
	private boolean hasAttachments;

	@Column(name = "need_moderate")
	private boolean moderate;

	@Column(name = "post_edit_count")
	private int editCount;

	@Column(name = "post_edit_time")
	private Date editDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id")
	@IndexedEmbedded
	private Topic topic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forum_id")
	private Forum forum;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@IndexedEmbedded
	private User user;

	@OneToMany(mappedBy = "post")
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	private List<Attachment> attachments = new ArrayList<Attachment>();
	@Transient
	private boolean notifyReplies;

	@Transient
	private Boolean hasEditTimeExpired = Boolean.FALSE;

	public boolean shouldNotifyReplies() {
		return this.notifyReplies;
	}

	public void setNotifyReplies(boolean notify) {
		this.notifyReplies = notify;
	}

	public void setModerate(boolean status) {
		this.moderate = status;
	}

	public boolean isWaitingModeration() {
		return this.moderate;
	}

	/**
	 * Checks if the BB code is enabled
	 *
	 * @return boolean value representing the result
	 */
	public boolean isBbCodeEnabled() {
		return this.bbCodeEnabled;
	}

	/**
	 * Checks if HTML is enabled in the topic
	 *
	 * @return boolean value representing the result
	 */
	public boolean isHtmlEnabled() {
		return this.htmlEnabled;
	}

	/**
	 * Gets the ID of the post
	 *
	 * @return int value with the ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Checks if signature is allowd in the message
	 *
	 * @return boolean representing the result
	 */
	public boolean isSignatureEnabled() {
		return this.signatureEnabled;
	}

	/**
	 * Checks if smart Smilies are enabled :)
	 *
	 * @return boolean representing the result
	 */
	public boolean isSmiliesEnabled() {
		return this.smiliesEnabled;
	}

	/**
	 * Gets the time, represented as long, of the message post
	 *
	 * @return long representing the post time
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Gets the forum this post belongs to. In fact, this method makes a call to
	 * {@link #getTopic().getForum()}
	 *
	 * @return the forum
	 */
	public Forum getForum() {
		return this.forum;
	}

	public void setForum(Forum forum) {
		this.forum = forum;
	}

	/**
	 * Gets the id of the topic this message is associated
	 *
	 * @return int value with the topic id
	 */
	public Topic getTopic() {
		return this.topic;
	}

	/**
	 * Gets the User who has posted the message
	 *
	 * @return int value with the user id
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Gets the IP of the user who have posted the message
	 *
	 * @return String value with the user IP
	 */
	public String getUserIp() {
		return this.userIp;
	}

	/**
	 * Sets the status for BB code in the message
	 *
	 * @param bbCodeEnabled
	 *            <code>true</code> or <code>false</code>, depending the
	 *            intention
	 */
	public void setBbCodeEnabled(boolean bbCodeEnabled) {
		this.bbCodeEnabled = bbCodeEnabled;
	}

	/**
	 * Sets the status for HTML code in the message
	 *
	 * @param htmlEnabled
	 *            <code>true</code> or <code>false</code>, depending the
	 *            intention
	 */
	public void setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
	}

	/**
	 * Sets the id for the message
	 *
	 * @param id
	 *            The id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the status for signatures in the message
	 *
	 * @param signatureEnabled
	 *            <code>true</code> or <code>false</code>, depending the
	 *            intention
	 */
	public void setSignatureEnabled(boolean signatureEnabled) {
		this.signatureEnabled = signatureEnabled;
	}

	/**
	 * Sets the status for smilies in the message
	 *
	 * @param smiliesEnabled
	 *            <code>true</code> or <code>false</code>, depending the
	 *            intention
	 */
	public void setSmiliesEnabled(boolean smiliesEnabled) {
		this.smiliesEnabled = smiliesEnabled;
	}

	/**
	 * Sets the time the message was sent
	 *
	 * @param date
	 *            The time
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the id of the topic that the message belongs to
	 *
	 * @param topicId
	 *            The id of the topic
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * Sets the User that sent the message
	 *
	 * @param userId
	 *            The user Id
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the message of the post
	 *
	 * @return String containing the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the text of the post
	 *
	 * @param text
	 *            The text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the subject of the post
	 *
	 * @return String with the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Sets the subject for the message
	 *
	 * @param subject
	 *            The subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Sets the IP of the user
	 *
	 * @param userIP
	 *            The IP address of the user
	 */
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	/**
	 * @return Returns the hasAttachments.
	 */
	public boolean getHasAttachments() {
		return this.hasAttachments;
	}

	/**
	 * @param hasAttachments
	 *            The hasAttachments to set.
	 */
	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	/**
	 * @return the editDate
	 */
	public Date getEditDate() {
		return this.editDate;
	}

	/**
	 * @param editDate
	 *            the editDate to set
	 */
	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	/**
	 * @param editCount
	 *            the editCount to set
	 */
	public void incrementEditCount() {
		this.editCount++;
	}

	/**
	 * Gets the total number of times the post was edited
	 *
	 * @return int value with the total number of times the post was edited
	 */
	public int getEditCount() {
		return this.editCount;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Post)) {
			return false;
		}

		return ((Post) o).getId() == this.id;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}

	public void addAttachment(Attachment attachment) {
		attachment.setPost(this);
		this.attachments.add(attachment);
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the attachments
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}

	public Boolean getHasEditTimeExpired() {
		return hasEditTimeExpired;
	}

	public void setHasEditTimeExpired(Boolean hasEditTimeExpired) {
		this.hasEditTimeExpired = hasEditTimeExpired;
	}
	public void calculateHasEditTimeExpired(long limitedTime, Date now) {
		this.hasEditTimeExpired = now.getTime() - date.getTime() > limitedTime;
	}
}
