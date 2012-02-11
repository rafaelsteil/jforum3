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
package net.jforum.security;

import javax.servlet.http.HttpServletRequest;

import net.jforum.core.exceptions.AccessRuleException;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.PrivateMessageRepository;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PrivateMessageOwnerRule implements AccessRule {
	private PrivateMessageRepository repository;

	public PrivateMessageOwnerRule(PrivateMessageRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see net.jforum.security.AccessRule#shouldProceed(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		PrivateMessage pm = this.repository.get(this.findId(request));
		User currentUser = userSession.getUser();

		return pm != null && (pm.getToUser().equals(currentUser) || pm.getFromUser().equals(currentUser));
	}

	private int findId(HttpServletRequest request) {
		if (request.getParameterMap().containsKey("id")) {
			return Integer.parseInt(request.getParameter("id"));
		}

		throw new AccessRuleException("Could not find the parameter 'id' in the current request");
	}
}
