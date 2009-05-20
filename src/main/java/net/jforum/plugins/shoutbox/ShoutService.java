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

import java.text.SimpleDateFormat;

import net.jforum.entities.User;
import net.jforum.formatters.PostOptions;
import net.jforum.repository.UserRepository;
import net.jforum.services.MessageFormatService;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bill
 */
public class ShoutService {
	private I18n i18n;
	private JForumConfig config;
	private ShoutRepository repository;
	private MessageFormatService formatService;
	private UserRepository userRepository;
	public final int ANONYMOUS_USER_ID;

	public ShoutService(JForumConfig config, I18n i18n,UserRepository userRepository,
			ShoutRepository repository,MessageFormatService formatService) {
		this.config = config;
		this.i18n = i18n;
		this.repository = repository;
		this.formatService = formatService;
		this.userRepository = userRepository;

		ANONYMOUS_USER_ID = config.getInt(net.jforum.util.ConfigKeys.ANONYMOUS_USER_ID);
	}

	public String formatShoutMessage(Shout shout,String contexPath) {

		PostOptions options = new PostOptions(false /*isHtmlEnabled*/,
				config.getBoolean(ConfigKeys.SHOUTBOX_ALLOW_SMILIES, false) /*isSmiliesEnabled*/,
				config.getBoolean(ConfigKeys.SHOUTBOX_ALLOW_BBCODE, false) /*isBbCodeEnabled*/,
				false /*isSignatureEnabled*/, contexPath);

		return formatService.format(shout.getMessage(), options);
	}

	public String formatShoutDate(Shout shout){
		String userDateFormat = shout.getUser().getDateFormat();

		SimpleDateFormat formater;

		try{
			formater = new SimpleDateFormat(userDateFormat);
		}catch(Exception e){
			String dateformat = config.getString(ConfigKeys.SHOUTBOX_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss");
			formater = new SimpleDateFormat(dateformat);
		}

		return formater.format(shout.getShoutTime());
	}

	public String getShouter(Shout shout){
		String shouter ;

		User user = shout.getUser();
		if(ANONYMOUS_USER_ID != user.getId()){
			shouter = user.getUsername();
			//result.put("shouter_link","");
		}else if(ANONYMOUS_USER_ID == user.getId() && !"".equals(shout.getShouterName())){
			shouter = shout.getShouterName();
			//$shouter_link = -1;
		}else{
			shouter = i18n.getMessage("Guest");
			//$shouter_link = -1;
		}

		return shouter;
	}

	public void delShout(Shout shout){
		repository.remove(shout);
	}


	public String addShout(Shout shout) {
		ShoutBox  shoutBox = shout.getShoutBox();
		User user = shout.getUser();

		if(shoutBox == null) {
			return i18n.getMessage("ShoutBox.cannotBeNull");
		}

		if(shoutBox.isDisabled()) {
			return i18n.getMessage("ShoutBox.isDisabled"); //Shoutbox_disabled
		}


		//Flood Control
		Shout myLastShout = repository.getMyLastShout(shout.getShouterIp());
		if(myLastShout!= null ){
			long timeInterval = shout.getShoutTime().getTime() - myLastShout.getShoutTime().getTime();
			if(timeInterval < config.getInt(ConfigKeys.SHOUTBOX_FLOOD_INTERVAL, 0)) {
				return i18n.getMessage("ShoutBox.flooderError");
			}
		}

		// Some weird conversion of the data inputed
		String shouter;
		if(ANONYMOUS_USER_ID != user.getId()){
			shouter = "";
		}else{
			if(!shoutBox.isAllowAnonymous()) {
				return i18n.getMessage("ShoutBox.noAuth");
			}

			//remove the html tag
			shouter = userRepository.get(ANONYMOUS_USER_ID).getUsername();

			// The name is shortened to 30 letters
			if(shouter.length()>30) {
				shouter = shouter.substring(0, 30);
			}
		}
		shout.setShouterName(shouter);

		//Don't allow the html input
		// we don't want users shouting images so we take them out before
		// parsing the bbcodes
		//TODO:config
		String msg = safeHtml(shout.getMessage());//.replaceAll("\\<.*?>","")//remove theml
		//								 .replaceAll("[img]([^[]*)[/img]", "");//"\\[img\\]([^\[]*)\\[/img\\]"

		// Only if a message have been provides the information is added to the db
		if(StringUtils.isNotEmpty(msg)){
			// The message is cut of after 250 letters
			if(msg.length()>250) {
				msg = msg.substring(0, 250);
			}

			shout.setMessage(msg);

			repository.add(shout);
			return null;
		}

		return i18n.getMessage("ShoutBox.emptyMessage");
	}

	private String safeHtml(String text){
		return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
