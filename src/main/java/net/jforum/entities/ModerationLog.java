/*
 * Copyright (c) JForum Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Created on 08/07/2007 11:25:54
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

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

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_moderation_log")
@Component
@PrototypeScoped
public class ModerationLog {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_moderation_log_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "log_id")
	private int id;

	@Column(name = "post_id")
	private int postId;

	@Column(name = "topic_id")
	private int topicId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "post_user_id")
	private User posterUser = new User();

	@Column(name = "log_description")
	private String description = "";

	@Column(name = "log_original_message")
	private String originalMessage;

	@Column(name = "log_date")
	private Date date;

	@Column(name = "log_type")
	private int type;

	public int getId() {
		return this.id;
	}

	public User getUser() {
		return this.user;
	}

	public String getDescription() {
		return this.description;
	}

	public String getOriginalMessage() {
		return this.originalMessage;
	}

	public Date getDate() {
		return this.date;
	}

	public int getType() {
		return this.type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPostId() {
		return this.postId;
	}

	public int getTopicId() {
		return this.topicId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public User getPosterUser() {
		return this.posterUser;
	}

	public void setPosterUser(User posterUser) {
		this.posterUser = posterUser;
	}
}
