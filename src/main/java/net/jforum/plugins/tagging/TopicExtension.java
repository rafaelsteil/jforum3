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

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.controllers.TopicController;
import net.jforum.entities.Topic;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Topic Post extension for {@link TopicController}
 * @author Bill
 */
@ActionExtension(Domain.TOPICS)
@Component
public class TopicExtension {
	private TagService tagService;
	private final Result result;

	public TopicExtension(TagService tagService, Result result) {
		this.tagService = tagService;
		this.result = result;
	}

	@Extends(Actions.ADDSAVE)
	public void afterSave(String tagString) {
		if(StringUtils.isNotEmpty(tagString)){
			Topic topic = (Topic)result.included().get("topic");

			if(topic != null) {
				tagService.addTag(tagString, topic);
			}
		}
	}
}
