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

import net.jforum.entities.Category;

/**
 * @author Bill
 *
 */
@Entity
@Table(name = "jforum_shoutbox")
public class ShoutBox implements Serializable {
	private static final long serialVersionUID = -4541763902668385154L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_shoutbox_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name="shout_length")
	private int shoutLength;

	@Column(name="allow_anonymous")
	private boolean allowAnonymous;

	@Column(name="disabled")
	private boolean disabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getShoutLength() {
		return shoutLength;
	}

	public void setShoutLength(int shoutLength) {
		this.shoutLength = shoutLength;
	}

	public boolean isAllowAnonymous() {
		return allowAnonymous;
	}

	public void setAllowAnonymous(boolean isAllowAnonymous) {
		allowAnonymous = isAllowAnonymous;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean isDisabled) {
		disabled = isDisabled;
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
		ShoutBox other = (ShoutBox) obj;
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
