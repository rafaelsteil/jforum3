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
package net.jforum.formatters;

/**
 * @author Rafael Steil
 */
public class PostOptions {
	private boolean htmlEnabled;
	private boolean smiliesEnabled;
	private boolean bbCodeEnabled;
	private boolean appendSignature;
	private String contextPath;
	
	public PostOptions(boolean htmlEnabled, boolean smiliesEnabled, 
			boolean bbCodeEnabled, boolean appendSignature, String contextPath) {
		this.htmlEnabled = htmlEnabled;
		this.smiliesEnabled = smiliesEnabled;
		this.bbCodeEnabled = bbCodeEnabled;
		this.appendSignature = appendSignature;
		this.contextPath = contextPath;
	}

	public boolean isHtmlEnabled() {
		return this.htmlEnabled;
	}
	
	public boolean isSmiliesEnabled() {
		return this.smiliesEnabled;	
	}
	
	public boolean isBbCodeEnabled() {
		return this.bbCodeEnabled;
	}
	
	public boolean appendSignature() {
		return this.appendSignature;
	}
	
	public String contextPath() {
		return this.contextPath;
	}
}
