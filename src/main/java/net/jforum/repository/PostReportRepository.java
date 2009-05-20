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
package net.jforum.repository;

import java.util.List;

import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.util.PaginatedResult;

/**
 * @author Rafael Steil
 */
public interface PostReportRepository extends Repository<PostReport> {
	/**
	 * Get all post abuse reports, optionally filtering by email
	 * @param status the status to filter
	 * @param forumIds optional list of forum ids to filter
	 * @return
	 */
	public List<PostReport> getAll(PostReportStatus status, int... forumIds);

	public PaginatedResult<PostReport> getPaginated(int start, int count, PostReportStatus status, int... forumIds);

	/**
	 * Count how many post abuse report exist, optionally filtering by forum
	 * @param forumIds optional list of forum ids to filter
	 * @return
	 */
	public int countPendingReports(int... forumIds);
}
