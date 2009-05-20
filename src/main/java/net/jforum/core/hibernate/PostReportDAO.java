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
package net.jforum.core.hibernate;

import java.util.List;

import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.repository.PostReportRepository;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * @author Rafael Steil
 */
public class PostReportDAO extends HibernateGenericDAO<PostReport> implements PostReportRepository {
	public PostReportDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * @see net.jforum.repository.PostReportRepository#getPaginated(int, int, net.jforum.entities.PostReportStatus, int[])
	 */
	@SuppressWarnings("unchecked")
	public PaginatedResult<PostReport> getPaginated(int start, int count, PostReportStatus status, int... forumIds) {
		int totalRecords = this.countTotalReportsByStatus(status, forumIds);

		List<PostReport> reports = this.createGetAllQuery(status, forumIds)
			.setFirstResult(start)
			.setMaxResults(count)
			.list();

		return new PaginatedResult<PostReport>(reports, totalRecords);
	}

	/**
	 * @see net.jforum.repository.PostReportRepository#getAll()
	 */
	@SuppressWarnings("unchecked")
	public List<PostReport> getAll(PostReportStatus status, int... forumIds) {
		return this.createGetAllQuery(status, forumIds).list();
	}

	/**
	 * @see net.jforum.repository.PostReportRepository#countPendingReports(int...)
	 */
	public int countPendingReports(int... forumIds) {
		return this.countTotalReportsByStatus(PostReportStatus.UNRESOLVED, forumIds);
	}

	private int countTotalReportsByStatus(PostReportStatus status, int... forumIds) {
		Criteria criteria = this.session().createCriteria(PostReport.class)
			.add(Restrictions.eq("status", status))
			.setProjection(Projections.rowCount());

		if (!ArrayUtils.isEmpty(forumIds)) {
			criteria.createAlias("post", "post").add(
				Restrictions.in("post.forum.id", this.primitiveToWrapper(forumIds)));
		}

		return ((Number)criteria.uniqueResult()).intValue();
	}

	private Integer[] primitiveToWrapper(int... ids) {
		Integer[] wrapped = new Integer[ids.length];

		for (int i = 0; i < ids.length; i++) {
			wrapped[i] = Integer.valueOf(ids[i]);
		}

		return wrapped;
	}

	private Query createGetAllQuery(PostReportStatus status, int... forumIds) {
		String query = "select new PostReport(report.id, post.id, post.subject, post.topic.id, report.date, " +
		"report.description, reportUser.username, reportUser.id, postUser.username, postUser.id, report.status)" +
		" from PostReport report " +
		" join report.user reportUser " +
		" join report.post post" +
		" join post.user postUser " +
		" where report.status = :status";

		if (!ArrayUtils.isEmpty(forumIds)) {
			query += " and post.forum.id in (:forumIds)";
		}

		if (!ArrayUtils.isEmpty(forumIds)) {
			return this.session().createQuery(query)
				.setParameter("status", status)
				.setParameterList("forumIds", this.primitiveToWrapper(forumIds));
		}
		else {
			return this.session().createQuery(query).setParameter("status", status);
		}
	}
}
