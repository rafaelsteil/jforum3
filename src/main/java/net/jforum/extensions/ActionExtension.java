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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Rafael Steil
 * @author Bill
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.TYPE )
public @interface ActionExtension {
	
	/**
	 * the className will be default value
	 * 
	 * @return extend component name
	 */
	public String value() default ""; 
}
