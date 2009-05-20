package org.hinjector;

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

/**
 * @Author Fabio Kung
 */
public abstract class HInjectorSessionFactory implements SessionFactory {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final SessionFactory original;

    public HInjectorSessionFactory(SessionFactory original) {
        this.original = original;
    }

    protected SessionFactory getOriginal() {
    	return original;
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        // TODO chain interceptors
        return original.openSession(interceptor);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        // TODO chain interceptors
        return original.openSession(connection, interceptor);
    }

    public Session getCurrentSession() throws HibernateException {
        return original.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
	public ClassMetadata getClassMetadata(Class aClass) throws HibernateException {
        return original.getClassMetadata(aClass);
    }

    public ClassMetadata getClassMetadata(String s) throws HibernateException {
        return original.getClassMetadata(s);
    }

    public CollectionMetadata getCollectionMetadata(String s) throws HibernateException {
        return original.getCollectionMetadata(s);
    }

    @SuppressWarnings("unchecked")
	public Map getAllClassMetadata() throws HibernateException {
        return original.getAllClassMetadata();
    }

    @SuppressWarnings("unchecked")
	public Map getAllCollectionMetadata() throws HibernateException {
        return original.getAllCollectionMetadata();
    }

    public Statistics getStatistics() {
        return original.getStatistics();
    }

    public void close() throws HibernateException {
        original.close();
    }

    public boolean isClosed() {
        return original.isClosed();
    }

    @SuppressWarnings("unchecked")
	public void evict(Class aClass) throws HibernateException {
        original.evict(aClass);
    }

    @SuppressWarnings("unchecked")
	public void evict(Class aClass, Serializable serializable) throws HibernateException {
        original.evict(aClass, serializable);
    }

    public void evictEntity(String s) throws HibernateException {
        original.evictEntity(s);
    }

    public void evictEntity(String s, Serializable serializable) throws HibernateException {
        original.evictEntity(s, serializable);
    }

    public void evictCollection(String s) throws HibernateException {
        original.evictCollection(s);
    }

    public void evictCollection(String s, Serializable serializable) throws HibernateException {
        original.evictCollection(s, serializable);
    }

    public void evictQueries() throws HibernateException {
        original.evictQueries();
    }

    public void evictQueries(String s) throws HibernateException {
        original.evictQueries(s);
    }

    public StatelessSession openStatelessSession() {
        return original.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return original.openStatelessSession(connection);
    }

    @SuppressWarnings("unchecked")
	public Set getDefinedFilterNames() {
        return original.getDefinedFilterNames();
    }

    public FilterDefinition getFilterDefinition(String s) throws HibernateException {
        return original.getFilterDefinition(s);
    }

    public Reference getReference() throws NamingException {
        return original.getReference();
    }
}
