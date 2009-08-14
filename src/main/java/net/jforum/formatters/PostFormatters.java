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

/**
 * @author Rafael Steil
 */
public class PostFormatters extends ArrayList<Formatter> {
	public void setFormatters(List<Formatter> list) {
		this.addAll(list);
	}
}
