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

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;

/**
 * @author Rafael Steil
 */
public class QueryCacheFactory implements org.hibernate.cache.QueryCacheFactory {
	/**
	 * @see org.hibernate.cache.QueryCacheFactory#getQueryCache(java.lang.String, org.hibernate.cache.UpdateTimestampsCache, org.hibernate.cfg.Settings, java.util.Properties)
	 */
	public QueryCache getQueryCache(String regionName, UpdateTimestampsCache updateTimestampsCache, Settings settings, Properties props)
			throws HibernateException {
		return new net.jforum.core.support.hibernate.QueryCache(settings, props, updateTimestampsCache, regionName);
	}

}
