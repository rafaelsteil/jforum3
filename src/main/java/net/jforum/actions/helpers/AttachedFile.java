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
package net.jforum.actions.helpers;

import net.jforum.entities.Attachment;
import net.jforum.util.UploadUtils;

/**
 * @author Rafael Steil
 */
public class AttachedFile {
	private UploadUtils uploadUtils;
	private Attachment attachment;

	public AttachedFile(Attachment attachment, UploadUtils uploadUtils) {
		this.attachment = attachment;
		this.uploadUtils = uploadUtils;
	}

	public UploadUtils getUploadUtils() {
		return this.uploadUtils;
	}

	public Attachment getAttachment() {
		return this.attachment;
	}
}
