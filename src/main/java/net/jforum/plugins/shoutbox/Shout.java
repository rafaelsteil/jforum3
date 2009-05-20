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
package net.jforum.plugins.shoutbox;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.jforum.entities.User;

/**
 * @author Bill
 *
 */
@Entity
@Table(name = "jforum_shouts")
public class Shout implements Serializable {
	private static final long serialVersionUID = -4541763902668385154L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_shouts_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "shout_id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shout_box_id")
	private ShoutBox shoutBox;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column (name = "shouter_name",length=30,nullable=false)
	private String shouterName;

	@Column (name ="shout_text",nullable=false)
	private String message;

	@Column (name = "shouter_ip",length=15,nullable=false)
	private String shouterIp;

	@Column (name ="shout_time")
	private Date shoutTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ShoutBox getShoutBox() {
		return shoutBox;
	}

	public void setShoutBox(ShoutBox shoutBox) {
		this.shoutBox = shoutBox;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getShouterName() {
		return shouterName;
	}

	public void setShouterName(String shouterName) {
		this.shouterName = shouterName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getShouterIp() {
		return shouterIp;
	}

	public void setShouterIp(String shouterIp) {
		this.shouterIp = shouterIp;
	}

	public Date getShoutTime() {
		return shoutTime;
	}

	public void setShoutTime(Date shoutTime) {
		this.shoutTime = shoutTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Shout other = (Shout) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
