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
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_banlist")
@Component
@PrototypeScoped
public class Banlist implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_banlist_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "banlist_id")
	private int id;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "banlist_ip")
	private String ip;

	@Column(name = "banlist_email")
	private String email;

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return this.userId;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean matches(Banlist b) {
		boolean status = false;

		if (this.matchesUserId(b) || this.matchesEmail(b)) {
			status = true;
		}
		else if (!StringUtils.isEmpty(b.getIp()) && !StringUtils.isEmpty(this.getIp())) {
			if (b.getIp().equalsIgnoreCase(this.getIp())) {
				status = true;
			}
			else {
				status = this.matchIp(b);
			}
		}

		return status;
	}

	private boolean matchesEmail(Banlist b) {
		return (!StringUtils.isEmpty(b.getEmail()) && b.getEmail().equals(this.getEmail()));
	}

	private boolean matchesUserId(Banlist b) {
		return b.getUserId() > 0 && this.getUserId() > 0 && b.getUserId() == this.getUserId();
	}

	private boolean matchIp(Banlist b) {
		boolean status = false;

		StringTokenizer userToken = new StringTokenizer(b.getIp(), ".");
		StringTokenizer thisToken = new StringTokenizer(this.getIp(), ".");

		if (userToken.countTokens() == thisToken.countTokens()) {
			String[] userValues = this.tokenizerAsArray(userToken);
			String[] thisValues = this.tokenizerAsArray(thisToken);

			status = this.compareIpValues(userValues, thisValues);
		}
		return status;
	}

	private boolean compareIpValues(String[] userValues, String[] thisValues) {
		boolean helperStatus = true;
		boolean onlyStars = true;

		for (int i = 0; i < thisValues.length; i++) {
			if (thisValues[i].charAt(0) != '*') {
				onlyStars = false;

				if (!thisValues[i].equals(userValues[i])) {
					helperStatus = false;
				}
			}
		}

		return helperStatus && !onlyStars;
	}

	private String[] tokenizerAsArray(StringTokenizer token) {
		String[] values = new String[token.countTokens()];

		for (int i = 0; token.hasMoreTokens(); i++) {
			values[i] = token.nextToken();
		}

		return values;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getId();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Banlist)) {
			return false;
		}

		return ((Banlist)o).getId() == this.getId();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder()
			.append("id=").append(this.getId()).append(',')
			.append("ip=").append(this.getIp()).append(',')
			.append("userId=").append(this.getUserId()).append(',')
			.append("email=").append(this.getEmail())
			.toString();
	}
}
