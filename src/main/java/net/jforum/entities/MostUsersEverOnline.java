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
import java.util.Date;

/**
 * @author Rafael Steil
 */
public class MostUsersEverOnline implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int total;
	private Date date;

	public int getTotal() {
		return total;
	}

	public Date getDate() {
		return date;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
