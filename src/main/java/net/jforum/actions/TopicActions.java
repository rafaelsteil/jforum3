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
import java.util.Date;
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
import net.jforum.plugins.post.ForumLimitedTimeRepository;
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
@InterceptedBy( { MethodSecurityInterceptor.class, ExtensibleInterceptor.class })
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
	private final ForumLimitedTimeRepository forumLimitedTimeRepository;

	public TopicActions(ViewPropertyBag propertyBag, JForumConfig config, TopicService topicService,
		ViewService viewService, ForumRepository forumRepository, SmilieRepository smilieRepository,
		PostRepository postRepository, TopicRepository topicRepository, CategoryRepository categoryRepository,
		RankingRepository rankingRepository, SessionManager sessionManager, PollRepository pollRepository,
		ForumLimitedTimeRepository forumLimitedTimeRepository,
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
		this.forumLimitedTimeRepository = forumLimitedTimeRepository;
		this.attachmentService = attachmentService;
		this.request = request;
	}

	public void preList(@Parameter(key = "topicId") int topicId, @Parameter(key = "postId") int postId) {
		int count = this.postRepository.countPreviousPosts(postId);
		int postsPerPage = this.config.getInt(ConfigKeys.POSTS_PER_PAGE);

		if (topicId == 0) {
			Post post = this.postRepository.get(postId);
			topicId = post.getTopic().getId();
		}

		String url = null;

		if (count > postsPerPage) {
			int page = new Pagination().calculeStartFromCount(count, postsPerPage);
			url = this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, page, topicId);
		}
		else {
			url = this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, topicId);
		}

		this.viewService.redirect(url + "#" + postId);
	}

	/**
	 * Shows the page to quote an existing message
	 * @param postId the id of the post to quote
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void quote(@Parameter(key = "postId") int postId) {
		Post post = this.postRepository.get(postId);

		this.propertyBag.put("post", post);
		this.propertyBag.put("isQuote", true);
		this.propertyBag.put("isReply", true);
		this.propertyBag.put("topic", post.getTopic());
		this.propertyBag.put("forum", post.getForum());
		this.propertyBag.put("smilies", this.smilieRepository.getAllSmilies());

		this.viewService.renderView(Actions.ADD);
	}

	public void vote(@Parameter(key = "topicId") int topicId, @Parameter(key = "pollId") int pollId,
		@Parameter(key = "optionId") int optionId) {
		UserSession userSession = this.sessionManager.getUserSession();

		if (userSession.isLogged() && optionId != 0) {
			User user = userSession.getUser();
			Poll poll = this.topicRepository.get(topicId).getPoll();

			if (!this.pollRepository.hasUserVoted(poll, user) && poll.isOpen()) {

				PollVoter voter = new PollVoter();
				voter.setIp(userSession.getIp());
				voter.setPoll(poll);
				voter.setUser(user);

				this.pollRepository.registerVote(voter);

				PollOption option = this.pollRepository.getOption(optionId);
				option.incrementVotes();
			}
		}

		this.viewService.redirectToAction(Actions.LIST, topicId);
	}

	/**
	 * Shows the message review page
	 * @param topicId the id of the topic being replies
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void replyReview(@Parameter(key = "topicId") int topicId) {
		Topic topic = this.topicRepository.get(topicId);

		Pagination pagination = new Pagination(this.config, 0).forTopic(topic);
		int start = pagination.calculeStart(pagination.getTotalPages(), this.config.getInt(ConfigKeys.POSTS_PER_PAGE));

		this.propertyBag.put("topic", topic);
		this.propertyBag.put("posts", topic.getPosts(start, pagination.getRecordsPerPage()));
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

		this.propertyBag.put("post", post);
	}

	/**
	 * Shows the page to reply an existing topic
	 * @param topicId the id of the topic to reply
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void reply(@Parameter(key = "topicId") int topicId) {
		Topic topic = this.topicRepository.get(topicId);

		this.propertyBag.put("isReply", true);
		this.propertyBag.put("post", new Post());
		this.propertyBag.put("topic", topic);
		this.propertyBag.put("forum", topic.getForum());
		this.propertyBag.put("smilies", this.smilieRepository.getAllSmilies());

		this.viewService.renderView(Actions.ADD);
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

		UserSession userSession = this.sessionManager.getUserSession();

		post.setUserIp(userSession.getIp());
		post.setUser(userSession.getUser());

		ActionUtils.definePostOptions(post, options);

		RoleManager roleManager = userSession.getRoleManager();
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		if (roleManager.isAttachmentsAlllowed(topic.getForum().getId())) {
			attachments = this.attachmentService.processNewAttachments(request);
		}

		topic = this.topicRepository.get(topic.getId());

		if (topic.getForum().isModerated() && !roleManager.isModerator()) {
			post.setModerate(true);
		}

		this.topicService.reply(topic, post, attachments);

		if (post.isWaitingModeration()) {
			this.viewService.redirectToAction(Domain.MESSAGES, Actions.REPLY_WAITING_MODERATION, topic.getId());
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
		Topic topic = this.topicRepository.get(topicId);

		if (topic.isWaitingModeration() ) {
			this.viewService.redirectToAction(Domain.MESSAGES, Actions.TOPIC_WAITING_MODERATION,
				topic.getForum().getId());
			return;
		}

		topic.incrementViews();
		UserSession userSession = this.sessionManager.getUserSession();
		userSession.markTopicAsRead(topicId);

		Pagination pagination = new Pagination(this.config, page).forTopic(topic);

		boolean canVoteOnPolls = userSession.isLogged() && userSession.getRoleManager().getCanVoteOnPolls();

		if (canVoteOnPolls && topic.isPollEnabled()) {
			canVoteOnPolls = !this.pollRepository.hasUserVoted(topic.getPoll(), userSession.getUser());
		}

		this.propertyBag.put("canVoteOnPolls", canVoteOnPolls);
		this.propertyBag.put("viewPollResults", viewPollResults);
		this.propertyBag.put("topic", topic);
		this.propertyBag.put("forum", topic.getForum());
		this.propertyBag.put("pagination", pagination);
		this.propertyBag.put("isModeratorOnline", this.sessionManager.isModeratorOnline());
		this.propertyBag.put("rankings", this.rankingRepository.getAllRankings());
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());

		List<Post> posts = topic.getPosts(pagination.getStart(), pagination.getRecordsPerPage());
		if (posts.isEmpty() == false) {
			long limitedTime = this.forumLimitedTimeRepository.getLimitedTime(posts.get(0).getForum());
			if(limitedTime > 0) {
				Date now = new Date();
				for (Post post : posts) {
					post.calculateHasEditTimeExpired(limitedTime, now);
				}
			}
		}
		this.propertyBag.put("posts", posts);
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
		UserSession userSession = this.sessionManager.getUserSession();
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		if (userSession.getRoleManager().isAttachmentsAlllowed(topic.getForum().getId())) {
			attachments = this.attachmentService.processNewAttachments(this.request);
		}

		topic.setType(options.getTopicType());
		topic.setSubject(post.getSubject());
		topic.setUser(userSession.getUser());
		post.setUserIp(userSession.getIp());
		topic.setFirstPost(post);

		Forum forum = this.forumRepository.get(topic.getForum().getId());

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
		this.propertyBag.put("topic", topic);

		if (topic.isWaitingModeration()) {
			this.viewService.redirectToAction(Domain.MESSAGES, Actions.TOPIC_WAITING_MODERATION, topic.getForum().getId());
		}
		else {
			this.redirecToListing(topic, post);
		}
	}

	public void listSmilies() {
		this.propertyBag.put("smilies", this.smilieRepository.getAllSmilies());
	}

	@Viewless
	@SecurityConstraint(value = DownloadAttachmentRule.class)
	public void downloadAttachment(@Parameter(key = "attachmentId") int attachmentId) {
		Attachment attachment = this.attachmentService.getAttachmentForDownload(attachmentId);
		String downloadPath = this.attachmentService.buildDownloadPath(attachment);

		if (!new File(downloadPath).exists()) {
			// TODO show a nice message instead
			throw new ForumException("Attachment not found");
		}

		this.viewService.startDownload(downloadPath, attachment.getRealFilename(), attachment.getFilesize());
	}

	/**
	 * Shows the page to create a new topic
	 * @param forumId the forum where the topic should be created
	 */
	@SecurityConstraint(CreateNewTopicRule.class)
	public void add(@Parameter(key = "forumId") int forumId) {
		Forum forum = this.forumRepository.get(forumId);

		this.propertyBag.put("forum", forum);
		this.propertyBag.put("post", new Post());
		this.propertyBag.put("isNewTopic", true);
		this.propertyBag.put("smilies", this.smilieRepository.getAllSmilies());
	}

	private void redirecToListing(Topic topic, Post post) {
		Pagination pagination = new Pagination(this.config, 0).forTopic(topic);

		StringBuilder url = new StringBuilder(pagination.getTotalPages() > 1
			? this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, pagination.getTotalPages(), topic.getId())
			: this.viewService.buildUrl(Domain.TOPICS, Actions.LIST, topic.getId()));

		url.append('#').append(post.getId());

		this.viewService.redirect(url.toString());
	}
}
