package net.jforum.core.support.hibernate;

import java.io.Serializable;

import net.jforum.core.exceptions.ForumException;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Rafael Steil
 */
public class SpringInterceptor extends EmptyInterceptor {
	private final SessionFactory sessionFactory;
	private final ApplicationContext beanRegistry;

	public SpringInterceptor(ApplicationContext beanRegistry, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.beanRegistry = beanRegistry;
	}

	@Override
	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
		if (!EntityMode.POJO.equals(entityMode)) {
			return null;
		}

		Class<?> c = getClassByName(entityName);
		Object instance = this.beanRegistry.getBean(c);
		sessionFactory.getClassMetadata(c).setIdentifier(instance, id, EntityMode.POJO);

		return instance;
	}

	private Class<?> getClassByName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ForumException(e);
		}
	}
}
