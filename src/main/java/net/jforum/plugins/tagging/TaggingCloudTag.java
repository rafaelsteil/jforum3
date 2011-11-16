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
package net.jforum.plugins.tagging;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import net.jforum.core.SessionManager;
import net.jforum.core.tags.ImportFileTag;
import net.jforum.entities.Forum;
import net.jforum.entities.UserSession;
import net.jforum.security.RoleManager;

/**
 * @author Bill
 *
 */
public class TaggingCloudTag extends ImportFileTag {

	public static final String DEFAULT_URL = "/tag/tagCloud.jsp";
	public static final int DEFAULT_TAGCOUNT = 100;
	
	private Forum forum;
	private int tagCount;
	
	private TagService tagService;
	private SessionManager sessionManager;

	public TaggingCloudTag() {
		if (tagService == null) {
			this.tagService = this.getBean(TagService.class);
		}
		if(sessionManager == null ){
			this.sessionManager = this.getBean(SessionManager.class);
		}
	}

	/**
	 * @param tagNumber the tagNumber to set
	 */
	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	/**
	 * @param forum the forum to set
	 */
	public void setForum(Forum forum) {
		this.forum = forum;
	}

	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		if(tagCount<1)
			tagCount = DEFAULT_TAGCOUNT;
		
		UserSession userSession = this.sessionManager.getUserSession();
		RoleManager roleManager = userSession.getRoleManager();
		
		Map<String,Integer> hotTagsWithGroupIndex = null;
		//forum index page
		if(forum == null){
			hotTagsWithGroupIndex = this.tagService.getHotTags(tagCount,7,roleManager);
		}else{//topic list page
			hotTagsWithGroupIndex = this.tagService.getHotTags(forum, tagCount, 7);
		}
		
		if(hotTagsWithGroupIndex.size()>0){
			Map<String,String> tagClass = this.getTagWithClass(hotTagsWithGroupIndex);
			this.request().setAttribute("tags",	tagClass);
			
			if(this.url == null)
				this.setUrl(DEFAULT_URL);
			
			//process the import file.
			super.doTag();
		}
		
	}
	
	private Map<String,String> getTagWithClass(Map<String,Integer> hotTagsWithGroupIndex){
		Map<String,String> tagClass = new LinkedHashMap<String,String>();
		for(Map.Entry<String, Integer> entry : hotTagsWithGroupIndex.entrySet()){
			String tagName = entry.getKey();
			Integer groupIndex = (Integer) entry.getValue();
			String cssClass = this.getClass(groupIndex);
			
			tagClass.put(tagName, cssClass);
		}
		
		return tagClass;
	}
	
	private String getClass(int groupIndex){
		switch(groupIndex){
			case 6:
				return "largest";
			case 5:
				return "verylarge";
			case 4:
				return "large";
			case 3:
				return "medium";
			case 2:
				return "small";
			case 1:
				return "verysmall";
			default:
				return "smallest";
		}
	}
}
