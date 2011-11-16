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
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.core.tags.URLTag;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.TopicRepository;
import net.jforum.security.ReplyTopicRule;
import net.jforum.services.ViewService;

import org.apache.commons.lang.StringUtils;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;

/**
 * @author Bill
 */
@Component("tag")
@InterceptedBy( { MethodSecurityInterceptor.class })
public class TagAction {

	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private TagService tagService;
	private TopicRepository topicRepository;
	private UserSession userSession;

	public TagAction(ViewPropertyBag propertyBag, TagService tagService, TopicRepository topicRepository,
			ViewService viewService, UserSession userSession) {
		this.propertyBag = propertyBag;
		this.tagService = tagService;
		this.topicRepository = topicRepository;
		this.viewService = viewService;
		this.userSession = userSession;
	}

	/**
	 * Find all topics that use the specified tag
	 */
	public void find(@Parameter(key = "tag") String tag) {
		try { tag = URLDecoder.decode(tag, URLTag.URL_ENCODE); }
		catch (UnsupportedEncodingException e) { }

		List<Topic> topics = this.tagService.search(tag, this.userSession.getRoleManager());

		propertyBag.put("topics", topics);
		propertyBag.put("tag", tag);
	}

	@SecurityConstraint(ReplyTopicRule.class)
	public void reply(@Parameter(key = "topicId") int topicId) {
		Topic topic = this.topicRepository.get(topicId);
		Forum forum = topic.getForum();
		propertyBag.put("forum", forum);
		propertyBag.put("topic", topic);
		propertyBag.put("tags", this.tagService.getTagString(topic));
	}

	@SecurityConstraint(ReplyTopicRule.class)
	public void replySave(@Parameter(key = "topic") Topic topic, @Parameter(key = "tag") String tagString) {
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

		propertyBag.put("tags", tagClass);
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
