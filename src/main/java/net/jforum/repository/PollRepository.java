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

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.PollVoter;
import net.jforum.entities.User;

/**
 * @author Rafael Steil
 */
public interface PollRepository {
	public void registerVote(PollVoter voter);
	public boolean hasUserVoted(Poll poll, User user);
	public PollOption getOption(int optionId);
}
