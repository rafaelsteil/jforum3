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

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import net.jforum.core.tags.ImportFileTag;
import net.jforum.entities.Category;
import net.jforum.entities.UserSession;

/**
 * @author Bill
 *
 */
public class ShoutBoxTag extends ImportFileTag {
	
	public static final String DEFAULT_URL = "/shoutbox/shoutbox.jsp";
	
	private Category category;
	private ShoutBoxService shoutboxService;

	public ShoutBoxTag(){
		if (shoutboxService == null) {
			shoutboxService = this.getBean(ShoutBoxService.class);
		}
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		ShoutBox shoutBox = shoutboxService.getShoutBox(category);
		
		if(shoutBox!=null){
			boolean showShoutBox = !shoutBox.isDisabled();
			ServletRequest request = this.request();
			if(showShoutBox && !shoutBox.isAllowAnonymous()){//if the shoutbox is not disabled, still need check the shoutbox allow_anonymous
				UserSession userSession = (UserSession) request.getAttribute(UserSession.class.getName());
				if(!userSession.isLogged())//is not logged in,it will not show the shoutbox
					showShoutBox = false;
			}
			
			if(showShoutBox){ 
				//put the param into context that needed by the imported file
				request.setAttribute("CurrentShoutBox", shoutBox);
				
				if(this.url == null)
					this.setUrl(DEFAULT_URL);
				
				//Process the file import
				super.doTag();
			}
		}
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	
}
