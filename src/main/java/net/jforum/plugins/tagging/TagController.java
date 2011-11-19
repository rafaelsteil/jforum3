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
package net.jforum.plugins.tagging;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.tags.URLTag;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ReplyTopicRule;
import net.jforum.services.ViewService;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 */
@Resource
@Path("tag")
public class TagController {
	private ViewService viewService;
	private TagService tagService;
	private TopicRepository topicRepository;
	private UserSession userSession;
	private final Result result;

	public TagController(TagService tagService, TopicRepository topicRepository,
			ViewService viewService, UserSession userSession, Result result) {
		this.result = result;
		this.tagService = tagService;
		this.topicRepository = topicRepository;
		this.viewService = viewService;
		this.userSession = userSession;
	}

	/**
	 * Find all topics that use the specified tag
	 */
	public void find(String tag) {
		try { tag = URLDecoder.decode(tag, URLTag.URL_ENCODE); }
		catch (UnsupportedEncodingException e) { }

		List<Topic> topics = this.tagService.search(tag, this.userSession.getRoleManager());

		result.include("topics", topics);
		result.include("tag", tag);
	}

	@SecurityConstraint(ReplyTopicRule.class)
	public void reply(int topicId) {
		Topic topic = this.topicRepository.get(topicId);
		Forum forum = topic.getForum();
		result.include("forum", forum);
		result.include("topic", topic);
		result.include("tags", this.tagService.getTagString(topic));
	}

	@SecurityConstraint(ReplyTopicRule.class)
	public void replySave(Topic topic, String tagString) {
		if (StringUtils.isNotEmpty(tagString) && topic != null) {
			tagService.addTag(tagString, topic);
			viewService.redirectToAction(Domain.TOPICS, Actions.LIST, topic.getId());
		}
	}

	/**
	 * list all the hot tags. the more hot, the bigger font size
	 */
	public void list() {
		Map<String, Integer> hotTagsWithGroupIndex = this.tagService.getHotTags(200, 7, userSession.getRoleManager());

		Map<String, String> tagClass = new LinkedHashMap<String, String>();
		for (Map.Entry<String, Integer> entry : hotTagsWithGroupIndex.entrySet()) {
			String tagName = entry.getKey();
			Integer groupIndex = entry.getValue();
			String cssClass = this.getClass(groupIndex);

			tagClass.put(tagName, cssClass);
		}

		result.include("tags", tagClass);
	}

	private String getClass(int groupIndex) {
		switch (groupIndex) {
			case 6:
				return "largest";
			case 5:
				return "verylarge";
			case 4:
				return "large";
			case 3:
				return "medium";
			case 2:
				return "small";
			case 1:
				return "verysmall";
			default:
				return "smallest";
		}
	}
}
