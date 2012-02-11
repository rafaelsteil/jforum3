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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_post_report")
@Component
@PrototypeScoped
public class PostReport {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_post_report_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "report_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(name = "report_date")
	private Date date;

	@Column(name = "report_description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_status")
	private PostReportStatus status = PostReportStatus.UNRESOLVED;

	public PostReport() {
	}

	public PostReport(int id, int postId, String postSubject, int topicId, Date reportDate, String description,
		String reporterName, int reporterId, String postUser, int postUserId, PostReportStatus status) {
		this.id = id;
		this.description = description;

		this.date = reportDate;
		this.status = status;
		this.post = new Post();
		this.post.setId(postId);
		this.post.setSubject(postSubject);
		this.post.setTopic(new Topic());
		this.post.getTopic().setId(topicId);
		this.post.setUser(new User());
		this.post.getUser().setUsername(postUser);
		this.post.getUser().setId(postUserId);

		this.user = new User();
		this.user.setUsername(reporterName);
		this.user.setId(reporterId);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Post getPost() {
		return this.post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PostReportStatus status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public PostReportStatus getStatus() {
		return status;
	}
}
