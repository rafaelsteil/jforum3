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
@Table(name = "jforum_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class Config {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_config_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "config_id")
	private int id;

	@Column(name = "config_name")
	private String name;

	@Column(name = "config_value")
	private String value;

	/**
	 * Gets the entry name
	 *
	 * @return The Entry name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the entry value
	 *
	 * @return The Entry value
	 */
	public String getValue() {
		return this.value;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Sets the entry name
	 *
	 * @param string The entry name to set
	 */
	public void setName(String string) {
		this.name = string;
	}

	/**
	 * Sets the entry value
	 *
	 * @param string The entry value to set
	 */
	public void setValue(String string) {
		this.value = string;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Config)) {
			return false;
		}

		Config c = (Config)o;
		return c.getId() == this.getId()
			&& c.getName().equals(this.getName())
			&& c.getValue().equals(this.getValue());
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
		return new StringBuilder()
			.append('[').append(this.getId()).append(',')
			.append(this.getName()).append(',')
			.append(this.getValue())
			.append(']')
			.toString();

	}
}
