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
package net.jforum.actions;

import java.io.IOException;
import java.util.Date;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.exceptions.ForumException;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Post;
import net.jforum.repository.ForumRepository;
import net.jforum.services.ViewService;
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
import org.vraptor.annotations.Component;

/**
 * @author Rafael Steil
 */
@Component(Domain.ADMIN_SEARCH)
public class LuceneAdminActions {
	private JForumConfig config;
	private ViewPropertyBag propertyBag;
	private ForumRepository forumRepository;
	private SessionFactory sessionFactory;
	private ViewService viewService;

	public LuceneAdminActions(JForumConfig config, ViewPropertyBag propertyBag, ForumRepository forumRepository,
		ViewService viewService, SessionFactory sessionFactory) {
		this.config = config;
		this.propertyBag = propertyBag;
		this.forumRepository = forumRepository;
		this.viewService = viewService;
		this.sessionFactory = sessionFactory;
	}

	public void rebuildIndex() {

		Runnable indexingJob = new Runnable() {
			public void run() {
				Session session =  null;

				try {
					session = sessionFactory.openSession();

					FullTextSession fullTextSession = Search.createFullTextSession(session);
					fullTextSession.setFlushMode(FlushMode.MANUAL);
					fullTextSession.setCacheMode(CacheMode.IGNORE);

					session.beginTransaction();

					int index = 0;
					int batchSize = config.getInt(ConfigKeys.LUCENE_BATCH_SIZE);

					ScrollableResults results = fullTextSession.createCriteria(Post.class)
						.createAlias("topic", "t")
						.scroll(ScrollMode.FORWARD_ONLY);

					while (results.next() && "1".equals(config.getValue(ConfigKeys.LUCENE_CURRENTLY_INDEXING))) {
						index++;

						fullTextSession.index(results.get(0));

						if (index % batchSize == 0) {
							session.clear();
						}
					}

					session.getTransaction().commit();
				}
				catch (Exception e) {
					if (session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
				}
				finally {
					if (session.isOpen() && session.isConnected()) {
						session.close();
					}
				}
			}
		};

		config.addProperty(ConfigKeys.LUCENE_CURRENTLY_INDEXING, "1");

		Thread thread = new Thread(indexingJob);
		thread.start();

		viewService.redirectToAction(Actions.LIST);
	}

	public void cancelIndexing() {
		config.addProperty(ConfigKeys.LUCENE_CURRENTLY_INDEXING, "0");
		viewService.redirectToAction(Actions.LIST);
	}

	/**
	 * Shows the main statistics page
	 */
	public void list() {
		IndexReader indexReader = null;
		ReaderProvider readerProvider = null;

		try {
			SearchFactory searchFactory = Search.createFullTextSession(sessionFactory
				.getCurrentSession()).getSearchFactory();

			DirectoryProvider<?> directoryProvider = searchFactory.getDirectoryProviders(Post.class)[0];
			readerProvider = searchFactory.getReaderProvider();
			indexReader = readerProvider.openReader(directoryProvider);

			String indexDirectory = directoryProvider.getDirectory().toString();
			indexDirectory = indexDirectory.substring(indexDirectory.indexOf('@') + 1);

			boolean indexExists = IndexReader.indexExists(indexDirectory);

			propertyBag.put("indexExists", indexExists);

			if (indexExists) {
				propertyBag.put("numberOfDocs", indexReader.numDocs());
				propertyBag.put("indexLocation", indexDirectory);
				propertyBag.put("totalMessages", forumRepository.getTotalMessages());
				propertyBag.put("isLocked", IndexReader.isLocked(indexDirectory));
				propertyBag.put("lastModified", new Date(IndexReader.lastModified(indexDirectory)));
			}
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			if (readerProvider != null && indexReader != null) {
				readerProvider.closeReader(indexReader);
			}
		}
	}
}
