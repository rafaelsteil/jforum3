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

import javax.servlet.ServletContext;

import net.jforum.util.ConfigKeys;

import org.springframework.web.context.ConfigurableWebApplicationContext;

import br.com.caelum.vraptor.ioc.spring.SpringProvider;

public class VRaptorSpringProvider extends SpringProvider {
	@Override
	protected ConfigurableWebApplicationContext getParentApplicationContext(ServletContext context) {
		ConfigurableWebApplicationContext springContext = super.getParentApplicationContext(context);
		context.setAttribute(ConfigKeys.SPRING_CONTEXT, springContext);
		return springContext;
	}
}
