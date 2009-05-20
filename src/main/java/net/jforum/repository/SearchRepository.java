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
package net.jforum.repository;

import net.jforum.entities.util.SearchParams;
import net.jforum.entities.util.SearchResult;

import org.apache.lucene.queryParser.ParseException;

/**
 * @author Rafael Steil
 */
public interface SearchRepository {
	public SearchResult search(SearchParams params) throws ParseException;
}
