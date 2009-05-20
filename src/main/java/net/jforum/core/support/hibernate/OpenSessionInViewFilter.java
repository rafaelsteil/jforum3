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
package net.jforum.core.support.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.jforum.util.ConfigKeys;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Rafael Steil
 */
public class OpenSessionInViewFilter implements Filter {
	private SessionFactory sessionFactory;
	private ServletContext servletContext;

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		this.ensureSessionFactoryIsInitialized();

		try {
			this.openAndBindSession();

			chain.doFilter(req, res);

			this.commitAndCloseSession();
		}
		catch (Exception e) {
			try {
				if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
					sessionFactory.getCurrentSession().getTransaction().rollback();
				}

				this.closeSession();
			}
			catch (Exception e2) { }

			throw new ServletException(e);
		}
		finally {
			try {
				TransactionSynchronizationManager.unbindResource(sessionFactory);
			}
			catch (IllegalStateException e) { }
		}
	}

	private void closeSession() {
		if (sessionFactory.getCurrentSession().isOpen()
			&& sessionFactory.getCurrentSession().isConnected()) {
			sessionFactory.getCurrentSession().close();
		}
	}

	private void commitAndCloseSession() {
		sessionFactory.getCurrentSession().getTransaction().commit();
		this.closeSession();
	}

	private void openAndBindSession() {
		TransactionSynchronizationManager.bindResource(sessionFactory,
			new SessionHolder(sessionFactory.openSession()));

		sessionFactory.getCurrentSession().beginTransaction();
	}

	private void ensureSessionFactoryIsInitialized() {
		if (sessionFactory == null) {
			ApplicationContext context = (ApplicationContext)servletContext.getAttribute(ConfigKeys.SPRING_CONTEXT);
			sessionFactory = (SessionFactory)context.getBean(SessionFactory.class.getName(), SessionFactory.class);
		}
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}
}
