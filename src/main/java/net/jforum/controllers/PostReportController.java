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
package net.jforum.controllers;

import java.util.Date;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.Post;
import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.PostReportRepository;
import net.jforum.security.ModerationRule;
import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.hibernate.ObjectNotFoundException;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.POST_REPORT)
public class PostReportController {
	private final PostReportRepository repository;
	private final JForumConfig config;
	private final Result result;
	private final UserSession userSession;

	public PostReportController(PostReportRepository repository, JForumConfig config,
			Result result, UserSession userSession) {
		this.repository = repository;
		this.userSession = userSession;
		this.config = config;
		this.result = result;
	}

	@SecurityConstraint(ModerationRule.class)
	public void list() {
		int[] forumIds = this.getForumIdsToFilter();
		this.result.include("reports",
				this.repository.getAll(PostReportStatus.UNRESOLVED, forumIds));
	}

	@SecurityConstraint(ModerationRule.class)
	public void listResolved(int page) {
		int[] forumIds = this.getForumIdsToFilter();
		int recordsPerPage = this.config.getInt(ConfigKeys.TOPICS_PER_PAGE);

		PaginatedResult<PostReport> reports = this.repository.getPaginated(
				new Pagination().calculeStart(page, recordsPerPage),
				recordsPerPage, PostReportStatus.RESOLVED, forumIds);

		Pagination pagination = new Pagination(this.config, page)
				.forPostReports(reports.getTotalRecords());

		this.result.include("pagination", pagination);
		this.result.include("reports", reports.getResults());
	}

	@SecurityConstraint(ModerationRule.class)
	public void resolve(int reportId) {
		PostReport report = this.repository.get(reportId);

		if (this.canManipulateReport(report)) {
			report.setStatus(PostReportStatus.RESOLVED);
			this.repository.update(report);
		}

		this.result.redirectTo(this).list();
	}

	@SecurityConstraint(ModerationRule.class)
	public void delete(int reportId) {
		PostReport report = this.repository.get(reportId);

		if (this.canManipulateReport(report)) {
			this.repository.remove(report);
		}

		this.result.redirectTo(this).list();
	}

	public void report(int postId, String description) {
		UserSession userSession = this.userSession;

		if (userSession.isLogged()) {
			PostReport report = new PostReport();
			report.setDate(new Date());
			report.setUser(userSession.getUser());
			report.setDescription(description);

			Post post = new Post();
			post.setId(postId);
			report.setPost(post);

			this.repository.add(report);
		}
	}

	private boolean canManipulateReport(PostReport report) {
		int[] forumIds = this.userSession.getRoleManager()
				.getRoleValues(SecurityConstants.FORUM);

		for (int forumId : forumIds) {
			// Make sure the user is removing a report from a forum he can
			// moderate
			try {
				if (forumId == report.getPost().getForum().getId()) {
					return true;
				}
			} catch (ObjectNotFoundException e) {
				return true;
			}
		}

		return false;
	}

	private int[] getForumIdsToFilter() {
		int[] forumIds = null;
		RoleManager roleManager = this.userSession
				.getRoleManager();

		if (!roleManager.isAdministrator() && !roleManager.isCoAdministrator()) {
			forumIds = roleManager.getRoleValues(SecurityConstants.FORUM);
		}

		return forumIds;
	}
}
