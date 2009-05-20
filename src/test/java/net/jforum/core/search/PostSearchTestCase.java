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
package net.jforum.core.search;

import java.util.List;

import net.jforum.entities.Post;
import net.jforum.util.JDBCLoader;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Filipe Sabella
 */
public class PostSearchTestCase {
	private static SessionFactory sessionFactory;
	private Session session;
	private FullTextSession fullTextSession;

	@Test
	public void testSearch1() throws ParseException {
		Assert.assertEquals(1, createQuery("Structured").list().size());
	}
	@Test
	public void testSearch2() throws ParseException {
		Assert.assertEquals(8, createQuery("SettingsFactory").list().size());
	}
	@Test
	public void testSearchAnd() throws ParseException {
		Assert.assertEquals(3, createQuery("SettingsFactory AND disabled").list().size());
	}
	@Test
	public void testSearchOr() throws ParseException {
		Assert.assertEquals(9, createQuery("SettingsFactory OR 'Hibernate Search'").list().size());
	}

	private org.hibernate.Query createQuery(String criteria) throws ParseException {
		QueryParser parser = new QueryParser("text", new StopAnalyzer());
		Query luceneQuery = parser.parse(criteria);
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, Post.class);
		return fullTextQuery;
	}

	@Before
	public void setUp() {
		session = sessionFactory.openSession();
		fullTextSession = Search.createFullTextSession(session);
	}

	@BeforeClass
	public static void before() {
		Configuration config = new AnnotationConfiguration();
		config.configure("/hibernate-tests.cfg.xml");
		sessionFactory = config.buildSessionFactory();

		addData(sessionFactory.openSession());
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private static void addData(Session session) {
		new JDBCLoader(session.connection()).run("/search/getTopics.sql");
		session.flush();
		FullTextSession fullTextSession = Search.createFullTextSession(session);

		List<Post> posts = session.createCriteria(Post.class).list();
		Transaction tx = fullTextSession.beginTransaction();

		for (Post post : posts) {
			fullTextSession.index(post);
		}

		tx.commit();
	}

	@AfterClass
	public static void after() {
		if(sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
