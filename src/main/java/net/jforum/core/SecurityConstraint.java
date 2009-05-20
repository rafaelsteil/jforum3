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
package net.jforum.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jforum.security.AccessRule;
import net.jforum.security.EmptyRule;

/**
 * Used to enforce security rules in action's methods
 * @author Rafael Steil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SecurityConstraint {
	public Class<? extends AccessRule> value() default EmptyRule.class;
	public boolean displayLogin() default false;
	public Role[] multiRoles() default {};
}
