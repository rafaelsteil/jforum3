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
package net.jforum.entities.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Hold pagination aware data
 * @author Rafael Steil
 */
public class PaginatedResult<T> {
	protected List<T> results = new ArrayList<T>();
	protected int totalRecords;

	public PaginatedResult(List<T> results, int totalRecords) {
		this.results = results;
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the results
	 */
	public List<T> getResults() {
		return this.results;
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return this.totalRecords;
	}
}
