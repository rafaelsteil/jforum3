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
package net.jforum.entities;

/**
 * @author Rafael Steil
 */
public class PrivateMessageType {
	public static final int READ = 0;
	public static final int NEW = 1;
	public static final int SENT = 2;
	public static final int SAVED_IN = 3;
	public static final int SAVED_OUT = 4;
	public static final int UNREAD = 5;

	private PrivateMessageType() { }
}
