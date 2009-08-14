package org.hinjector.spring;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hinjector.HInjectorSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author Rafael Steil
 */
public class SpringSessionFactory extends HInjectorSessionFactory {
	private final ApplicationContext beanRegistry;

	public SpringSessionFactory(ApplicationContext beanRegistry, SessionFactory original) {
		super(original);
		this.beanRegistry = beanRegistry;
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession()
	 */
	public Session openSession() throws HibernateException {
		return this.getOriginal().openSession(new SpringInterceptor(this.beanRegistry, this.getOriginal()));
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession(java.sql.Connection)
	 */
	public Session openSession(Connection connection) {
		return this.getOriginal().openSession(connection, new SpringInterceptor(this.beanRegistry, this.getOriginal()));
	}

	/**
	 * @see org.hinjector.HInjectorSessionFactory#getOriginal()
	 */
	@Override
	public SessionFactory getOriginal() {
		return super.getOriginal();
	}

	/**
	 * @see org.hinjector.HInjectorSessionFactory#getCurrentSession()
	 */
	@Override
	public Session getCurrentSession() throws HibernateException {
		return (Session) SessionFactoryUtils.doGetSession(this, false);
	}
}
