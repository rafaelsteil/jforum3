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

import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.repository.ForumRepository;
import net.jforum.security.AdministrationRule;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.ADMIN)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class AdminController {
	private final SessionManager sessionManager;
	private final ForumRepository forumRepository;
	private final Result result;

	public AdminController(SessionManager sessionManager,
			ForumRepository forumRepository, Result result) {
		this.sessionManager = sessionManager;
		this.forumRepository = forumRepository;
		this.result = result;
	}

	/**
	 * Shows the main administration page (for logged users)
	 */
	public void index() {
	}

	/**
	 * The left navigation menu
	 */
	public void menu() {

	}

	/**
	 * The main admin page
	 */
	public void main() {
		this.result.include("stats", this.forumRepository.getForumStats());
		this.result .include("sessions", this.sessionManager.getLoggedSessions());
		this.result.include("totalLoggedUsers", this.sessionManager.getTotalLoggedUsers());
	}
}
