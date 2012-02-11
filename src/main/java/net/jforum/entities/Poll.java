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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_vote_desc")
@Component
@PrototypeScoped
public class Poll implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_vote_desc_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "vote_id")
	private int id;

	@Column(name = "vote_text")
	private String label;

	@Column(name = "vote_start")
	private Date startDate;

	@OneToMany(mappedBy = "poll")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	private List<PollOption> options = new ArrayList<PollOption>();

	@Column(name = "vote_length")
	private int length;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public List<PollOption> getOptions() {
		return options;
	}

	public int getTotalVotes() {
		int votes = 0;

		for (PollOption option : this.options) {
			votes += option.getVoteCount();
		}

		return votes;
	}

	public boolean isOpen() {
		if (this.length == 0) {
			return true;
		}

		Calendar endTime = Calendar.getInstance();
		endTime.setTime(startDate);
		endTime.add(Calendar.DAY_OF_YEAR, this.length);

		return System.currentTimeMillis() < endTime.getTimeInMillis();
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(List<PollOption> options) {
		this.options = options;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
}
