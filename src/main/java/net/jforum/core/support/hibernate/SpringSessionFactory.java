package net.jforum.core.support.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.springframework.context.ApplicationContext;

/**
 * @author Rafael Steil
 */
public class SpringSessionFactory implements SessionFactory {
	private final ApplicationContext applicationContext;
	private final SessionFactory original;

	public SpringSessionFactory(ApplicationContext applicationContext, SessionFactory original) {
		this.original = original;
		this.applicationContext = applicationContext;
	}

	@Override
	public Session openSession() throws HibernateException {
		return original.openSession(new SpringInterceptor(this.applicationContext, original));
	}

	@Override
	public Session openSession(Connection connection) {
		return original.openSession(connection, new SpringInterceptor(this.applicationContext, original));
	}

	@Override
	public Session openSession(Interceptor interceptor) throws HibernateException {
        return original.openSession(interceptor);
    }

    @Override
	public Session openSession(Connection connection, Interceptor interceptor) {
        return original.openSession(connection, interceptor);
    }

    @Override
	public Session getCurrentSession() throws HibernateException {
        return original.getCurrentSession();
    }

	@Override
	@SuppressWarnings("rawtypes")
	public ClassMetadata getClassMetadata(Class aClass) throws HibernateException {
        return original.getClassMetadata(aClass);
    }

    @Override
	public ClassMetadata getClassMetadata(String s) throws HibernateException {
        return original.getClassMetadata(s);
    }

    @Override
	public CollectionMetadata getCollectionMetadata(String s) throws HibernateException {
        return original.getCollectionMetadata(s);
    }

    @Override
	@SuppressWarnings("rawtypes")
	public Map getAllClassMetadata() throws HibernateException {
        return original.getAllClassMetadata();
    }

    @Override
	@SuppressWarnings("rawtypes")
	public Map getAllCollectionMetadata() throws HibernateException {
        return original.getAllCollectionMetadata();
    }

    @Override
	public Statistics getStatistics() {
        return original.getStatistics();
    }

    @Override
	public void close() throws HibernateException {
        original.close();
    }

    @Override
	public boolean isClosed() {
        return original.isClosed();
    }

	@Override
	@SuppressWarnings("rawtypes")
	public void evict(Class aClass) throws HibernateException {
        original.evict(aClass);
    }

	@Override
	@SuppressWarnings("rawtypes")
	public void evict(Class aClass, Serializable serializable) throws HibernateException {
        original.evict(aClass, serializable);
    }

    @Override
	public void evictEntity(String s) throws HibernateException {
        original.evictEntity(s);
    }

    @Override
	public void evictEntity(String s, Serializable serializable) throws HibernateException {
        original.evictEntity(s, serializable);
    }

    @Override
	public void evictCollection(String s) throws HibernateException {
        original.evictCollection(s);
    }

    @Override
	public void evictCollection(String s, Serializable serializable) throws HibernateException {
        original.evictCollection(s, serializable);
    }

    @Override
	public void evictQueries() throws HibernateException {
        original.evictQueries();
    }

    @Override
	public void evictQueries(String s) throws HibernateException {
        original.evictQueries(s);
    }

    @Override
	public StatelessSession openStatelessSession() {
        return original.openStatelessSession();
    }

    @Override
	public StatelessSession openStatelessSession(Connection connection) {
        return original.openStatelessSession(connection);
    }

	@Override
	public Set<?> getDefinedFilterNames() {
        return original.getDefinedFilterNames();
    }

    @Override
	public FilterDefinition getFilterDefinition(String s) throws HibernateException {
        return original.getFilterDefinition(s);
    }

    @Override
	public Reference getReference() throws NamingException {
        return original.getReference();
    }
}
