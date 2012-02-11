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
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_sessions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Component
@PrototypeScoped
public class Session {
	@Id
	@Column(name = "user_id")
	private int userId;

	@Column(name = "session_start")
	private Date start;

	@Column(name = "session_last_accessed")
	private Date lastAccessed;

	@Column(name = "session_last_visit")
	private Date lastVisit;

	@Column(name = "session_ip")
	private String ip;


	/**
	 * @return the lastVisit
	 */
	public Date getLastVisit() {
		return this.lastVisit;
	}

	/**
	 * @param lastVisit the lastVisit to set
	 */
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return this.userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the sessionStart
	 */
	public Date getStart() {
		return this.start;
	}

	/**
	 * @param Start the sessionStart to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the sessionTime
	 */
	public Date getLastAccessed() {
		return this.lastAccessed;
	}

	/**
	 * @param sessionTime the sessionTime to set
	 */
	public void setLastAccessed(Date date) {
		this.lastAccessed = date;
	}

	/**
	 * @return the sessionIp
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
}
