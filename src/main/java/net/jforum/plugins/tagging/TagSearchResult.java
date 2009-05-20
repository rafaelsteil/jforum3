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
package net.jforum.plugins.tagging;

import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Topic;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.security.RoleManager;

/**
 * @author Bill
 */
public class TagSearchResult extends PaginatedResult<Topic> {
	public TagSearchResult(List<Topic> results, int totalRecords) {
		super(results, totalRecords);
	}

	/**
	 * Apply security filters on the results
	 * FIXME the best approach would probably be to filter the documents directly in Lucene
	 * when they are being retrieved. Filtering here *may* mess up some records
	 * @param roleManager the set of roles to be used as filters
	 * @return the filtered data
	 */
	public TagSearchResult filter(RoleManager roleManager) {
		for (Iterator<Topic> it = this.getResults().iterator(); it.hasNext(); ) {
			Topic topic = it.next();

			if (!roleManager.isForumAllowed(topic.getForum().getId())) {
				it.remove();
				totalRecords--;
			}
		}

		return this;
	}
}
