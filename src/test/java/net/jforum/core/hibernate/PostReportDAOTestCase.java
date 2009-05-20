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

import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.util.JDBCLoader;

import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PostReportDAOTestCase extends AbstractDAOTestCase<PostReport> {
	@Test
	@SuppressWarnings("deprecation")
	public void countPendingReportsShouldFilterByForum() {
		new JDBCLoader(this.session().connection()).run("/postreport/countPendingReports.sql");
		PostReportDAO dao = this.newDAO();
		Assert.assertEquals(1, dao.countPendingReports(1));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void countPendingReportsWithoutFilteringShouldReturnAllResultsExceptResolved() {
		new JDBCLoader(this.session().connection()).run("/postreport/countPendingReports.sql");
		PostReportDAO dao = this.newDAO();
		Assert.assertEquals(2, dao.countPendingReports());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getAllShouldFilterByForumExpectOneResult() {
		new JDBCLoader(this.session().connection()).run("/postreport/getAll.sql");
		PostReport report = this.createPostReport(1, 1, PostReportStatus.UNRESOLVED);
		this.createPostReport(2, 2, PostReportStatus.UNRESOLVED);

		List<PostReport> reports = this.getAll(1, 1);

		this.assertPostReport(report, reports.get(0));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getAllResolvedReports() {
		new JDBCLoader(this.session().connection()).run("/postreport/getAll.sql");
		this.createPostReport(1, 1, PostReportStatus.UNRESOLVED);
		PostReport report1 = this.createPostReport(2, 2, PostReportStatus.RESOLVED);
		PostReport report2 = this.createPostReport(2, 2, PostReportStatus.RESOLVED);

		PostReportDAO dao = this.newDAO();
		List<PostReport> reports = dao.getAll(PostReportStatus.RESOLVED, null);

		Assert.assertEquals(2, reports.size());

		this.assertPostReport(report1, reports.get(0));
		this.assertPostReport(report2, reports.get(1));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void getAllWithoutFilterShouldReturnAllReults() {
		new JDBCLoader(this.session().connection()).run("/postreport/getAll.sql");
		PostReport report = this.createPostReport(1, 1, PostReportStatus.UNRESOLVED);
		PostReport report2 = this.createPostReport(1, 2, PostReportStatus.UNRESOLVED);

		List<PostReport> reports = this.getAll(2, null);

		this.assertPostReport(report, reports.get(0));
		this.assertPostReport(report2, reports.get(1));
	}

	private List<PostReport> getAll(int expectedCount, int... forumIds) {
		PostReportDAO dao = this.newDAO();

		List<PostReport> reports = dao.getAll(PostReportStatus.UNRESOLVED, forumIds);
		Assert.assertEquals(expectedCount, reports.size());

		return reports;
	}

	private void assertPostReport(PostReport report, PostReport report2) {
		Assert.assertEquals(report.getPost().getId(), report2.getPost().getId());
		Assert.assertEquals(report.getPost().getTopic().getId(), report2.getPost().getTopic().getId());
		Assert.assertEquals(report.getDate(), report2.getDate());
		Assert.assertEquals(report.getDescription(), report2.getDescription());
		Assert.assertEquals(report.getPost().getSubject(), report2.getPost().getSubject());
		Assert.assertEquals(report.getPost().getUser().getId(), report2.getPost().getUser().getId());
		Assert.assertEquals(report.getUser().getId(), report2.getUser().getId());
	}

	private PostReport createPostReport(int forumId, int postId, PostReportStatus status) {
		PostReport report = new PostReport();

		report.setDate(new Date());
		report.setDescription("description");
		report.setPost(new Post());
		report.getPost().setId(postId);
		report.getPost().setForum(new Forum());
		report.getPost().getForum().setId(forumId);
		report.getPost().setTopic(new Topic());
		report.getPost().getTopic().setId(1);
		report.getPost().setUser(new User());
		report.getPost().getUser().setId(1);
		report.setUser(new User());
		report.getUser().setId(1);
		report.setStatus(status);

		PostReportDAO dao = this.newDAO();
		this.insert(report, dao);

		return report;
	}

	private PostReportDAO newDAO() {
		return new PostReportDAO(sessionFactory);
	}
}
