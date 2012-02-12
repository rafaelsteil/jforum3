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
package net.jforum.formatters;

import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class SmiliesFormatter implements Formatter {
	private JForumConfig config;
	private Container container;

	public SmiliesFormatter(JForumConfig config, Container container) {
		this.config = config;
		this.container = container;
	}

	/**
	 * @see net.jforum.formatters.Formatter#format(java.lang.String, net.jforum.formatters.PostOptions)
	 */
	@Override
	public String format(String text, PostOptions postOptions) {
		SmilieRepository repository = container.instanceFor(SmilieRepository.class);

		if (postOptions.isSmiliesEnabled()) {
			for (Smilie smilie : repository.getAllSmilies()) {
				text = StringUtils.replace(text, smilie.getCode(),
					this.imageTag(smilie.getDiskName(), postOptions.contextPath()));
			}
		}

		return text;
	}

	private String imageTag(String filename, String contextPath) {
		return new StringBuilder(128)
			.append("<img src='")
			.append(contextPath)
			.append('/')
			.append(this.config.getValue(ConfigKeys.SMILIE_IMAGE_DIR))
			.append('/')
			.append(filename).append("' border='0'/>")
			.toString();
	}
}
