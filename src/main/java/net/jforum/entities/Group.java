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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_groups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Group implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_groups_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "group_id")
	private int id;

	@Column(name = "group_name")
	private String name;

	@Column(name = "group_description")
	private String description;

	@OneToMany(mappedBy = "group")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<Role> roles = new ArrayList<Role>();

	@ManyToMany(mappedBy = "groups")
	private List<User> users = new ArrayList<User>();

	public boolean roleExist(String roleName) {
		for (Role role : roles) {
			if (role.getName().equals(roleName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add a new security role to this group
	 * @param role the role to add
	 */
	public void addRole(Role role) {
		roles.add(role);
		role.setGroup(this);
	}

	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the description.
	 *
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the id.
	 *
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Group)) {
			return false;
		}

		return ((Group)o).getId()  == this.getId();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getId();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%d, %s", id, name);
	}
}
