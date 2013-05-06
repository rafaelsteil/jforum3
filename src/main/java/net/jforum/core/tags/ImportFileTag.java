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

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.jforum.util.ConfigKeys;

import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;

/**
 * @author Bill
 *
 */
public abstract class ImportFileTag extends JForumTag {
	
	protected String charEncoding;                // 'charEncoding' attrib.
	
	protected String url;                                // 'url' attribute
	
	/**
	 * @param charEncoding the charEncoding to set
	 */
	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		// check the URL
		if (StringUtils.isEmpty(url))
		    throw new NullAttributeException("import", "url");
		
		String jsp = this.getFile(url);
		
		ServletRequest request  = this.request();
		ServletResponse respose = this.response();
		HttpSession session = ((HttpServletRequest)request).getSession();
		ServletContext servletContext = session.getServletContext();
		
		String jspPath = servletContext.getRealPath(jsp);
		File jspFile = new File(jspPath);
		if(!jspFile.exists())
			return ;
		
		respose.flushBuffer();
		RequestDispatcher rd = this.pageContext().getRequest().getRequestDispatcher(jsp);
		try {
			 // include the resource, using our custom wrapper
		    ImportResponseWrapper irw = new ImportResponseWrapper((HttpServletResponse) respose);
		    irw.setCharacterEncoding(charEncoding);
			rd.include(request, irw);
			// disallow inappropriate response codes per JSTL spec
		    if (irw.getStatus() < 200 || irw.getStatus() > 299) {
		    	throw new JspTagException(irw.getStatus() + " " + jsp);
		    }

		    // recover the response String from our wrapper
		    pageContext().getOut().print(irw.getString());
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	private String getFile(String item){
		return new StringBuilder(128)
		.append('/').append(this.config().getValue(ConfigKeys.TEMPLATE_DIRECTORY)).append('/')
		.append(this.config().getValue(ConfigKeys.TEMPLATE_NAME))
		.append(item)
		.toString();
	}
}
