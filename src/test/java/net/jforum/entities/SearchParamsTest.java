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
package net.jforum.entities;

import net.jforum.entities.util.SearchMatchType;
import net.jforum.entities.util.SearchParams;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Filipe Sabelle
 */
public class SearchParamsTest {
	@Test
	public void testSearchParamsQueryWithOr() {
		SearchParams p = new SearchParams(); p.setQuery("test query words"); p.setMatchType(SearchMatchType.OR);
		Assert.assertEquals("(test query words  ) or (subject:test subject:query subject:words  )", p.buildQuery());
	}
	@Test
	public void testSearchParamsQueryWithAnd() {
		SearchParams p = new SearchParams(); p.setQuery("test query words"); p.setMatchType(SearchMatchType.AND);
		Assert.assertEquals("(+test +query +words  ) or (+subject:test +subject:query +subject:words  )", p.buildQuery());
	}
	@Test
	public void testSearchParamsQueryWithForumId() {
		Forum f = new Forum(); f.setId(1);
		SearchParams p = new SearchParams(); p.setQuery("test query words"); p.setMatchType(SearchMatchType.AND); p.setForum(f);
		Assert.assertEquals("(+test +query +words  and +topic.forum.id:1) or (+subject:test +subject:query +subject:words  and +topic.forum.id:1)", p.buildQuery());
	}

	@Test
	public void testSearchParamsQueryWithQuotesAndAnd() {
		SearchParams p = new SearchParams(); p.setQuery("test 'query words' with quotes"); p.setMatchType(SearchMatchType.AND);
		Assert.assertEquals("(+test +'query words' +with +quotes  ) or (+subject:test +subject:'query words' +subject:with +subject:quotes  )", p.buildQuery());
	}
	@Test
	public void testSearchParamsQueryWithDoubleQuotesAndAnd() {
		SearchParams p = new SearchParams(); p.setQuery("test \"query words\" with quotes"); p.setMatchType(SearchMatchType.AND);
		Assert.assertEquals("(+test +\"query words\" +with +quotes  ) or (+subject:test +subject:\"query words\" +subject:with +subject:quotes  )", p.buildQuery());
	}
}