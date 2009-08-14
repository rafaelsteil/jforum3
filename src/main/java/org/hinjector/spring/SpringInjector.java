package org.hinjector.spring;

import org.hibernate.SessionFactory;
import org.hinjector.HInjector;
import org.springframework.context.ApplicationContext;

/**
 * @author Rafael Steil
 */
public class SpringInjector implements HInjector {
	private final ApplicationContext applicationContext;

	public SpringInjector(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @see org.hinjector.HInjector#support(org.hibernate.SessionFactory)
	 */
	public SessionFactory support(SessionFactory factory) {
		return new SpringSessionFactory(this.applicationContext, factory);
	}

}
