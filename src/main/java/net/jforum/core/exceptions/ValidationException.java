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
package net.jforum.core.exceptions;

/**
 * @author Rafael Steil
 */
public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = -3577897862011575132L;

	public ValidationException(String message) {
		super(message);
	}
}
