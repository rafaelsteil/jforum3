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

import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.repository.PostRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 */
@ActionExtension(Domain.POSTS)
public class PostExtension {
	private TagService tagService;
	private PostRepository postRepository;
	private final Result result;

	public PostExtension(PostRepository postRepository, TagService tagService, Result result) {
		this.postRepository = postRepository;
		this.tagService = tagService;
		this.result = result;
	}

	@Extends(Actions.EDIT)
	public void edit() {
		Post post = (Post) result.included().get("post");

		if (post != null) {
			Topic topic = post.getTopic();

			if (post.equals(topic.getFirstPost())) {
				result.include("tags", tagService.getTagString(topic));
			}
		}
	}

	@Extends(Actions.EDITSAVE)
	public void editSave(Post post, String tagString) {
		post = this.postRepository.get(post.getId());

		if (post == null) {
			return;
		}

		Topic topic = post.getTopic();
		if (post.equals(topic.getFirstPost())) {
			List<Tag> tags = tagService.getTag(topic);

			if (tagService.getTagString(topic).equals(tagString.trim())) {
				return;
			}

			// changed. remove all the old tags
			this.tagService.remove(tags);

			// add the new tag if not empty
			if (StringUtils.isNotEmpty(tagString)) {
				this.tagService.addTag(tagString, topic);
			}
		}
	}
}
