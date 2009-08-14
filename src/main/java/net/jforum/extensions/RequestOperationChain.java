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
package net.jforum.extensions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Rafael Steil
 */
public class RequestOperationChain implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	private List<String> operations = new ArrayList<String>();

	public void setOperations(List<String> operations) {
		this.operations = operations;
	}

	public void callAllOperations() {
		for (String operationClassName : this.operations) {
			RequestOperation operation = (RequestOperation)this.applicationContext.getBean(operationClassName);
			operation.execute();
		}
	}

	/**
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
