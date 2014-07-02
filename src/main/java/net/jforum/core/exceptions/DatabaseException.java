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
 * @version $Id: DatabaseException.java,v 1.1.2.1 2007/02/25 18:52:28 rafaelsteil Exp $
 */
public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 5979182004250721653L;

	public DatabaseException(String message) {
		super(message);
	}
	
	public DatabaseException(String message, Throwable t) {
		super(message, t);
		this.setStackTrace(t.getStackTrace());
	}
	
	public DatabaseException(Throwable t) {
		super(t);
		this.setStackTrace(t.getStackTrace());
	}
}
