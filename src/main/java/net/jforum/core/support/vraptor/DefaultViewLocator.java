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
package net.jforum.core.support.vraptor;

import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;
import org.vraptor.LogicException;
import org.vraptor.annotations.Remotable;
import org.vraptor.component.LogicMethod;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.url.InvalidURLException;
import org.vraptor.url.ViewLocator;
import org.vraptor.view.RemoteView;
import org.vraptor.view.RemoteViewManager;
import org.vraptor.view.ViewManager;

/**
 * @author Rafael Steil
 */
public class DefaultViewLocator implements ViewLocator {

	private final String ACCEPTREQUESTEADER = "Accept";
	private final String XREQUESTEADER = "X-Requested-With";
	private final String XHRHEADERVALUE = "XMLHttpRequest";

	private final String SUPPORTEDXML  = "XML";
	private final String SUPPORTEDJSON  = "AJAX";

	/**
	 * @see org.vraptor.url.ViewLocator#locate(javax.servlet.http.HttpServletRequest, org.vraptor.component.LogicMethod, org.vraptor.view.ViewManager)
	 */
	public ViewManager locate(VRaptorServletRequest req, LogicMethod method, ViewManager defaultViewManager) throws InvalidURLException, LogicException {

		/*
		//TODO allow view managers to be expanded by registering your own managers...
		// this code should look go for each one as in the converter finder
		*/
		if(isXhr(req)){
			String viewType = this.getViewType(req);
			if ((SUPPORTEDXML.equals(viewType) || SUPPORTEDJSON.equals(viewType))) {
				if(!method.getMetadata().isAnnotationPresent(Remotable.class)) {
					throw new LogicException("logic method is not @Remotable");
				}
				return new RemoteViewManager(defaultViewManager, RemoteView.valueOf(viewType.toUpperCase()));
			}
		}
		return defaultViewManager;
	}

	/**
	 * is it XMLHttpRequest?
	 * when JQuery send a ajax request to remote, it will send the header.
	 * xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
	 * @param req
	 * @return
	 */
	private boolean isXhr(VRaptorServletRequest req){
		return XHRHEADERVALUE.equals(req.getHeader(XREQUESTEADER));
	}

	/**
	 * get RemoteView according to request.
	 * HTTP Header from JQuery XHR:
	 * Accept: application/json, text/javascript, * / *
	 * Accept: application/xml, text/xml, * / *
	 * However, IE6 will send
	 * Accept: * / *
	 * and
	 * accept: application/json, text/javascript, * / *
	 * at same time
	 * @param req
	 * @return
	 */
	private String getViewType(VRaptorServletRequest req){
		Enumeration<?> accepts = req.getHeaders(ACCEPTREQUESTEADER);//IE6 will send two Accept header

		StringBuilder acceptBuilder = new StringBuilder();
		for(;accepts.hasMoreElements();){
			acceptBuilder.append(accepts.nextElement());
			acceptBuilder.append(", ");
		}

		String accept = acceptBuilder.toString();

		if(StringUtils.isEmpty(accept))
			return null;
		if(accept.indexOf("application/json")>-1){
			return SUPPORTEDJSON;
		}else if(accept.indexOf("application/xml")>-1){
			return SUPPORTEDXML;
		}
		return null;

	}
}
