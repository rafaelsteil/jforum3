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
package net.jforum.core.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import net.jforum.entities.Post;
import net.jforum.formatters.Formatter;
import net.jforum.formatters.PostFormatters;
import net.jforum.formatters.PostOptions;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rafael Steil
 */
public class DisplayFormattedMessageTag extends JForumTag {
	private PostFormatters formatters;
	private Post post;
	private String rawMessage;

	public DisplayFormattedMessageTag() {
		formatters = this.getBean(PostFormatters.class);
	}

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		if (this.post == null && !StringUtils.isEmpty(this.rawMessage)) {
			this.post = new Post();
			this.post.setText(this.rawMessage);
		}

		if (post == null) {
			return;
		}

		String text = post.getText();
		PostOptions options = new PostOptions(this.post.isHtmlEnabled(),
			this.post.isSmiliesEnabled(), this.post.isBbCodeEnabled(),
			this.post.isSignatureEnabled(), this.request().getContextPath());

		for (Formatter formatter : formatters) {
			text = formatter.format(text, options);
		}

		this.write(text);
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public void setRawMessage(String message) {
		this.rawMessage = message;
	}
}
