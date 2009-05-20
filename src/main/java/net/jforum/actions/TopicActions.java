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


import java.io.File;
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
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ForumException;
import net.jforum.core.support.vraptor.MultipartRequestInterceptor;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Attachment;
import net.jforum.entities.Forum;
import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.PollVoter;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.PollRepository;
import net.jforum.repository.PostRepository;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.SmilieRepository;
import net.jforum.repository.TopicRepository;
import net.jforum.security.AccessForumRule;
import net.jforum.security.CreateNewTopicRule;
import net.jforum.security.DownloadAttachmentRule;
import net.jforum.security.ReplyTopicRule;
import net.jforum.security.RoleManager;
import net.jforum.services.AttachmentService;
import net.jforum.services.TopicService;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.annotations.Parameter;
import org.vraptor.annotations.Viewless;
import org.vraptor.http.VRaptorServletRequest;

/**
 * @author Rafael Steil
 */
@Component(Domain.TOPICS)
@InterceptedBy( { MultipartRequestInterceptor.class, MethodSecurityInterceptor.class, ExtensibleInterceptor.class })
public class TopicActions {
	private ViewPropertyBag propertyBag;
	private ForumRepository forumRepository;
	private PostRepository postRepository;
	private SmilieRepository smilieRepository;
	private TopicService topicService;
	private ViewService viewService;
	private JForumConfig config;
	private TopicRepository topicRepository;
	private CategoryRepository categoryRepository;
	private RankingRepository rankingRepository;
	private SessionManager sessionManager;
	private PollRepository pollRepository;
	private AttachmentService attachmentService;
	private VRaptorServletRequest request;

	public TopicActions(ViewPropertyBag propertyBag, JForumConfig config, TopicService topicService,
		ViewService viewService, ForumRepository forumRepository, SmilieRepository smilieRepository,
		PostRepository postRepository, TopicRepository topicRepository, CategoryRepository categoryRepository,
		RankingRepository rankingRepository, SessionManager sessionManager, PollRepository pollRepository,
		AttachmentService attachmentService, VRaptorServletRequest request) {
		this.propertyBag = propertyBag;
		this.forumRepository = forumRepository;
		this.smilieRepository = smilieRepository;
		this.topicService = topicService;
		this.viewService = viewService;
		this.postRepository = postRepository;
		this.config = config;
		this.topicRepository = topicRepository;
		this.categoryRepository = categoryRepository;
		this.rankingRepository = rankingRepository;
		this.sessionManager = sessionManager;
		this.pollRepository = pollRepository;
		this.attachmentService = attachmentService;
		this.request = request;
	}

	public void preList(@Parameter(key = "topicId") int topicId, @Parameter(key = "postId") int postId) {
		int count = postRepository.countPreviousPosts(postId);
		int postsPerPage = config.getInt(ConfigKeys.POSTS_PER_PAGE);

		if (topicId == 0) {
			Post post = postRepository.get(postId);
			topicId = post.getTopic().getId();
		}

		String url = null;

		if (count > postsPerPage) {
			int page = new Pagination().calculeStart(count, postsPerPage);
			url = viewService.buildUrl(Domain.TOPICS, Actions.LIST, page, topicId);
		}
		else {
			url = viewService.buildUrl(Domain.TOPICS, Actions.LIST, topicId);
		}

		viewService.redirect(url + "#" + postId);
	}

	/**
	 * Shows the page to quote an existing message
	 * @param postId the id of the post to quote
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void quote(@Parameter(key = "postId") int postId) {
		Post post = postRepository.get(postId);

		propertyBag.put("post", post);
		propertyBag.put("isQuote", true);
		propertyBag.put("isReply", true);
		propertyBag.put("topic", post.getTopic());
		propertyBag.put("forum", post.getForum());
		propertyBag.put("smilies", smilieRepository.getAllSmilies());

		viewService.renderView(Actions.ADD);
	}

	public void vote(@Parameter(key = "topicId") int topicId, @Parameter(key = "pollId") int pollId,
		@Parameter(key = "optionId") int optionId) {
		UserSession userSession = sessionManager.getUserSession();

		if (userSession.isLogged() && optionId != 0) {
			User user = userSession.getUser();
			Poll poll = topicRepository.get(topicId).getPoll();

			if (!pollRepository.hasUserVoted(poll, user) && poll.isOpen()) {

				PollVoter voter = new PollVoter();
				voter.setIp(userSession.getIp());
				voter.setPoll(poll);
				voter.setUser(user);

				pollRepository.registerVote(voter);

				PollOption option = pollRepository.getOption(optionId);
				option.incrementVotes();
			}
		}

		viewService.redirectToAction(Actions.LIST, topicId);
	}

	/**
	 * Shows the message review page
	 * @param topicId the id of the topic being replies
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void replyReview(@Parameter(key = "topicId") int topicId) {
		Topic topic = topicRepository.get(topicId);

		Pagination pagination = new Pagination(config, 0).forTopic(topic);
		int start = pagination.calculeStart(pagination.getTotalPages(), config.getInt(ConfigKeys.POSTS_PER_PAGE));

		propertyBag.put("topic", topic);
		propertyBag.put("posts", topic.getPosts(start, pagination.getRecordsPerPage()));
	}

	/**
	 * Displays the page to preview a message before posting it
	 * @param message the message to preview
	 * @param options the formatting options
	 */
	public void preview(@Parameter(key = "message") String message, @Parameter(key = "options") PostFormOptions options) {
		Post post = new Post();

		post.setText(message);
		post.setBbCodeEnabled(options.isBbCodeEnabled());
		post.setHtmlEnabled(options.isHtmlEnabled());
		post.setSmiliesEnabled(options.isSmiliesEnabled());

		propertyBag.put("post", post);
	}

	/**
	 * Shows the page to reply an existing topic
	 * @param topicId the id of the topic to reply
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void reply(@Parameter(key = "topicId") int topicId) {
		Topic topic = topicRepository.get(topicId);

		propertyBag.put("isReply", true);
		propertyBag.put("post", new Post());
		propertyBag.put("topic", topic);
		propertyBag.put("forum", topic.getForum());
		propertyBag.put("smilies", smilieRepository.getAllSmilies());

		viewService.renderView(Actions.ADD);
	}

	/**
	 * Adds a reply to an existing topic.
	 * @param topic the topic the reply is made
	 * @param post the reply itself
	 * @param options post formatting options
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void replySave(@Parameter(key = "topic") Topic topic, @Parameter(key = "post") Post post,
		@Parameter(key = "postOptions") PostFormOptions options) {

		UserSession userSession = sessionManager.getUserSession();

		post.setUserIp(userSession.getIp());
		post.setUser(userSession.getUser());

		ActionUtils.definePostOptions(post, options);

		RoleManager roleManager = userSession.getRoleManager();
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		if (roleManager.isAttachmentsAlllowed(topic.getForum().getId())) {
			attachments = attachmentService.processNewAttachments(request);
		}

		topic = topicRepository.get(topic.getId());

		if (topic.getForum().isModerated() && !roleManager.isModerator()) {
			post.setModerate(true);
		}

		topicService.reply(topic, post, attachments);

		if (post.isWaitingModeration()) {
			viewService.redirectToAction(Domain.MESSAGES, Actions.REPLY_WAITING_MODERATION, topic.getId());
		}
		else {
			this.redirecToListing(topic, post);
		}
	}

	/**
	 * List all posts from a given topic
	 * @param topicId the id of the topic to show
	 * @param page the initial page to start showing
	 */
	@SecurityConstraint(value = AccessForumRule.class, displayLogin = true)
	public void list(@Parameter(key = "topicId") int topicId, @Parameter(key = "page") int page,
			@Parameter(key = "viewPollResults") boolean viewPollResults) {
		Topic topic = topicRepository.get(topicId);

		if (topic.isWaitingModeration() ) {
			viewService.redirectToAction(Domain.MESSAGES, Actions.TOPIC_WAITING_MODERATION,
				topic.getForum().getId());
			return;
		}

		topic.incrementViews();
		UserSession userSession = sessionManager.getUserSession();
		userSession.markTopicAsRead(topicId);

		Pagination pagination = new Pagination(config, page).forTopic(topic);

		boolean canVoteOnPolls = userSession.isLogged() && userSession.getRoleManager().getCanVoteOnPolls();

		if (canVoteOnPolls && topic.isPollEnabled()) {
			canVoteOnPolls = !pollRepository.hasUserVoted(topic.getPoll(), userSession.getUser());
		}

		propertyBag.put("canVoteOnPolls", canVoteOnPolls);
		propertyBag.put("viewPollResults", viewPollResults);
		propertyBag.put("topic", topic);
		propertyBag.put("forum", topic.getForum());
		propertyBag.put("pagination", pagination);
		propertyBag.put("isModeratorOnline", sessionManager.isModeratorOnline());
		propertyBag.put("rankings", rankingRepository.getAllRankings());
		propertyBag.put("categories", categoryRepository.getAllCategories());
		propertyBag.put("posts", topic.getPosts(pagination.getStart(), pagination.getRecordsPerPage()));
	}

	/**
	 * Saves a new topic.
	 * @param topic the topic to save.
	 * @param post the post itself
	 * @param options the formatting options
	 */
	@SecurityConstraint(CreateNewTopicRule.class)
	public void addSave(@Parameter(key = "topic") Topic topic, @Parameter(key = "post") Post post,
		@Parameter(key = "postOptions") PostFormOptions options,
		@Parameter(key = "pollOptions", create = true) List<PollOption> pollOptions) {

		ActionUtils.definePostOptions(post, options);
		UserSession userSession = sessionManager.getUserSession();
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		if (userSession.getRoleManager().isAttachmentsAlllowed(topic.getForum().getId())) {
			attachments = attachmentService.processNewAttachments(request);
		}

		topic.setType(options.getTopicType());
		topic.setSubject(post.getSubject());
		topic.setUser(userSession.getUser());
		post.setUserIp(userSession.getIp());
		topic.setFirstPost(post);

		Forum forum = forumRepository.get(topic.getForum().getId());

		if (forum.isModerated() && !userSession.getRoleManager().isModerator()) {
			topic.setPendingModeration(true);
		}

		if (!userSession.getRoleManager().getCanCreateStickyAnnouncementTopics()) {
			topic.setType(Topic.TYPE_NORMAL);
		}

		if (!userSession.getRoleManager().getCanCreatePolls()) {
			topic.setPoll(null);
		}

		topicService.addTopic(topic, pollOptions, attachments);
		propertyBag.put("topic", topic);

		if (topic.isWaitingModeration()) {
			viewService.redirectToAction(Domain.MESSAGES, Actions.TOPIC_WAITING_MODERATION, topic.getForum().getId());
		}
		else {
			this.redirecToListing(topic, post);
		}
	}

	public void listSmilies() {
		propertyBag.put("smilies", smilieRepository.getAllSmilies());
	}

	@Viewless
	@SecurityConstraint(value = DownloadAttachmentRule.class)
	public void downloadAttachment(@Parameter(key = "attachmentId") int attachmentId) {
		Attachment attachment = attachmentService.getAttachmentForDownload(attachmentId);
		String downloadPath = attachmentService.buildDownloadPath(attachment);

		if (!new File(downloadPath).exists()) {
			// TODO show a nice message instead
			throw new ForumException("Attachment not found");
		}

		viewService.startDownload(downloadPath, attachment.getRealFilename(), attachment.getFilesize());
	}

	/**
	 * Shows the page to create a new topic
	 * @param forumId the forum where the topic should be created
	 */
	@SecurityConstraint(CreateNewTopicRule.class)
	public void add(@Parameter(key = "forumId") int forumId) {
		Forum forum = forumRepository.get(forumId);

		propertyBag.put("forum", forum);
		propertyBag.put("post", new Post());
		propertyBag.put("isNewTopic", true);
		propertyBag.put("smilies", smilieRepository.getAllSmilies());
	}

	private void redirecToListing(Topic topic, Post post) {
		Pagination pagination = new Pagination(config, 0).forTopic(topic);

		StringBuilder url = new StringBuilder(pagination.getTotalPages() > 1
			? viewService.buildUrl(Domain.TOPICS, Actions.LIST, pagination.getTotalPages(), topic.getId())
			: viewService.buildUrl(Domain.TOPICS, Actions.LIST, topic.getId()));

		url.append('#').append(post.getId());

		viewService.redirect(url.toString());
	}
}
