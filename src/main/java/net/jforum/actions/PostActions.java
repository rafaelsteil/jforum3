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

import net.jforum.actions.helpers.ActionUtils;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.AttachedFile;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.actions.interceptors.ExtensibleInterceptor;
import net.jforum.actions.interceptors.MethodSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.PollOption;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.plugins.post.PostEditInterceptor;
import net.jforum.repository.PostRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.security.ChangePostRule;
import net.jforum.security.RoleManager;
import net.jforum.services.AttachmentService;
import net.jforum.services.PostService;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.interceptor.MultipartRequestInterceptor;
import org.vraptor.plugin.interceptor.MethodInterceptorInterceptor;

/**
 * @author Rafael Steil
 */
@Component(Domain.POSTS)
@InterceptedBy( {MultipartRequestInterceptor.class, MethodSecurityInterceptor.class, MethodInterceptorInterceptor.class, ExtensibleInterceptor.class })
public class PostActions {
	private PostRepository postRepository;
	private ViewPropertyBag propertyBag;
	private ViewService viewService;
	private SmilieRepository smilieRepository;
	private PostService postService;
	private JForumConfig config;
	private UserSession userSession;
	private AttachmentService attachmentService;
	private VRaptorServletRequest request;

	public PostActions(PostRepository postRepository, ViewPropertyBag propertyBag, ViewService viewService,
		SmilieRepository smilieRepository, PostService postService, JForumConfig config, UserSession userSession,
		AttachmentService attachmentService, VRaptorServletRequest request) {
		this.postRepository = postRepository;
		this.propertyBag = propertyBag;
		this.viewService = viewService;
		this.smilieRepository = smilieRepository;
		this.postService = postService;
		this.config = config;
		this.userSession = userSession;
		this.attachmentService = attachmentService;
		this.request = request;
	}

	/**
	 * Deletes an existing post
	 * @param postId
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	public void delete(@Parameter(key = "postId") int postId) {
		Post post = postRepository.get(postId);
		Topic topic = post.getTopic();
		postService.delete(post);

		if (topic.getTotalPosts() > 0) {
			this.redirecToListing(topic);
		}
		else {
			viewService.redirectToAction(Domain.FORUMS, Actions.SHOW, topic.getForum().getId());
		}
	}

	/**
	 * Saves an existing message
	 * @param post the message to save
	 * @param options the formatting options
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	public void editSave(@Parameter(key = "post") Post post, @Parameter(key = "postOptions") PostFormOptions options,
			@Parameter(key = "pollOptions", create = true) List<PollOption> pollOptions) {
		ActionUtils.definePostOptions(post, options);
		post.getTopic().setType(options.getTopicType());

		Post currentPost = postRepository.get(post.getId());
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		RoleManager roleManager = userSession.getRoleManager();

		if (roleManager.isAttachmentsAlllowed(currentPost.getForum().getId())) {
			attachments = attachmentService.processNewAttachments(request);
			attachmentService.editAttachments(currentPost, request);
		}

		if (!roleManager.getCanCreatePolls()) {
			pollOptions = new ArrayList<PollOption>();
		}

		postService.update(post, roleManager.getCanCreateStickyAnnouncementTopics(), pollOptions, attachments);
		viewService.redirectToAction(Domain.TOPICS, Actions.LIST, post.getTopic().getId());
	}

	/**
	 * Shows the page to edit an existing post
	 * @param postId the id of the post to edit
	 */
	@SecurityConstraint(value = ChangePostRule.class)
	@InterceptedBy(PostEditInterceptor.class)
	public void edit(@Parameter(key = "postId") int postId) {
		Post post = postRepository.get(postId);

		propertyBag.put("isEdit", true);
		propertyBag.put("post", post);
		propertyBag.put("topic", post.getTopic());
		propertyBag.put("forum", post.getTopic().getForum());
		propertyBag.put("smilies", smilieRepository.getAllSmilies());

		viewService.renderView(Domain.TOPICS, Actions.ADD);
	}

	private void redirecToListing(Topic topic) {
		Pagination pagination = new Pagination(config, 0).forTopic(topic);

		String url = new StringBuilder(pagination.getTotalPages() > 1
			? viewService.buildUrl(Domain.TOPICS, Actions.LIST, pagination.getTotalPages(), topic.getId())
			: viewService.buildUrl(Domain.TOPICS, Actions.LIST, topic.getId()))
			.toString();

		viewService.redirect(url);
	}
}
