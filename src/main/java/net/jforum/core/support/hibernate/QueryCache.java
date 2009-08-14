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
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.StandardQueryCache;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;

/**
 * @author Rafael Steil
 */
public class QueryCache extends StandardQueryCache {
	private boolean isUpToDate = true;

	/**
	 * @param settings
	 * @param props
	 * @param updateTimestampsCache
	 * @param regionName
	 * @throws HibernateException
	 */
	public QueryCache(Settings settings, Properties props, UpdateTimestampsCache updateTimestampsCache, String regionName)
			throws HibernateException {
		super(settings, props, updateTimestampsCache, regionName);
	}

	/**
	 * @see org.hibernate.cache.StandardQueryCache#clear()
	 */
	@Override
	public void clear() throws CacheException {
		super.clear();
		this.isUpToDate = true;
	}

	/**
	 * @see org.hibernate.cache.StandardQueryCache#isUpToDate(java.util.Set, java.lang.Long)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected boolean isUpToDate(Set spaces, Long timestamp) {
		return this.isUpToDate;
	}
}
