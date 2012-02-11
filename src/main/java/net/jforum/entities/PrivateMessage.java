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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_privmsgs")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class PrivateMessage implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_privmsgs_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "privmsgs_id")
	private int id;

	@Column(name = "privmsgs_type")
	private int type;

	@ManyToOne
	@JoinColumn(name = "privmsgs_from_userid")
	private User fromUser;

	@ManyToOne
	@JoinColumn(name = "privmsgs_to_userid")
	private User toUser;

	@Column(name = "privmsgs_date")
	private Date date;

	@Column(name = "privmsgs_text")
	private String text;

	@Column(name = "privmsgs_subject")
	private String subject;

	@Column(name = "privmsgs_enable_bbcode")
	private boolean bbCodeEnabled = true;

	@Column(name = "privmsgs_enable_html")
	private boolean htmlEnabled = true;

	@Column(name = "privmsgs_enable_smilies")
	private boolean smiliesEnabled = true;

	@Column(name = "privmsgs_attach_sig")
	private boolean signatureEnabled = true;

	@Column(name = "privmsgs_ip")
	private String ip;

	public PrivateMessage() { }

	/**
	 * Copy constructor
	 *
	 * @param pm the object to copy from
	 */
	public PrivateMessage(PrivateMessage pm) {
		this.setId(pm.getId());
		this.setType(pm.getType());
		this.setText(pm.getText());
		this.setSubject(pm.getSubject());
		this.setFromUser(pm.getFromUser());
		this.setToUser(pm.getToUser());
		this.setDate(pm.getDate());
	}

	/**
	 * @return Returns the fromUser.
	 */
	public User getFromUser() {
		return fromUser;
	}

	/**
	 * @param fromUser The fromUser to set.
	 */
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	/**
	 * @return Returns the toUser.
	 */
	public User getToUser() {
		return toUser;
	}

	/**
	 * @param toUser The toUser to set.
	 */
	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the time
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date the time to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the bbCodeEnabled
	 */
	public boolean isBbCodeEnabled() {
		return this.bbCodeEnabled;
	}

	/**
	 * @param bbCodeEnabled the bbCodeEnabled to set
	 */
	public void setBbCodeEnabled(boolean bbCodeEnabled) {
		this.bbCodeEnabled = bbCodeEnabled;
	}

	/**
	 * @return the htmlEnabled
	 */
	public boolean isHtmlEnabled() {
		return this.htmlEnabled;
	}

	/**
	 * @param htmlEnabled the htmlEnabled to set
	 */
	public void setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
	}

	/**
	 * @return the smiliesEnabled
	 */
	public boolean isSmiliesEnabled() {
		return this.smiliesEnabled;
	}

	/**
	 * @param smiliesEnabled the smiliesEnabled to set
	 */
	public void setSmiliesEnabled(boolean smiliesEnabled) {
		this.smiliesEnabled = smiliesEnabled;
	}

	/**
	 * @return the signatureEnabled
	 */
	public boolean isSignatureEnabled() {
		return this.signatureEnabled;
	}

	/**
	 * @param signatureEnabled the signatureEnabled to set
	 */
	public void setSignatureEnabled(boolean signatureEnabled) {
		this.signatureEnabled = signatureEnabled;
	}

	public boolean isNew() {
		return this.type == PrivateMessageType.NEW;
	}

	/**
	 * Flag this message as read
	 */
	public void markAsRead() {
		this.type = PrivateMessageType.READ;
	}

	/**
	 * Transorm this instance in a post
	 * Used only for displaying the formatted message
	 * @return the post
	 */
	public Post asPost() {
		Post post = new Post();

		post.setSubject(this.subject);
		post.setText(this.text);
		post.setBbCodeEnabled(this.isBbCodeEnabled());
		post.setHtmlEnabled(this.isHtmlEnabled());
		post.setSmiliesEnabled(this.isSmiliesEnabled());
		post.setSignatureEnabled(this.isSignatureEnabled());

		return post;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof PrivateMessage)) {
			return false;
		}

		return ((PrivateMessage) o).getId() == this.getId();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getId();
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
}
