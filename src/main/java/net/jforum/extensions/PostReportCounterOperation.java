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

import net.jforum.entities.UserSession;
import net.jforum.repository.PostReportRepository;
import net.jforum.util.SecurityConstants;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PostReportCounterOperation implements RequestOperation {
	private final PostReportRepository repository;
	private final Result result;
	private final UserSession userSession;

	public PostReportCounterOperation(PostReportRepository repository, Result result, UserSession userSession) {
		this.repository = repository;
		this.result = result;
		this.userSession = userSession;
	}

	/**
	 * @see net.jforum.extensions.RequestOperation#execute()
	 */
	@Override
	public void execute() {
		int total = 0;
		if (userSession != null && userSession.isLogged() && userSession.getRoleManager().isModerator()) {
			total = this.repository.countPendingReports(userSession.getRoleManager().getRoleValues(SecurityConstants.FORUM));
		}

		result.include("totalPostReports", total);
	}
}
