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

import java.util.Date;
import java.util.List;

import net.jforum.actions.helpers.AttachedFile;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PostService {
	private PostRepository postRepository;
	private AttachmentService attachmentService;
	private PollService pollService;
	private TopicRepository topicRepository;
	private ModerationLogService moderationLogService;

	public PostService(PostRepository postRepository, AttachmentService attachmentService,
			PollService pollService, TopicRepository topicRepository, ModerationLogService moderationLogService) {
		this.postRepository = postRepository;
		this.attachmentService = attachmentService;
		this.pollService = pollService;
		this.topicRepository = topicRepository;
		this.moderationLogService = moderationLogService;
	}

	/**
	 * Deletes an existing post
	 * @param postId
	 */
	public void delete(Post post) {
		this.attachmentService.deleteAllAttachments(post);
		this.postRepository.remove(post);
	}

	/**
	 * Updates an existing post
	 * @param post the post to update
	 * @param canChangeTopicType
	 * @param pollOptions
	 * @param attachments
	 * @param moderationLog
	 */
	public void update(Post post, boolean canChangeTopicType, List<PollOption> pollOptions,
			List<AttachedFile> attachments, ModerationLog moderationLog) {
		this.applySaveConstraints(post);

		Post currentPost = this.postRepository.get(post.getId());
		String originalPostMessage = currentPost.getText();

		currentPost.setSubject(post.getSubject());
		currentPost.setText(post.getText());
		currentPost.setEditDate(new Date());
		currentPost.incrementEditCount();

		this.copyFormattingOptions(post, currentPost);
		this.attachmentService.insertAttachments(attachments, currentPost);

		// TODO: Move to TopicPostEvent (?)
		Topic currentTopic = currentPost.getTopic();

		currentPost.setHasAttachments(currentPost.getAttachments().size() > 0);

		// FIXME: this will ovewrite the topic information about attachments
		currentTopic.setHasAttachment(currentPost.getHasAttachments());

		if (currentTopic.getFirstPost().equals(currentPost)) {
			currentTopic.setSubject(post.getSubject());

			if (canChangeTopicType) {
				currentTopic.setType(post.getTopic().getType());
			}

			if (!currentTopic.isPollEnabled()) {
				// Set a new poll
				currentTopic.setPoll(post.getTopic().getPoll());
				this.pollService.associatePoll(currentTopic, pollOptions);
			}
			else {
				// Update existing poll
				currentTopic.getPoll().setLabel(post.getTopic().getPoll().getLabel());
				currentTopic.getPoll().setLength(post.getTopic().getPoll().getLength());

				if (pollOptions != null && pollOptions.size() > 0) {
					this.pollService.processChanges(currentTopic.getPoll(), pollOptions);
				}
			}
		}

		this.postRepository.update(currentPost);
		this.topicRepository.update(currentTopic);

		this.moderationLogService.registerPostEdit(moderationLog, currentPost, originalPostMessage);
	}

	private void copyFormattingOptions(Post from, Post to) {
		to.setBbCodeEnabled(from.isBbCodeEnabled());
		to.setHtmlEnabled(from.isHtmlEnabled());
		to.setSmiliesEnabled(from.isSmiliesEnabled());
		to.setSignatureEnabled(from.isSignatureEnabled());
	}

	private void applySaveConstraints(Post post) {
		if (post == null) {
			throw new NullPointerException("Cannot update a null post");
		}

		if (post.getId() == 0) {
			throw new IllegalStateException("The post does not have an id");
		}

		if (StringUtils.isEmpty(post.getSubject())) {
			throw new IllegalStateException("The post should have a subject");
		}

		if (StringUtils.isEmpty(post.getText())) {
			throw new IllegalStateException("The post should have a message");
		}
	}
}
