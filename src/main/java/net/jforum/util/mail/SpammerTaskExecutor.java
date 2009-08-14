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
package net.jforum.util.mail;

import org.springframework.core.task.TaskExecutor;

/**
 * @author Rafael Steil
 */
public class SpammerTaskExecutor {
	private TaskExecutor taskExecutor;

	public SpammerTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void dispatch(Spammer spammer) {
		this.taskExecutor.execute(new EmailSenderTask(spammer));
	}
}
