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
package net.jforum.core.support.hibernate;

import net.jforum.actions.helpers.PermissionOptions;
import net.jforum.services.GroupService;

/**
 * @author Rafael Steil
 */
public class AOPTestGroupService extends GroupService {
	/**
	 * @see net.jforum.services.GroupService#savePermissions(int, net.jforum.actions.helpers.PermissionOptions)
	 */
	@Override
	public void savePermissions(int groupId, PermissionOptions permissions) {
	}
}
