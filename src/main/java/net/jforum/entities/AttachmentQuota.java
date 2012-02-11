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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_quota_limit")
@Component
@PrototypeScoped
public class AttachmentQuota {
	@Transient
	public static final int KB = 1;

	@Transient
	public static final int MB = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "quota_desc")
	private String description;

	@Column(name = "quota_limit")
	private int size;

	@Column(name = "quota_type")
	private int type;

	/**
	 * Checks if the size passed as argument is greater than the quota's limit.
	 *
	 * @param size
	 *            The size to check
	 * @return <code>true</code> if the size is greater than quota's limit.
	 */
	public boolean exceedsQuota(long size) {
		if (this.type == AttachmentQuota.KB) {
			return (size > this.size * 1024);
		}

		return (size > this.size * 1024 * 1024);
	}

	public int getSizeInBytes() {
		if (this.type == AttachmentQuota.KB) {
			return (this.size * 1024);
		}

		return (this.size * 1024 * 1024);
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the id.
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @param size
	 *            The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}

		if(!(obj instanceof AttachmentQuota)){
			return false;
		}

		AttachmentQuota other = (AttachmentQuota) obj;
		return this.id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
