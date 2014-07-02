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

import java.util.ArrayList;
import java.util.List;

import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class PostFormatters extends ArrayList<Formatter> {
	public PostFormatters(JForumConfig config, Container container) throws Exception {

		List<String> formatters = config.getValueAsList(ConfigKeys.MESSAGE_FORMATTERS);

		for (String name : formatters) {
			Class<? extends Formatter> k = (Class<? extends Formatter>)Class.forName(name);
			add(container.instanceFor(k));
		}
	}
}
