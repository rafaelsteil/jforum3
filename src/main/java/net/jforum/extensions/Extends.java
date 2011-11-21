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
 * Used to create extension points to any controller's public action.
 * JForum allows extension points that can take action after the regular
 * execution of any controller's action, in order to enhance the functionality
 * or even change the behaviour.
 *
 * @author Rafael Steil
 * @author Bill
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Extends {

	/**
	 * The name of the form parameters that will be parsed
	 * from the request to be injected as this logic arguments.
	 *
	 * If not present, VRaptor will use the argument class name decapitalized
	 * when searching in the request parameters.
	 *
	 * <code>
	 * void method(SomeClass argument, OtherClass argument2)
	 * </code>
	 *
	 * will look fo servlet request parameters "someClass"
	 * and "otherClass" repectively.  Or you can name them
	 * using this annotation attribute.
	 *
	 * The array must have length equals to the number of arguments
	 * that this logic method receives.
	 *
	 */
	public String[] parameters() default {};

	/**
	 * Extend Logic names. A Extend Logic can extend more than one logic method.
	 * the default value is the method name,
	 * so it should be same with the method that need to extended
	 *
	 * @return names
	 */
	public String[] value() default {};
}
