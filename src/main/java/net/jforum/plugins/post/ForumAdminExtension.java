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
package net.jforum.plugins.post;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.Result;

/**
 * @author Bill
 */
@ActionExtension(Domain.FORUMS_ADMIN)
public class ForumAdminExtension {
	private ForumLimitedTimeRepository repository;
	private ForumRepository forumRepository;
	private JForumConfig config;
	private final Result result;
	private final UserSession userSession;

	public ForumAdminExtension(JForumConfig config, ForumRepository forumRepository,
			ForumLimitedTimeRepository repository,
			Result result, UserSession userSession) {
		this.config = config;
		this.forumRepository = forumRepository;
		this.result = result;
		this.repository = repository;
		this.userSession = userSession;
	}

	@Extends(Actions.EDIT)
	public void edit(int forumId) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);

		if(isEnabled){
			Forum forum = forumRepository.get(forumId);
			long time = this.repository.getLimitedTime(forum);
			this.result.include("forumTimeLimitedEnable", true);
			this.result.include("forumLimitedTime", time);
		}
	}

	@Extends(Actions.EDITSAVE)
	public void editSave(Forum forum, long forumLimitedTime) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.userSession.getRoleManager();

 			if (roleManager.isAdministrator() || roleManager.isCategoryAllowed(forum.getCategory().getId())) {
				ForumLimitedTime current = this.repository.getForumLimitedTime(forum);
				if(current == null){//maybe time limited function enabled after forum created
					current = new ForumLimitedTime();
					current.setForum(forum);
				}
				current.setLimitedTime(forumLimitedTime);
				this.repository.saveOrUpdate(current);
			}
		}
	}

	@Extends(Actions.ADD)
	public void add() {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			this.result.include("fourmTimeLimitedEnable", true);
			this.result.include("fourmLimitedTime", 0);
		}
	}

	@Extends(Actions.ADDSAVE)
	public void addSave(long fourmLimitedTime) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.userSession.getRoleManager();

			Forum forum = (Forum) this.result.included().get("forum");

			if (forum != null && (roleManager.isAdministrator() || roleManager.isCategoryAllowed(forum.getCategory().getId()))) {
				if(fourmLimitedTime > 0){
					ForumLimitedTime current = new ForumLimitedTime();
					current.setForum(forum);
					current.setLimitedTime(fourmLimitedTime);
					this.repository.add(current);
				}
			}
		}
	}

	@Extends("delete")
	public void delete(int... forumsId) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.userSession.getRoleManager();

			if (roleManager.isAdministrator()) {
				for(int forumId : forumsId){
					Forum forum = new Forum();
					forum.setId(forumId);
					ForumLimitedTime fourmLimitedTime =this.repository.getForumLimitedTime(forum);

					if(fourmLimitedTime!=null) {
						this.repository.remove(fourmLimitedTime);
					}
				}
			}
		}
	}
}
