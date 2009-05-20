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
package net.jforum.repository;

import java.util.List;

import net.jforum.entities.PrivateMessage;
import net.jforum.entities.User;

/**
 * @author Rafael Steil
 */
public interface PrivateMessageRepository extends Repository<PrivateMessage> {
	/**
	 * Selects all messages from the user's inbox.
	 *
	 * @param the user who received the messages
	 * @return al messages
	 */
	public List<PrivateMessage> getFromInbox(User user);

	/**
	 * Selects all messages from the user's sent box.
	 *
	 * @param user the user who sent the message
	 * @return all messages found
	 */
	public List<PrivateMessage> getFromSentBox(User user);
}