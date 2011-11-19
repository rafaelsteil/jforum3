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
package net.jforum.controllers;

import java.io.IOException;
import java.util.Date;

import net.jforum.actions.helpers.Domain;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Post;
import net.jforum.repository.ForumRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.lucene.index.IndexReader;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.ADMIN_SEARCH)
public class LuceneAdminController {
	private JForumConfig config;
	private ForumRepository forumRepository;
	private SessionFactory sessionFactory;
	private final Result result;

	public LuceneAdminController(JForumConfig config,
			ForumRepository forumRepository, SessionFactory sessionFactory,
			Result result) {
		this.config = config;
		this.forumRepository = forumRepository;
		this.sessionFactory = sessionFactory;
		this.result = result;
	}

	public void rebuildIndex() {

		Runnable indexingJob = new Runnable() {
			public void run() {
				Session session = null;

				try {
					session = sessionFactory.openSession();

					FullTextSession fullTextSession = Search
							.createFullTextSession(session);
					fullTextSession.setFlushMode(FlushMode.MANUAL);
					fullTextSession.setCacheMode(CacheMode.IGNORE);

					session.beginTransaction();

					int index = 0;
					int batchSize = config.getInt(ConfigKeys.LUCENE_BATCH_SIZE);

					ScrollableResults results = fullTextSession
							.createCriteria(Post.class)
							.createAlias("topic", "t")
							.scroll(ScrollMode.FORWARD_ONLY);

					while (results.next()
							&& "1".equals(config
									.getValue(ConfigKeys.LUCENE_CURRENTLY_INDEXING))) {
						index++;

						fullTextSession.index(results.get(0));

						if (index % batchSize == 0) {
							session.clear();
						}
					}

					session.getTransaction().commit();
				} catch (Exception e) {
					if (session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
				} finally {
					if (session.isOpen() && session.isConnected()) {
						session.close();
					}
				}
			}
		};

		this.config.addProperty(ConfigKeys.LUCENE_CURRENTLY_INDEXING, "1");

		Thread thread = new Thread(indexingJob);
		thread.start();

		this.result.redirectTo(this).list();
	}

	public void cancelIndexing() {
		this.config.addProperty(ConfigKeys.LUCENE_CURRENTLY_INDEXING, "0");
		this.result.redirectTo(this).list();
	}

	/**
	 * Shows the main statistics page
	 */
	public void list() {
		IndexReader indexReader = null;
		ReaderProvider readerProvider = null;

		try {
			SearchFactory searchFactory = Search.createFullTextSession(
					this.sessionFactory.getCurrentSession()).getSearchFactory();

			DirectoryProvider<?> directoryProvider = searchFactory
					.getDirectoryProviders(Post.class)[0];
			readerProvider = searchFactory.getReaderProvider();
			indexReader = readerProvider.openReader(directoryProvider);

			String indexDirectory = directoryProvider.getDirectory().toString();
			indexDirectory = indexDirectory.substring(indexDirectory
					.indexOf('@') + 1);

			boolean indexExists = IndexReader.indexExists(indexDirectory);

			this.result.include("indexExists", indexExists);

			if (indexExists) {
				this.result.include("numberOfDocs", indexReader.numDocs());
				this.result.include("indexLocation", indexDirectory);
				this.result.include("totalMessages",
						this.forumRepository.getTotalMessages());
				this.result.include("isLocked",
						IndexReader.isLocked(indexDirectory));
				this.result.include("lastModified",
						new Date(IndexReader.lastModified(indexDirectory)));
			}
		} catch (IOException e) {
			throw new ForumException(e);
		} finally {
			if (readerProvider != null && indexReader != null) {
				readerProvider.closeReader(indexReader);
			}
		}
	}
}
