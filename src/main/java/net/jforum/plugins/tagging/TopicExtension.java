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

import net.jforum.actions.TopicActions;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Topic;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;

import org.apache.commons.lang.StringUtils;
import org.vraptor.annotations.Parameter;

/**
 * Topic Post extension for {@link TopicActions}
 * @author Bill
 *
 */
@ActionExtension(Domain.TOPICS)
public class TopicExtension {
	private ViewPropertyBag propertyBag;
	private TagService tagService;

	public TopicExtension(ViewPropertyBag propertyBag, TagService tagService) {
		this.propertyBag = propertyBag;
		this.tagService = tagService;
	}

	@Extends(Actions.ADDSAVE)
	public void afterSave(@Parameter(key = "tags") String tagString) {
		if(StringUtils.isNotEmpty(tagString)){
			Topic topic = (Topic)propertyBag.get("topic");
			if(topic == null)
				return;

			tagService.addTag(tagString,topic);
		}
	}
}
