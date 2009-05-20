package org.hinjector.spring;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Rafael Steil
 */
public class SpringInterceptor extends EmptyInterceptor {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final SessionFactory sessionFactory;
	private final ApplicationContext beanRegistry;

	public SpringInterceptor(ApplicationContext beanRegistry, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.beanRegistry = beanRegistry;
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#instantiate(java.lang.String, org.hibernate.EntityMode, java.io.Serializable)
	 */
	@Override
	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
		if (!this.isSupported(entityMode)) {
			return null;
		}

		Object instance = beanRegistry.getBean(entityName);
		this.fillId(entityName, instance, id);

		return instance;
	}

    private boolean isSupported(EntityMode entityMode) {
        return EntityMode.POJO.equals(entityMode);
    }

    private void fillId(String entityName, Object instance, Serializable id) {
        sessionFactory.getClassMetadata(entityName).setIdentifier(instance, id, EntityMode.POJO);
    }
}
