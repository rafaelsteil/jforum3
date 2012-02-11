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

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PostFormatters extends ArrayList<Formatter> {
	public PostFormatters(List<Formatter> list) {
		addAll(list);
	}
}
