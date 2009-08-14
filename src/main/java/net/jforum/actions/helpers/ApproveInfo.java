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

/**
 * @author Rafael Steil
 */
public class ApproveInfo {
	public static final int APPROVE = 0;
	public static final int DEFER = 1;
	public static final int REJECT = 2;

	private int postId;
	private int status;

	/**
	 * @return the postId
	 */
	public int getPostId() {
		return this.postId;
	}

	/**
	 * @param postId the postId to set
	 */
	public void setPostId(int postId) {
		this.postId = postId;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public boolean approve() {
		return this.getStatus() == APPROVE;
	}

	public boolean defer() {
		return this.getStatus() == DEFER;
	}

	public boolean reject() {
		return this.getStatus() == REJECT;
	}
}
