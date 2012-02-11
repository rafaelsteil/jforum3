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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_ranks")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class Ranking implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_ranks_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "rank_id")
	private int id;

	@Column(name = "rank_title")
	private String title;

	@Column(name = "rank_special")
	private boolean special;

	@Column(name = "rank_image")
	private String image;

	@Column(name = "rank_min")
	private int min;

	/**
	 * @return int
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return String
	 */
	public String getImage() {
		return this.image;
	}

	/**
	 * @return String
	 */
	public boolean isSpecial() {
		return this.special;
	}

	/**
	 * @return String
	 */
	public String getTitle() {
		return (this.title == null ? "" : this.title);
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
	 * Sets the image.
	 *
	 * @param image The image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Sets the special.
	 *
	 * @param special The special to set
	 */
	public void setSpecial(boolean special) {
		this.special = special;
	}

	/**
	 * Sets the title.
	 *
	 * @param title The title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public int getMin() {
		return this.min;
	}

	/**
	 * @param i
	 */
	public void setMin(int i) {
		this.min = i;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Ranking)) {
			return false;
		}

		return ((Ranking) o).getId() == this.getId();
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
		return this.getTitle();
	}
}
