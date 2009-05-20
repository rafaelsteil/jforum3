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

import net.jforum.entities.Attachment;
import net.jforum.entities.UserSession;
import net.jforum.repository.AttachmentRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

/**
 * @author Rafael Steil
 */
public class DownloadAttachmentRule implements AccessRule {
	private final JForumConfig config;
	private final AttachmentRepository repository;

	public DownloadAttachmentRule(JForumConfig config, AttachmentRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	/**
	 * @see net.jforum.security.AccessRule#shouldProceed(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean shouldProceed(UserSession userSession, HttpServletRequest request) {
		if (!userSession.isLogged() && !config.getBoolean(ConfigKeys.ATTACHMENTS_ANONYMOUS)) {
			return false;
		}

		Attachment attachment = repository.get(Integer.parseInt(request.getParameter("attachmentId")));

		if (!userSession.getRoleManager().isAttachmentsAlllowed(attachment.getPost().getForum().getId())
			&& !userSession.getRoleManager().getCanDownloadAttachments(attachment.getPost().getForum().getId())) {
			return false;
		}

		return true;
	}
}
