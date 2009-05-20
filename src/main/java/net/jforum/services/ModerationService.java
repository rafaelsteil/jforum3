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

import java.util.List;

import net.jforum.actions.helpers.ApproveInfo;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.TopicRepository;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Rafael Steil
 */
public class ModerationService {
	private PostRepository postRepository;
	private ForumRepository forumRepository;
	private TopicRepository topicRepository;

	public ModerationService(PostRepository postRepository, ForumRepository forumRepository,
		TopicRepository topicRepository) {
		this.postRepository = postRepository;
		this.forumRepository = forumRepository;
		this.topicRepository = topicRepository;
	}

	/**
	 * Required by CGLib. Use {@link #ModerationService(PostRepository, ForumRepository, TopicRepository)} instead
	 */
	public ModerationService() { }

	/**
	 * Move a set of topics to another forum
	 * @param toForumId the id of the new forum
	 * @param topicIds the id of the topics to move
	 */
	public void moveTopics(int toForumId, int... topicIds) {
		if (ArrayUtils.isEmpty(topicIds)) {
			return;
		}

		Forum newForum = forumRepository.get(toForumId);
		Forum oldForum = topicRepository.get(topicIds[0]).getForum();

		forumRepository.moveTopics(newForum, topicIds);

		newForum.setLastPost(forumRepository.getLastPost(newForum));
		oldForum.setLastPost(forumRepository.getLastPost(oldForum));
	}

	/**
	 * Lock or unlock a set of topics
	 * @param topicIds the id of the topics to lock or unlock
	 */
	public void lockUnlock(int... topicIds) {
		if (ArrayUtils.isEmpty(topicIds)) {
			return;
		}

		for (int topicId : topicIds) {
			Topic topic = topicRepository.get(topicId);

			if (topic.isLocked()) {
				topic.unlock();
			}
			else {
				topic.lock();
			}
		}
	}

	/**
	 * Delete a set of topics.
	 * @param topics the topics to delete
	 */
	public void deleteTopics(List<Topic> topics) {
		for (Topic topic : topics) {
			topicRepository.remove(topic);
		}
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
				Post post = postRepository.get(info.getPostId());

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

		Forum forum = forumRepository.get(forumId);
		forum.setLastPost(forumRepository.getLastPost(forum));
	}

	private void denyPost(Post post) {
		postRepository.remove(post);
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
		topic.setLastPost(topicRepository.getLastPost(topic));
	}
}
