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
package net.jforum.api;

/**
 * Used to execute code inside JForum environment
 * @author Rafael Steil
 */
public class JForum {
	public static void execute(JForumExecutionContext executionContext) {
		try {
			executionContext.execute();
		}
		catch (Exception e) {
			// TODO
		}
		finally {
			// TODO
		}
	}
}
