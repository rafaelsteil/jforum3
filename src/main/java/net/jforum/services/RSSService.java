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
package net.jforum.services;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.RSSRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;
import yarfraw.core.datamodel.ChannelFeed;
import yarfraw.core.datamodel.FeedFormat;
import yarfraw.core.datamodel.ItemEntry;
import yarfraw.core.datamodel.YarfrawException;
import yarfraw.io.FeedWriter;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class RSSService {
	private JForumConfig config;
	private RSSRepository rssRepository;
	private I18n i18n;
	private ForumRepository forumRepository;

	public RSSService(JForumConfig config, RSSRepository rssRepository, I18n i18n, ForumRepository forumRepository) {
		this.config = config;
		this.rssRepository = rssRepository;
		this.i18n = i18n;
		this.forumRepository = forumRepository;
	}

	/**
	 * Generate RSS for the latest topics of a given forum
	 * @param forumId the forum id
	 * @return the rss contents
	 */
	public String forForum(int forumId) {
		Forum forum = this.forumRepository.get(forumId);

		List<Topic> topics = this.rssRepository.getForumTopics(forum,
			this.config.getInt(ConfigKeys.TOPICS_PER_PAGE));

		ChannelFeed feed = new ChannelFeed()
			.setTitle(this.i18n.getFormattedMessage("RSS.ForumTopics.title", this.i18n.params(forum.getName())))
			.addLink(this.buildForumLink(forum))
			.setDescriptionOrSubtitle(forum.getDescription());

		for (Topic topic : topics) {
			String topicLink = this.buildTopicLink(topic);

			feed.addItem(new ItemEntry()
				.addLink(topicLink)
				.setUid(topicLink)
				.setTitle(topic.getSubject())
				.setPubDate(this.formatDate(topic.getDate()))
				.setDescriptionOrSummary(topic.getLastPost().getText())); // TODO: do some formatting
		}

		return this.generateRSS(feed);
	}

	/**
	 * Create the RSS
	 * @param feed the feed to use as source
	 * @return the rss contents
	 */
	private String generateRSS(ChannelFeed feed) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			FeedWriter.writeChannel(FeedFormat.RSS20, feed, os);
		}
		catch (YarfrawException e) {
			throw new ForumException(e);
		}

		return os.toString();
	}

	/**
	 * Build the link for a specific forum
	 * @param forum the forum
	 * @return the forum link
	 */
	private String buildForumLink(Forum forum) {
		return new StringBuilder().append(this.config.getString(ConfigKeys.FORUM_LINK))
			.append(Domain.FORUMS).append('/').append(Actions.SHOW)
			.append('/').append(forum.getId()).append(this.config.getValue(ConfigKeys.SERVLET_EXTENSION))
			.toString();
	}

	/**
	 * Build the link to a specific topic
	 * @param topic the topic
	 * @return the link
	 */
	private String buildTopicLink(Topic topic) {
		return new StringBuilder().append(this.config.getString(ConfigKeys.FORUM_LINK))
			.append(Domain.TOPICS).append('/').append(Actions.PRE_LIST)
			.append('/').append(topic.getId()).append('/').append(topic.getLastPost().getId())
			.append(this.config.getValue(ConfigKeys.SERVLET_EXTENSION))
			.toString();
	}

	/**
	 * Format a date to the RSS format
	 * @param date the date format
	 * @return the formatted date
	 */
	private String formatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(this.config.getValue(
			ConfigKeys.RSS_DATE_TIME_FORMAT), Locale.ENGLISH);
		return df.format(date);
	}
}
