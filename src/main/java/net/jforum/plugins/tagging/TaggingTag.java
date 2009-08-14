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
import java.util.List;

import javax.servlet.jsp.JspException;

import net.jforum.core.tags.JForumTag;
import net.jforum.core.tags.URLTag;
import net.jforum.entities.Topic;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;

/**
 * @author Bill
 *
 */
public class TaggingTag extends JForumTag {

	private Topic topic;
	private TagService tagService;
	private boolean showCount;
	private I18n i18n;

	public TaggingTag() {
		if(tagService == null) {
			this.tagService = this.getBean(TagService.class);
		}

		if(i18n ==null) {
			this.i18n = this.getBean(I18n.class);
		}
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public void setShowCount(boolean showCount) {
		this.showCount = showCount;
	}

	@Override
	public void doTag() throws JspException, IOException {
		if(topic == null) {
			return;
		}

		StringBuilder sb = new StringBuilder(50);
		try{
			List<Tag> tags = this.tagService.getTag(topic);

			if(tags.size()==0) {
				return;
			}

			sb.append("<style type=\"text/css\">@import url(")
			  .append(this.request().getContextPath())
			  .append('/').append(this.config().getValue(ConfigKeys.TEMPLATE_DIRECTORY)).append('/')
			  .append(this.config().getValue(ConfigKeys.TEMPLATE_NAME))
			  .append("/tag/css/tagging.css")
			  .append(");</style><div class=\"tagging-container\"><div class=\"tagging\">")
			  .append("<span class=\"tagging-image\"></span>")
			  .append("<span class=\"tagging-intro\">")
			  .append(i18n.getMessage("Tag.list.intro"))
			  .append("</span><span class=\"tagging-list\">");

			for(Tag tag : tags){
				String tagStr = tag.getName();
				String tagUrl = java.net.URLEncoder.encode(tagStr,URLTag.URL_ENCODE);

				sb.append("<span class=\"tagging-list-item tag tag_front\"><b><a class=\"tagging-link\"  href=\"")
				  .append(this.request().getContextPath())
				  .append("/tag/find/")
				  .append(tagUrl)
				  .append(this.config().getString(ConfigKeys.SERVLET_EXTENSION))
				  .append("\">")
				  .append(tagStr);
				if(showCount){
					int count = this.tagService.count(tagStr);
					sb.append("(")
					  .append(count)
					  .append("");
				}
				sb.append("</a></b></span>");
			}
			sb.append("</span></div></div>");
		}catch(Exception e){
			e.printStackTrace();
			return ;
		}
		pageContext().getOut().print(sb.toString());
	}

}
