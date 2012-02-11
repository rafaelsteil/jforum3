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

import net.jforum.core.support.hibernate.SessionFactoryCreator;
import net.jforum.util.ConfigKeys;

import org.springframework.web.context.ConfigurableWebApplicationContext;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.util.hibernate.HibernateTransactionInterceptor;
import br.com.caelum.vraptor.util.hibernate.SessionCreator;

/**
 * @author Rafael Steil
 */
public class VRaptorSpringProvider extends SpringProvider {

	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		registry.register(SessionCreator.class, SessionCreator.class);
		registry.register(SessionFactoryCreator.class, SessionFactoryCreator.class);
		registry.register(HibernateTransactionInterceptor.class, HibernateTransactionInterceptor.class);
	}

	@Override
	protected ConfigurableWebApplicationContext getParentApplicationContext(ServletContext context) {
		ConfigurableWebApplicationContext springContext = super.getParentApplicationContext(context);
		context.setAttribute(ConfigKeys.SPRING_CONTEXT, springContext);
		return springContext;
	}
}
