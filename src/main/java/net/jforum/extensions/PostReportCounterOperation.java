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
package net.jforum.extensions;

import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.UserSession;
import net.jforum.repository.PostReportRepository;
import net.jforum.util.SecurityConstants;

/**
 * @author Rafael Steil
 */
public class PostReportCounterOperation implements RequestOperation {
	private final PostReportRepository repository;
	private final ViewPropertyBag propertyBag;
	private final SessionManager sessionManager;

	public PostReportCounterOperation(PostReportRepository repository, ViewPropertyBag propertyBag,
			SessionManager sessionManager) {
		this.repository = repository;
		this.propertyBag = propertyBag;
		this.sessionManager = sessionManager;
	}

	/**
	 * @see net.jforum.extensions.RequestOperation#execute()
	 */
	public void execute() {
		int total = 0;
		UserSession userSession = this.sessionManager.getUserSession();

		if (userSession != null && userSession.isLogged() && userSession.getRoleManager().isModerator()) {
			total = this.repository.countPendingReports(userSession.getRoleManager().getRoleValues(SecurityConstants.FORUM));
		}

		this.propertyBag.put("totalPostReports", total);
	}
}
