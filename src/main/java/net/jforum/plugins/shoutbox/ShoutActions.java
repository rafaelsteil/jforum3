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
package net.jforum.plugins.shoutbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.jforum.entities.Forum;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;
import net.jforum.services.ViewService;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.Out;
import org.vraptor.annotations.Parameter;
import org.vraptor.annotations.Remotable;

/**
 * @author Bill
 *
 */
@Component("shoutbox")
public class ShoutActions {

	private JForumConfig config;
	private ShoutBoxService shoutBoxService;
	private ShoutService shoutService;
	private ShoutRepository shoutRepository;
	private UserSession userSession;
	private ViewService viewService;
	private I18n i18n;

	@Out
	private List<ShoutBean> shoutBeans = new ArrayList<ShoutBean>();
	@Out
	private List<String> errMsgs = new ArrayList<String>();

	public ShoutActions(JForumConfig config, ViewService viewService,I18n i18n,
			ShoutBoxService shoutBoxService, ShoutRepository shoutRepository,
			ShoutService shoutService, UserSession userSession) {
		this.config = config;
		this.shoutBoxService = shoutBoxService;
		this.shoutRepository = shoutRepository;
		this.shoutService = shoutService;
		this.userSession = userSession;
		this.viewService = viewService;
		this.i18n=i18n;
	}

	@Remotable
	public void shout(@Parameter(key = "shout") Shout shout,@Parameter(key="shoutBoxId")int shoutBoxId){
		clear();
		ShoutBox shoutBox = shoutBoxService.get(shoutBoxId);
		if(shoutBox ==null) {
			return ;
		}

		shout.setUser(userSession.getUser());
		shout.setShouterIp(userSession.getIp());
		shout.setShoutBox(shoutBox);
		shout.setShoutTime(new Date());

		String errMsg = shoutService.addShout(shout);

		if(errMsg != null){
			errMsgs.add(errMsg);
		}
		else{
			ShoutBean bean = new ShoutBean(shout,shoutService,viewService.getContextPath());
			bean.setCanDel(canDel(shout));
			shoutBeans.add(bean);
		}
	}

	@Remotable
	public void read(@Parameter(key = "lastId") int lastId,@Parameter(key="shoutBoxId")int shoutBoxId){
		clear();
		final int displayCount = config.getInt(ConfigKeys.SHOUTBOX_DISPLAY_SHOUTS);

		ShoutBox shoutBox = shoutBoxService.get(shoutBoxId);
		if(shoutBox ==null){
			errMsgs.add(i18n.getMessage("ShoutBox.cannotBeNull"));
			return ;
		}

		List<Shout> shouts =  shoutRepository.getShout(lastId, shoutBox, displayCount);

		for(Shout shout : shouts){
			ShoutBean bean = new ShoutBean(shout,shoutService,viewService.getContextPath());
			bean.setCanDel(canDel(shout));
			this.shoutBeans.add(bean);
		}
	}

	@Remotable
	public void delete(@Parameter(key = "shoutId") int shoutId){
		clear();

		Shout shout = shoutRepository.get(shoutId);

		if(shout != null){
			if(canDel(shout)){
				shoutService.delShout(shout);
			}else{
				errMsgs.add(i18n.getMessage("ShoutBox.cannotDel"));
			}
		}
	}

	private boolean canDel(Shout shout){
		User operator = this.userSession.getUser();

		if (operator!=null){
			User owner = shout.getUser();

			if (operator.equals(owner) && shoutService.ANONYMOUS_USER_ID != operator.getId()) {
				return true;
			}

			RoleManager roleManager = userSession.getRoleManager();
			boolean coAdminOrModeratorOfThisCategory = false;
			if(roleManager.isCoAdministrator() || roleManager.isModerator()) {
				List<Forum> forumsOfACategory = shout.getShoutBox().getCategory().getForums();
				coAdminOrModeratorOfThisCategory = roleManager.isCategoryModerated(forumsOfACategory);
			}

			return roleManager.isAdministrator() || coAdminOrModeratorOfThisCategory && roleManager.isCategoryAllowed(shout.getShoutBox().getCategory().getId());
		}
		return false;
	}

	private void clear(){
		errMsgs.clear();
		shoutBeans.clear();
	}

}
