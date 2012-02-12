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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Bill
 */
@Entity
@Table(name = "jforum_avatar")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class Avatar implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_avatar_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "id")
	private int id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "avatar_type", updatable = false)
	@Enumerated(EnumType.STRING)
	private AvatarType avatarType = AvatarType.AVATAR_GALLERY;

	@Column(name = "width")
	private int width;

	@Column(name = "height")
	private int height;

	@OneToMany(mappedBy = "avatar", fetch = FetchType.LAZY)
	private Set<User> users;

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public User getUploadedBy(){
		if (avatarType == AvatarType.AVATAR_UPLOAD && users != null && users.size( ) == 1) {
			return users.iterator().next();
		}

		return null;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public AvatarType getAvatarType() {
		return avatarType;
	}

	public void setAvatarType(AvatarType avatarType) {
		this.avatarType = avatarType;
	}

	@Override
	public String toString() {
		return fileName;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(this.id).hashCode();
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
		Avatar other = (Avatar) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
