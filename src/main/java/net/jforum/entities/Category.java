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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.jforum.repository.CategoryRepository;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_categories")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class Category implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_categories_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "category_id")
	private int id;

	@Column(name = "category_order")
	private int displayOrder;

	@Column(name = "category_moderated")
	private boolean moderated;

	@Column(name = "category_title")
	private String name;

	//@ManyToOne
	//@JoinColumn(name = "category_theme_id")
	@Transient
	private Theme theme;

	@Transient
	private CategoryRepository repository;

	public Category() {}

	@Autowired
	public Category(CategoryRepository repository) {
		this.repository = repository;
	}

	public void setModerated(boolean status) {
		this.moderated = status;
	}

	public boolean isModerated() {
		return this.moderated;
	}

	/**
	 * @return int
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return int
	 */
	public int getDisplayOrder() {
		return this.displayOrder;
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
	 * Sets the order.
	 *
	 * @param order The order to set
	 */
	public void setDisplayOrder(int order) {
		this.displayOrder = order;
	}

	/**
	 * Get all forums from this category.
	 *
	 * @return All forums, regardless it is accessible to the user or not.
	 */
	public List<Forum> getForums() {
		// We do not use @OneToMany because forums are ordered,
		// thus changing the display order of a single forum will not
		// automatically change its order in the collection, and manually
		// executing a sort() appears to be a worst approach
		return this.repository.getForums(this);
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

		if (!(o instanceof Category)) {
			return false;
		}

		return ((Category)o).getId() == this.getId();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(64)
			.append('[')
			.append(this.getName())
			.append(", id=").append(this.getId())
			.append(", order=").append(this.getDisplayOrder())
			.toString();
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
}
