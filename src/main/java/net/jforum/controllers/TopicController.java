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
package net.jforum.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jforum.actions.helpers.ActionUtils;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.AttachedFile;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.helpers.PostFormOptions;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.SessionManager;
import net.jforum.core.exceptions.ForumException;
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
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.URLBuilder;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Rafael Steil
 */
@Resource
@Path(Domain.TOPICS)
public class TopicController {
	private ForumRepository forumRepository;
	private PostRepository postRepository;
	private SmilieRepository smilieRepository;
	private TopicService topicService;
	private JForumConfig config;
	private TopicRepository topicRepository;
	private CategoryRepository categoryRepository;
	private RankingRepository rankingRepository;
	private SessionManager sessionManager;
	private PollRepository pollRepository;
	private AttachmentService attachmentService;
	private HttpServletRequest request;
	private final ForumLimitedTimeRepository forumLimitedTimeRepository;
	private final Result result;
	private final UserSession userSession;

	public TopicController(Result result, JForumConfig config,
			TopicService topicService, ForumRepository forumRepository,
			SmilieRepository smilieRepository, PostRepository postRepository,
			TopicRepository topicRepository,
			CategoryRepository categoryRepository,
			RankingRepository rankingRepository, SessionManager sessionManager,
			PollRepository pollRepository,
			ForumLimitedTimeRepository forumLimitedTimeRepository,
			AttachmentService attachmentService, HttpServletRequest request, UserSession userSession) {
		this.result = result;
		this.forumRepository = forumRepository;
		this.smilieRepository = smilieRepository;
		this.topicService = topicService;
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
		this.userSession = userSession;
	}

	public void preList(int topicId, int postId) {
		int count = this.postRepository.countPreviousPosts(postId);
		int postsPerPage = this.config.getInt(ConfigKeys.POSTS_PER_PAGE);

		if (topicId == 0) {
			Post post = this.postRepository.get(postId);
			topicId = post.getTopic().getId();
		}

		String url = null;

		if (count > postsPerPage) {
			int page = new Pagination().calculeStartFromCount(count, postsPerPage);
			url = URLBuilder.build(Domain.TOPICS, Actions.LIST, page, topicId);
		}
		else {
			url = URLBuilder.build(Domain.TOPICS, Actions.LIST, topicId);
		}

		this.result.redirectTo(url + "#" + postId);
	}

	/**
	 * Shows the page to quote an existing message
	 *
	 * @param postId
	 *            the id of the post to quote
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void quote(int postId) {
		Post post = this.postRepository.get(postId);

		this.result.include("post", post);
		this.result.include("isQuote", true);
		this.result.include("isReply", true);
		this.result.include("topic", post.getTopic());
		this.result.include("forum", post.getForum());
		this.result.include("smilies", this.smilieRepository.getAllSmilies());

		this.result.of(this).add(0);
	}

	public void vote(int topicId, int pollId, int optionId) {
		UserSession userSession = this.userSession;

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

		// TODO pass 0 and true?
		this.result.redirectTo(this).list(topicId, 0, true);
	}

	/**
	 * Shows the message review page
	 *
	 * @param topicId
	 *            the id of the topic being replies
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void replyReview(int topicId) {
		Topic topic = this.topicRepository.get(topicId);

		Pagination pagination = new Pagination(this.config, 0).forTopic(topic);
		int start = pagination.calculeStart(pagination.getTotalPages(),
				this.config.getInt(ConfigKeys.POSTS_PER_PAGE));

		this.result.include("topic", topic);
		this.result.include("posts",
				topic.getPosts(start, pagination.getRecordsPerPage()));
	}

	/**
	 * Displays the page to preview a message before posting it
	 *
	 * @param message
	 *            the message to preview
	 * @param options
	 *            the formatting options
	 */
	public void preview(String message, PostFormOptions options) {
		Post post = new Post();

		post.setText(message);
		post.setBbCodeEnabled(options.isBbCodeEnabled());
		post.setHtmlEnabled(options.isHtmlEnabled());
		post.setSmiliesEnabled(options.isSmiliesEnabled());

		this.result.include("post", post);
	}

	/**
	 * Shows the page to create a new topic
	 *
	 * @param forumId
	 *            the forum where the topic should be created
	 */
	@SecurityConstraint(CreateNewTopicRule.class)
	public void add(int forumId) {
		Forum forum = this.forumRepository.get(forumId);

		if (!result.included().containsKey("forum")) {
			this.result.include("forum", forum);
		}

		if (!result.included().containsKey("post")) {
			this.result.include("post", new Post());
		}

		this.result.include("isNewTopic", true);
		this.result.include("smilies", this.smilieRepository.getAllSmilies());
	}

	/**
	 * Shows the page to reply an existing topic
	 *
	 * @param topicId the id of the topic to reply
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void reply(int topicId) {
		Topic topic = this.topicRepository.get(topicId);
		Forum forum = topic.getForum();

		this.result.include("isReply", true);
		this.result.include("post", new Post());
		this.result.include("topic", topic);
		this.result.include("forum", forum);
		this.result.include("smilies", this.smilieRepository.getAllSmilies());

		result.of(this).add(forum.getId());
	}

	/**
	 * Adds a reply to an existing topic.
	 *
	 * @param topic the topic the reply is made
	 * @param post the reply itself
	 * @param options post formatting options
	 */
	@SecurityConstraint(ReplyTopicRule.class)
	public void replySave(Topic topic, Post post, PostFormOptions options) {

		UserSession userSession = this.userSession;

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
			this.result.redirectTo(MessageController.class).replyWaitingModeration(topic.getId());
		}
		else {
			this.redirecToListing(topic, post);
		}
	}

	/**
	 * List all posts from a given topic
	 *
	 * @param topicId the id of the topic to show
	 * @param page the initial page to start showing
	 */
	@SecurityConstraint(value = AccessForumRule.class, displayLogin = true)
	@Path("/list/{topicId}")
	public void list(int topicId, int page, boolean viewPollResults) {
		Topic topic = this.topicRepository.get(topicId);

		if (topic.isWaitingModeration()) {
			this.result.redirectTo(MessageController.class).topicWaitingModeration(topic.getForum().getId());
			return;
		}

		// FIXME resolve cache issues
		// topic.incrementViews();
		UserSession userSession = this.userSession;
		userSession.markTopicAsRead(topicId);

		Pagination pagination = new Pagination(this.config, page).forTopic(topic);

		boolean canVoteOnPolls = userSession.isLogged()
				&& userSession.getRoleManager().getCanVoteOnPolls();

		if (canVoteOnPolls && topic.isPollEnabled()) {
			canVoteOnPolls = !this.pollRepository.hasUserVoted(topic.getPoll(),
					userSession.getUser());
		}

		this.result.include("canVoteOnPolls", canVoteOnPolls);
		this.result.include("viewPollResults", viewPollResults);
		this.result.include("topic", topic);
		this.result.include("forum", topic.getForum());
		this.result.include("pagination", pagination);
		this.result.include("isModeratorOnline",
				this.sessionManager.isModeratorOnline());
		this.result
				.include("rankings", this.rankingRepository.getAllRankings());
		this.result.include("categories",
				this.categoryRepository.getAllCategories());

		List<Post> posts = topic.getPosts(pagination.getStart(),
				pagination.getRecordsPerPage());
		if (posts.isEmpty() == false) {
			long limitedTime = this.forumLimitedTimeRepository
					.getLimitedTime(posts.get(0).getForum());

			if (limitedTime > 0) {
				Date now = new Date();
				for (Post post : posts) {
					post.calculateHasEditTimeExpired(limitedTime, now);
				}
			}
		}
		this.result.include("posts", posts);
	}

	/**
	 * Saves a new topic.
	 *
	 * @param topic the topic to save.
	 * @param post the post itself
	 * @param opti the formatting options
	 */
	@SecurityConstraint(CreateNewTopicRule.class)
	public void addSave(Topic topic, Post post, PostFormOptions postOptions, List<PollOption> pollOptions) {

		ActionUtils.definePostOptions(post, postOptions);
		UserSession userSession = this.userSession;
		List<AttachedFile> attachments = new ArrayList<AttachedFile>();

		if (userSession.getRoleManager().isAttachmentsAlllowed(topic.getForum().getId())) {
			attachments = this.attachmentService.processNewAttachments(this.request);
		}

		topic.setType(postOptions.getTopicType());
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
		this.result.include("topic", topic);

		if (topic.isWaitingModeration()) {
			this.result.redirectTo(MessageController.class).topicWaitingModeration(topic.getForum().getId());
		} else {
			this.redirecToListing(topic, post);
		}
	}

	public void listSmilies() {
		this.result.include("smilies", this.smilieRepository.getAllSmilies());
	}

	@SecurityConstraint(value = DownloadAttachmentRule.class)
	public File downloadAttachment(int attachmentId) {
		Attachment attachment = this.attachmentService.getAttachmentForDownload(attachmentId);
		String downloadPath = this.attachmentService.buildDownloadPath(attachment);

		if (!new File(downloadPath).exists()) {
			// TODO show a nice message instead
			throw new ForumException("Attachment not found");
		}

		return new File(attachment.getRealFilename());
	}

	private void redirecToListing(Topic topic, Post post) {
		Pagination pagination = new Pagination(this.config, 0).forTopic(topic);

		StringBuilder url = new StringBuilder(
			pagination.getTotalPages() > 1
				? URLBuilder.build(Domain.TOPICS, Actions.LIST,pagination.getTotalPages(), topic.getId())
				: URLBuilder.build(Domain.TOPICS, Actions.LIST, topic.getId()));

		url.append('#').append(post.getId());

		this.result.redirectTo(url.toString());
	}
}
