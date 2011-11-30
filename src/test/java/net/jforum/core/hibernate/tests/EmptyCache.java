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

import java.util.Map;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;

/**
 * @author Rafael Steil
 */
public class EmptyCache implements Cache {

	/**
	 * @see org.hibernate.cache.Cache#clear()
	 */
	public void clear() throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#destroy()
	 */
	public void destroy() throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#get(java.lang.Object)
	 */
	public Object get(Object key) throws CacheException {
		return null;
	}

	/**
	 * @see org.hibernate.cache.Cache#getElementCountInMemory()
	 */
	public long getElementCountInMemory() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.Cache#getElementCountOnDisk()
	 */
	public long getElementCountOnDisk() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.Cache#getRegionName()
	 */
	public String getRegionName() {
		return null;
	}

	/**
	 * @see org.hibernate.cache.Cache#getSizeInMemory()
	 */
	public long getSizeInMemory() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.Cache#getTimeout()
	 */
	public int getTimeout() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.Cache#lock(java.lang.Object)
	 */
	public void lock(Object key) throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#nextTimestamp()
	 */
	public long nextTimestamp() {
		return 0;
	}

	/**
	 * @see org.hibernate.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public void put(Object key, Object value) throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#read(java.lang.Object)
	 */
	public Object read(Object key) throws CacheException {
		return null;
	}

	/**
	 * @see org.hibernate.cache.Cache#remove(java.lang.Object)
	 */
	public void remove(Object key) throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#toMap()
	 */
	@SuppressWarnings("rawtypes")
	public Map toMap() {
		return null;
	}

	/**
	 * @see org.hibernate.cache.Cache#unlock(java.lang.Object)
	 */
	public void unlock(Object key) throws CacheException {
	}

	/**
	 * @see org.hibernate.cache.Cache#update(java.lang.Object, java.lang.Object)
	 */
	public void update(Object key, Object value) throws CacheException {
	}

}
