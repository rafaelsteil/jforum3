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
package net.jforum.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.ActionUtils;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.AttachedFile;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.SecurityConstraint;
import net.jforum.entities.ModerationLog;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.PostRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.security.ChangePostRule;
import net.jforum.security.RoleManager;
import net.jforum.services.AttachmentService;
import net.jforum.services.PostService;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.POSTS)
//@InterceptedBy( {MultipartRequestInterceptor.class, MethodSecurityInterceptor.class, MethodInterceptorInterceptor.class, ExtensibleInterceptor.class })
public class PostActions {
	private PostRepository postRepository;
	private SmilieRepository smilieRepository;
	private PostService postService;
	private JForumConfig config;
	private UserSession userSession;
	private AttachmentService attachmentService;
	private HttpServletRequest request;
	private final Result result;

	public PostActions(PostRepository postRepository, SmilieRepository smilieRepository, 
			PostService postService, JForumConfig config, UserSession userSession,
		AttachmentService attachmentService, HttpServletRequest request, Result result) {
		this.postRepository = postRepository;
		this.smilieRepository = smilieRepository;
		this.postService = postService;
		this.config = config;
		this.userSession = userSession;
		this.attachmentService = attachmentService;
		this.request = request;
		this.result = result;
	}

	/**
	 * Deletes an existing post
	 * @param postId
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	public void delete(int postId) {
		Post post = this.postRepository.get(postId);
		Topic topic = post.getTopic();
		this.postService.delete(post);

		if (topic.getTotalPosts() > 0) {
			this.redirecToListing(topic);
		}
		else {
			//TODO pass zero?
			this.result.redirectTo(ForumActions.class).show(topic.getForum().getId(), 0);
		}
	}

	/**
	 * Saves an existing message
	 * @param post the message to save
	 * @param options the formatting options
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	public void editSave( Post post,  PostFormOptions options,
			List<PollOption> pollOptions,  ModerationLog moderationLog) {
		
		//TODO Verify this -> @Parameter(create=true) in param pollOptions
		
		ActionUtils.definePostOptions(post, options);
		post.getTopic().setType(options.getTopicType());

		Post currentPost = this.postRepository.get(post.getId());
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		RoleManager roleManager = this.userSession.getRoleManager();

		if (roleManager.isAttachmentsAlllowed(currentPost.getForum().getId())) {
			attachments = this.attachmentService.processNewAttachments(this.request);
			this.attachmentService.editAttachments(currentPost, request);
		}

		if (!roleManager.getCanCreatePolls()) {
			pollOptions = new ArrayList<PollOption>();
		}

		if (moderationLog != null) {
			moderationLog.setUser(this.userSession.getUser());
		}

		this.postService.update(post, roleManager.getCanCreateStickyAnnouncementTopics(), pollOptions, attachments, moderationLog);
		//TODO pass zero and true?
		this.result.redirectTo(TopicActions.class).list(post.getTopic().getId(), 0, true);
	}

	/**
	 * Shows the page to edit an existing post
	 * @param postId the id of the post to edit
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	//@InterceptedBy(PostEditInterceptor.class)
	public void edit(int postId) {
		Post post = this.postRepository.get(postId);

		this.result.include("isEdit", true);
		this.result.include("post", post);
		this.result.include("topic", post.getTopic());
		this.result.include("forum", post.getTopic().getForum());
		this.result.include("smilies", this.smilieRepository.getAllSmilies());

		//this.viewService.renderView(Domain.TOPICS, Actions.ADD);
		
		//TODO pass what to add method?
		this.result.forwardTo(TopicActions.class).add(0);
		
	}

	private void redirecToListing(Topic topic) {
		Pagination pagination = new Pagination(this.config, 0).forTopic(topic);

		final String url = new StringBuilder(pagination.getTotalPages() > 1
			? this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, pagination.getTotalPages(), topic.getId())
			: this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, topic.getId()))
			.toString();

		this.result.redirectTo(url);
	}
}
