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
package net.jforum.core;

import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.view.DefaultPathResolver;

@Component
public class VRaptorCustomPathResolver extends DefaultPathResolver {
	private final JForumConfig config;

	public VRaptorCustomPathResolver(FormatResolver resolver, JForumConfig config) {
		super(resolver);
		this.config = config;
	}

	@Override
	protected String getPrefix() {
		return config.getValue(ConfigKeys.TEMPLATE_DIRECTORY);
	}
}
