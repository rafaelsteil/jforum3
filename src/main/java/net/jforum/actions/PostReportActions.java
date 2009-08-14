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
package net.jforum.actions;

import java.util.Date;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Post;
import net.jforum.entities.PostReport;
import net.jforum.entities.PostReportStatus;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.PaginatedResult;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.PostReportRepository;
import net.jforum.security.ModerationRule;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.SecurityConstants;

import org.hibernate.ObjectNotFoundException;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Rafael Steil
 */
@Component(Domain.POST_REPORT)
@InterceptedBy(MethodSecurityInterceptor.class)
public class PostReportActions {
	private final PostReportRepository repository;
	private final SessionManager sessionManager;
	private final ViewPropertyBag propertyBag;
	private final ViewService viewService;
	private final JForumConfig config;

	public PostReportActions(PostReportRepository repository, SessionManager sessionManager,
		ViewPropertyBag propertyBag, ViewService viewService, JForumConfig config) {
		this.repository = repository;
		this.sessionManager = sessionManager;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
		this.config = config;
	}

	@SecurityConstraint(ModerationRule.class)
	public void list() {
		int[] forumIds = this.getForumIdsToFilter();
		this.propertyBag.put("reports", this.repository.getAll(PostReportStatus.UNRESOLVED, forumIds));
	}

	@SecurityConstraint(ModerationRule.class)
	public void listResolved(@Parameter(key = "page") int page) {
		int[] forumIds = this.getForumIdsToFilter();
		int recordsPerPage = this.config.getInt(ConfigKeys.TOPICS_PER_PAGE);

		PaginatedResult<PostReport> reports = this.repository.getPaginated(
			new Pagination().calculeStart(page, recordsPerPage), recordsPerPage,
			PostReportStatus.RESOLVED, forumIds);

		Pagination pagination = new Pagination(this.config, page).forPostReports(reports.getTotalRecords());

		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("reports", reports.getResults());
	}

	@SecurityConstraint(ModerationRule.class)
	public void resolve(@Parameter(key = "reportId") int reportId) {
		PostReport report = this.repository.get(reportId);

		if (this.canManipulateReport(report)) {
			report.setStatus(PostReportStatus.RESOLVED);
			this.repository.update(report);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	@SecurityConstraint(ModerationRule.class)
	public void delete(@Parameter(key = "reportId") int reportId) {
		PostReport report = this.repository.get(reportId);

		if (this.canManipulateReport(report)) {
			this.repository.remove(report);
		}

		this.viewService.redirectToAction(Actions.LIST);
	}

	public void report(@Parameter(key = "postId") int postId, @Parameter(key = "description") String description) {
		UserSession userSession = this.sessionManager.getUserSession();

		if (userSession.isLogged()) {
			PostReport report = new PostReport();
			report.setDate(new Date());
			report.setUser(userSession.getUser());
			report.setDescription(description);

			Post post = new Post(); post.setId(postId);
			report.setPost(post);

			this.repository.add(report);
		}
	}

	private boolean canManipulateReport(PostReport report) {
		int[] forumIds = this.sessionManager.getUserSession().getRoleManager().getRoleValues(SecurityConstants.FORUM);

		for (int forumId : forumIds) {
			// Make sure the user is removing a report from a forum he can moderate
			try {
				if (forumId == report.getPost().getForum().getId()) {
					return true;
				}
			}
			catch (ObjectNotFoundException e) {
				return true;
			}
		}

		return false;
	}

	private int[] getForumIdsToFilter() {
		int[] forumIds = null;
		RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

		if (!roleManager.isAdministrator() && !roleManager.isCoAdministrator()) {
			forumIds = roleManager.getRoleValues(SecurityConstants.FORUM);
		}

		return forumIds;
	}
}
