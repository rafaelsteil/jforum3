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
import net.jforum.core.SessionManager;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Forum;
import net.jforum.extensions.ActionExtension;
import net.jforum.extensions.Extends;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Parameter;

/**
 * @author Bill
 */
@ActionExtension(Domain.FORUMS_ADMIN)
public class ForumAdminExtension {

	private ForumLimitedTimeRepository repository;
	private ForumRepository forumRepository;
	private JForumConfig config;
	private ViewPropertyBag propertyBag;
	private SessionManager sessionManager;

	public ForumAdminExtension(JForumConfig config,
			ForumRepository forumRepository, ViewPropertyBag propertyBag,
			ForumLimitedTimeRepository repository, SessionManager sessionManager) {
		this.config = config;
		this.forumRepository = forumRepository;
		this.propertyBag = propertyBag;
		this.repository = repository;
		this.sessionManager = sessionManager;
	}

	@Extends(Actions.EDIT)
	public void edit(@Parameter(key = "forumId") int forumId) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);

		if(isEnabled){
			Forum forum = forumRepository.get(forumId);
			long time = this.repository.getLimitedTime(forum);
			this.propertyBag.put("forumTimeLimitedEnable", true);
			this.propertyBag.put("forumLimitedTime", time);
		}
	}

	@Extends(Actions.EDITSAVE)
	public void editSave(@Parameter(key = "forum") Forum forum, @Parameter(key = "forumLimitedTime", create=true) long forumLimitedTime) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

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
			this.propertyBag.put("fourmTimeLimitedEnable", true);
			this.propertyBag.put("fourmLimitedTime", 0);
		}
	}

	@Extends(Actions.ADDSAVE)
	public void addSave(@Parameter(key = "fourmLimitedTime",create=true) long fourmLimitedTime) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

			Forum forum = (Forum) this.propertyBag.get("forum");
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
	public void delete(@Parameter(key = "forumsId") int... forumsId) {
		boolean isEnabled = this.config.getBoolean(ConfigKeys.FORUM_TIME_LIMITED_ENABLE, false);
		if(isEnabled){
			RoleManager roleManager = this.sessionManager.getUserSession().getRoleManager();

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
