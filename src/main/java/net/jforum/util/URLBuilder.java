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

public class URLBuilder {
	/**
	 * Builds an URL by adding a '/' between each argument, and ".page" in the end
	 * @param args The parts of the URL to build
	 * @return the URL
	 */
	public static String build(Object... args) {
		StringBuilder sb = new StringBuilder().append('/');

		for (int i = 0; i < args.length - 1; i++) {
			sb.append(args[i]).append('/');
		}

		sb.append(args[args.length - 1]);

		return sb.toString();
	}
}
