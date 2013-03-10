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
package net.jforum.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class GroupInteractionFilter {
	/**
	 * Filter the property bag for forums/show, based on group interaction
	 * settings
	 *
	 * @param propertyBag the property bag for the "show" method of the {@link ForumController} action
	 * @param userSession the user session of the current logged user
	 */
	public void filterForumListing(Result result, UserSession userSession) {
		@SuppressWarnings("unchecked")
		Collection<UserSession> sessions = (Collection<UserSession>) result.included().get("onlineUsers");

		if (sessions == null) {
			sessions = new ArrayList<UserSession>();
		}

		Set<UserSession> newSessions = new HashSet<UserSession>();
		User currentUser = userSession.getUser();

		for (Group group : currentUser.getGroups()) {
			for (UserSession anotherUserSession : sessions) {
				User user = anotherUserSession.getUser();

				if (user != null && user.getGroups().contains(group)) {
					newSessions.add(anotherUserSession);
				}
			}
		}

		result.include("totalLoggedUsers", newSessions.size());
		result.include("onlineUsers", newSessions);
	}
}
