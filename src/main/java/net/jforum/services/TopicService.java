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
import net.jforum.entities.Forum;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.repository.UserRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class TopicService {
	private TopicRepository topicRepository;
	private PostRepository postRepository;
	private ForumRepository forumRepository;
	private AttachmentService attachmentService;
	private PollService pollService;
	private UserRepository userRepository;

	public TopicService(TopicRepository topicRepository, PostRepository postRepository,
		ForumRepository forumRepository, AttachmentService attachmentService, PollService pollService, UserRepository userRepository) {
		this.topicRepository = topicRepository;
		this.postRepository = postRepository;
		this.forumRepository = forumRepository;
		this.attachmentService = attachmentService;
		this.pollService = pollService;
		this.userRepository = userRepository;
	}

	/**
	 * Create a new topic.
	 * Saves a topic, as well the related first post. Date, user and subject
	 * of the associated post are forced to be the same value used
	 * by the topic.
	 * @param topic the topic to save
	 * @param pollOptions
	 * @param attachments
	 */
	public void addTopic(Topic topic, List<PollOption> pollOptions, List<AttachedFile> attachments) {
		this.performAddValidations(topic);

		if (topic.getDate() == null) {
			topic.setDate(new Date());
		}

		Post post = topic.getFirstPost();
		topic.setFirstPost(null);

		this.pollService.associatePoll(topic, pollOptions);

		topic.setHasAttachment(attachments.size() > 0);
		this.topicRepository.add(topic);

		post.setForum(topic.getForum());
		post.setTopic(topic);
		post.setDate(topic.getDate());
		post.setUser(topic.getUser());
		post.setSubject(topic.getSubject());

		this.attachmentService.insertAttachments(attachments, post);
		this.postRepository.add(post);

		topic.setFirstPost(post);
		topic.setLastPost(post);

		if (!topic.isWaitingModeration()) {
			Forum forum = this.forumRepository.get(topic.getForum().getId());
			forum.setLastPost(post);

			int userTotalPosts = this.userRepository.getTotalPosts(post.getUser());
			topic.getUser().setTotalPosts(userTotalPosts);
		}
	}

	/**
	 * Posts a reply to a topic
	 * @param topic the topic which will receive the reply
	 * @param post the reply itself
	 * @param attachments
	 */
	public void reply(Topic topic, Post post, List<AttachedFile> attachments) {
		Topic current = this.topicRepository.get(topic.getId());

		if (StringUtils.isEmpty(post.getSubject())) {
			post.setSubject(current.getSubject());
		}

		this.performReplyValidations(post);
		this.attachmentService.insertAttachments(attachments, post);

		if (attachments.size() > 0) {
			current.setHasAttachment(true);
		}

		topic.setForum(current.getForum());

		post.setTopic(current);
		post.setDate(new Date());
		post.setForum(current.getForum());

		this.postRepository.add(post);

		if (!post.isWaitingModeration()) {
			current.setLastPost(post);
			current.getForum().setLastPost(post);
			current.incrementTotalReplies();
			post.getUser().incrementTotalPosts();
		}
	}

	private void performReplyValidations(Post post) {
		this.performCommonPostValidations(post);
	}

	private void performAddValidations(Topic topic) {
		if (topic.getUser() == null) {
			throw new IllegalStateException("Cannot save a topic without an user");
		}

		if (StringUtils.isEmpty(topic.getSubject())) {
			throw new IllegalStateException("Cannot save a topic without a subject");
		}

		if (topic.getForum().getId() == 0) {
			throw new IllegalStateException("Cannot save a Topic without a forum");
		}

		this.performCommonPostValidations(topic.getFirstPost());
	}

	private void performCommonPostValidations(Post post) {
		if (StringUtils.isEmpty(post.getSubject())) {
			throw new IllegalStateException("Cannot save a post without a subject");
		}

		if (StringUtils.isEmpty(post.getText())) {
			throw new IllegalStateException("Cannot save a post without a message");
		}
	}
}
