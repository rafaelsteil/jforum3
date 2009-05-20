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
 * General formatting and post / topic information from a form
 * @author Rafael Steil
 */
public class PostFormOptions {
	private boolean disableHtml;
	private boolean disableSmilies;
	private boolean disableBbCode;
	private boolean appendSignature;
	private boolean notifyReplies;
	private int topicType;

	public boolean isHtmlEnabled() {
		return !disableHtml;
	}

	public boolean isSmiliesEnabled() {
		return !disableSmilies;
	}

	public boolean isBbCodeEnabled() {
		return !disableBbCode;
	}

	public boolean getAppendSignature() {
		return appendSignature;
	}

	public boolean getNotifyReplies() {
		return notifyReplies;
	}

	public int getTopicType() {
		return topicType;
	}

	/**
	 * @param topicType the topicType to set
	 */
	public void setTopicType(int topicType) {
		this.topicType = topicType;
	}

	/**
	 * @param disableHtml the disableHtml to set
	 */
	public void setDisableHtml(boolean disableHtml) {
		this.disableHtml = disableHtml;
	}

	/**
	 * @param disableSmilies the disableSmilies to set
	 */
	public void setDisableSmilies(boolean disableSmilies) {
		this.disableSmilies = disableSmilies;
	}

	/**
	 * @param disableBbCode the disableBbCode to set
	 */
	public void setDisableBbCode(boolean disableBbCode) {
		this.disableBbCode = disableBbCode;
	}

	/**
	 * @param appendSignature the appendSignature to set
	 */
	public void setAppendSignature(boolean appendSignature) {
		this.appendSignature = appendSignature;
	}

	/**
	 * @param notifyReplies the notifyReplies to set
	 */
	public void setNotifyReplies(boolean notifyReplies) {
		this.notifyReplies = notifyReplies;
	}
}
