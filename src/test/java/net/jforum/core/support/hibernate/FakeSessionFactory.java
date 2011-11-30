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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.StatelessSession;
import org.hibernate.cache.Cache;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;

/**
 * @author Rafael Steil
 */
@SuppressWarnings("rawtypes") 
public class FakeSessionFactory implements SessionFactoryImplementor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see org.hibernate.SessionFactory#close()
	 */
	public void close() throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evict(java.lang.Class)
	 */
	public void evict(Class persistentClass) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evict(java.lang.Class, java.io.Serializable)
	 */
	public void evict(Class persistentClass, Serializable id) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictCollection(java.lang.String)
	 */
	public void evictCollection(String roleName) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictCollection(java.lang.String, java.io.Serializable)
	 */
	public void evictCollection(String roleName, Serializable id) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictEntity(java.lang.String)
	 */
	public void evictEntity(String entityName) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictEntity(java.lang.String, java.io.Serializable)
	 */
	public void evictEntity(String entityName, Serializable id) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictQueries()
	 */
	public void evictQueries() throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#evictQueries(java.lang.String)
	 */
	public void evictQueries(String cacheRegion) throws HibernateException {
	}

	/**
	 * @see org.hibernate.SessionFactory#getAllClassMetadata()
	 */
	public Map getAllClassMetadata() throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getAllCollectionMetadata()
	 */
	public Map getAllCollectionMetadata() throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getClassMetadata(java.lang.Class)
	 */
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getClassMetadata(java.lang.String)
	 */
	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getCollectionMetadata(java.lang.String)
	 */
	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getCurrentSession()
	 */
	public Session getCurrentSession() throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getDefinedFilterNames()
	 */
	public Set getDefinedFilterNames() {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getFilterDefinition(java.lang.String)
	 */
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#getStatistics()
	 */
	public Statistics getStatistics() {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#isClosed()
	 */
	public boolean isClosed() {
		return false;
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession()
	 */
	public Session openSession() throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession(java.sql.Connection)
	 */
	public Session openSession(Connection connection) {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession(org.hibernate.Interceptor)
	 */
	public Session openSession(Interceptor interceptor) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#openSession(java.sql.Connection, org.hibernate.Interceptor)
	 */
	public Session openSession(Connection connection, Interceptor interceptor) {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#openStatelessSession()
	 */
	public StatelessSession openStatelessSession() {
		return null;
	}

	/**
	 * @see org.hibernate.SessionFactory#openStatelessSession(java.sql.Connection)
	 */
	public StatelessSession openStatelessSession(Connection connection) {
		return null;
	}

	/**
	 * @see javax.naming.Referenceable#getReference()
	 */
	public Reference getReference() throws NamingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getAllSecondLevelCacheRegions()
	 */
	public Map getAllSecondLevelCacheRegions() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getCollectionPersister(java.lang.String)
	 */
	public CollectionPersister getCollectionPersister(String role) throws MappingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getCollectionRolesByEntityParticipant(java.lang.String)
	 */
	public Set getCollectionRolesByEntityParticipant(String entityName) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getConnectionProvider()
	 */
	public ConnectionProvider getConnectionProvider() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getDialect()
	 */
	public Dialect getDialect() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getEntityNotFoundDelegate()
	 */
	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getEntityPersister(java.lang.String)
	 */
	public EntityPersister getEntityPersister(String entityName) throws MappingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getIdentifierGenerator(java.lang.String)
	 */
	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getImplementors(java.lang.String)
	 */
	public String[] getImplementors(String className) throws MappingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getImportedClassName(java.lang.String)
	 */
	public String getImportedClassName(String name) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getInterceptor()
	 */
	public Interceptor getInterceptor() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getNamedQuery(java.lang.String)
	 */
	public NamedQueryDefinition getNamedQuery(String queryName) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getNamedSQLQuery(java.lang.String)
	 */
	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getQueryCache()
	 */
	public QueryCache getQueryCache() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getQueryCache(java.lang.String)
	 */
	public QueryCache getQueryCache(String regionName) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getQueryPlanCache()
	 */
	public QueryPlanCache getQueryPlanCache() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getResultSetMapping(java.lang.String)
	 */
	public ResultSetMappingDefinition getResultSetMapping(String name) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getReturnAliases(java.lang.String)
	 */
	public String[] getReturnAliases(String queryString) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getReturnTypes(java.lang.String)
	 */
	public Type[] getReturnTypes(String queryString) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getSQLExceptionConverter()
	 */
	public SQLExceptionConverter getSQLExceptionConverter() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getSecondLevelCacheRegion(java.lang.String)
	 */
	public Cache getSecondLevelCacheRegion(String regionName) {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getSettings()
	 */
	public Settings getSettings() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getSqlFunctionRegistry()
	 */
	public SQLFunctionRegistry getSqlFunctionRegistry() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getStatisticsImplementor()
	 */
	public StatisticsImplementor getStatisticsImplementor() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getTransactionManager()
	 */
	public TransactionManager getTransactionManager() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#getUpdateTimestampsCache()
	 */
	public UpdateTimestampsCache getUpdateTimestampsCache() {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#openSession(java.sql.Connection, boolean, boolean, org.hibernate.ConnectionReleaseMode)
	 */
	public Session openSession(Connection connection, boolean flushBeforeCompletionEnabled, boolean autoCloseSessionEnabled,
			ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.SessionFactoryImplementor#openTemporarySession()
	 */
	public Session openTemporarySession() throws HibernateException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.Mapping#getIdentifierPropertyName(java.lang.String)
	 */
	public String getIdentifierPropertyName(String className) throws MappingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.Mapping#getIdentifierType(java.lang.String)
	 */
	public Type getIdentifierType(String className) throws MappingException {
		return null;
	}

	/**
	 * @see org.hibernate.engine.Mapping#getReferencedPropertyType(java.lang.String, java.lang.String)
	 */
	public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
		return null;
	}

}
