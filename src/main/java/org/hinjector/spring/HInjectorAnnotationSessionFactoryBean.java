package org.hinjector.spring;

import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * @author Rafael Steil
 */
public class HInjectorAnnotationSessionFactoryBean extends AnnotationSessionFactoryBean
	implements BeanFactoryPostProcessor, ApplicationContextAware {
	private ApplicationContext applicationContext;

	/**
	 * @see org.springframework.orm.hibernate3.AbstractSessionFactoryBean#wrapSessionFactoryIfNecessary(org.hibernate.SessionFactory)
	 */
	@Override
	protected SessionFactory wrapSessionFactoryIfNecessary(SessionFactory rawSf) {
		return new SpringSessionFactory(this.applicationContext, rawSf);
	}

	/**
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@SuppressWarnings("unchecked")
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;

		Set<String> entities = this.getSessionFactory().getAllClassMetadata().keySet();

		for (String entity : entities) {
			RootBeanDefinition definition = new RootBeanDefinition(this.getEntityClass(entity));
			definition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			definition.setAutowireCandidate(true);
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

			registry.registerBeanDefinition(entity, definition);
		}
	}

	/**
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private Class<?> getEntityClass(String entityName) {
		Class<?> entityClass;

		try {
			entityClass = Class.forName(entityName, false, this.getClass().getClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Invalid entity name: " + entityName, e);
		}

		return entityClass;
	}
}
