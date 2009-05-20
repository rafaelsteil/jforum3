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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_vote_results")
public class PollOption implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_vote_results_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "vote_option_id")
	private int id;

	@Column(name = "vote_option_text")
	private String text;

	@Column(name = "vote_result")
	private int voteCount;

	@ManyToOne
	@JoinColumn(name = "vote_id")
	private Poll poll;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getVotePercentage() {
		int percent = 0;

		if (poll != null) {
			int totalCount = poll.getTotalVotes();
			percent = Math.round(100f * voteCount / totalCount);
		}

		return percent;
	}

	public Poll getPoll() {
		return poll;
	}

	public void setPoll(Poll poll) {
		this.poll = poll;
	}

	public void incrementVotes() {
		voteCount++;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(128).append('[').append(id).append(", ")
			.append(text).append(", ").append(voteCount).append(']')
			.toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PollOption)) {
			return false;
		}

		PollOption po = (PollOption) o;
		return po.getId() == id
			&& po.getText().equals(text)
			&& po.getVoteCount() == voteCount;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;

		result *= 37 + id;
		result *= 37 + text.hashCode();
		result *= 37 + voteCount;

		return result;
	}
}
