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
package net.jforum.core.hibernate.tests;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;

/**
 * An empty provider to be used with unit testing
 * @author Rafael Steil
 */
public class EmptyCacheProvider implements CacheProvider {

	/**
	 * @see org.hibernate.cache.CacheProvider#buildCache(java.lang.String, java.util.Properties)
	 */
	public Cache buildCache(String regionName, Properties properties) throws CacheException {
		return new EmptyCache();
	}

	/**
	 * @see org.hibernate.cache.CacheProvider#isMinimalPutsEnabledByDefault()
	 */
	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}

	/**
	 * @see org.hibernate.cache.CacheProvider#nextTimestamp()
	 */
	public long nextTimestamp() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.CacheProvider#start(java.util.Properties)
	 */
	public void start(Properties properties) throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.CacheProvider#stop()
	 */
	public void stop() {
	}

}
