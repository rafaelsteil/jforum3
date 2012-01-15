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
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

/**
 * Given a desired location, builds the link URL
 *
 * @author Rafael Steil
 */
public class URLTag extends JForumTag {

	public static final String URL_ENCODE = "UTF-8";

	private String address;
	private boolean encode;

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {

		StringBuilder urlBuilder = new StringBuilder(128).append(this.request().getContextPath());

		if (!encode) {
			urlBuilder.append(this.address);
		}
		else {
			if (this.address == null) {
				this.address = "";
			}

			String[] addresses = this.address.split("/");

			for (String _address : addresses) {
				if (StringUtils.isNotEmpty(_address)) {
					urlBuilder.append("/").append(URLEncoder.encode(_address, URL_ENCODE));
				}
			}
		}

		this.write(this.response().encodeURL(urlBuilder.toString()));
	}

	/**
	 * @param address the resource to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param encode the encode to set
	 */
	public void setEncode(boolean encode) {
		this.encode = encode;
	}
}
