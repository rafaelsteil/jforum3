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

import java.util.ArrayList;
import java.util.List;

import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.entities.Forum;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;

import org.apache.commons.lang.ArrayUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ModerationService {
	private PostRepository postRepository;
	private ForumRepository forumRepository;
	private TopicRepository topicRepository;
	private ModerationLogService moderationLogService;

	public ModerationService(PostRepository postRepository, ForumRepository forumRepository, TopicRepository topicRepository,
			ModerationLogService moderationLogService) {
		this.postRepository = postRepository;
		this.forumRepository = forumRepository;
		this.topicRepository = topicRepository;
		this.moderationLogService = moderationLogService;
	}

	/**
	 * Move a set of topics to another forum
	 * @param toForumId the id of the new forum
	 * @param topicIds the id of the topics to move
	 * @param moderationLog
	 */
	public void moveTopics(int toForumId, ModerationLog moderationLog, int... topicIds) {
		if (ArrayUtils.isEmpty(topicIds)) {
			return;
		}

		Forum newForum = this.forumRepository.get(toForumId);
		Forum oldForum = this.topicRepository.get(topicIds[0]).getForum();

		this.forumRepository.moveTopics(newForum, topicIds);

		newForum.setLastPost(this.forumRepository.getLastPost(newForum));
		oldForum.setLastPost(this.forumRepository.getLastPost(oldForum));

		this.moderationLogService.registerMovedTopics(moderationLog, topicIds);
	}

	/**
	 * Lock or unlock a set of topics
	 * @param topicIds the id of the topics to lock or unlock
	 * @param moderationLog
	 */
	public void lockUnlock(int[] topicIds, ModerationLog moderationLog) {
		if (ArrayUtils.isEmpty(topicIds)) {
			return;
		}

		for (int topicId : topicIds) {
			Topic topic = this.topicRepository.get(topicId);

			if (topic.isLocked()) {
				topic.unlock();
			}
			else {
				topic.lock();
			}
		}

		this.moderationLogService.registerLockedTopics(moderationLog, topicIds);
	}

	/**
	 * Delete a set of topics.
	 * @param topics the topics to delete
	 * @param moderationLog
	 */
	public void deleteTopics(List<Topic> topics, ModerationLog moderationLog) {
		List<Topic> topicsForModeration = new ArrayList<Topic>();

		for (Topic topic : topics) {
			topicsForModeration.add(this.topicRepository.get(topic.getId()));
			this.topicRepository.remove(topic);
		}

		this.moderationLogService.registerDeleteTopics(topicsForModeration, moderationLog);
	}

	/**
	 * Process a set of approval data
	 * @param forumId
	 * @param infos
	 */
	public void doApproval(int forumId, List<ApproveInfo> infos) {
		if (infos == null || infos.size() == 0) {
			return;
		}

		for (ApproveInfo info : infos) {
			if (!info.defer()) {
				Post post = this.postRepository.get(info.getPostId());

				if (post != null) {
					if (info.approve()) {
						this.approvePost(post);
					}
					else if (info.reject()) {
						this.denyPost(post);
					}
				}
			}
		}

		Forum forum = this.forumRepository.get(forumId);
		forum.setLastPost(this.forumRepository.getLastPost(forum));
	}

	private void denyPost(Post post) {
		this.postRepository.remove(post);
	}

	public void approvePost(Post post) {
		Topic topic = post.getTopic();

		if (topic.isWaitingModeration()) {
			topic.setPendingModeration(false);
		}
		else {
			topic.incrementTotalReplies();
		}

		post.setModerate(false);
		post.getUser().incrementTotalPosts();
		topic.setLastPost(this.topicRepository.getLastPost(topic));
	}
}
