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
import net.jforum.repository.BanlistRepository;
import net.jforum.security.AdministrationRule;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.BANNING_ADMIN)
// @InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class BanlistAdminController {
	private BanlistRepository repository;
	private final Result result;

	public BanlistAdminController(BanlistRepository repository, Result result) {
		this.repository = repository;
		this.result = result;
	}

	public void list() {
		this.result.include("banlist", this.repository.getAllBanlists());
	}
}
