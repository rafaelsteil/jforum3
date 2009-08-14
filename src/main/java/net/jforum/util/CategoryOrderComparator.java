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
package net.jforum.util;

import java.util.Comparator;

import net.jforum.entities.Category;

/**
 * @author Rafael Steil
 * @version $Id: CategoryOrderComparator.java,v 1.1.2.1 2007/02/25 18:55:28 rafaelsteil Exp $
 */
public class CategoryOrderComparator implements Comparator<Category>
{
	/** 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Category c1, Category c2) 
	{
		if (c1.getDisplayOrder() > c2.getDisplayOrder()) {
			return 1;
		}
		else if (c1.getDisplayOrder() < c2.getDisplayOrder() ) {
			return -1;
		}
		else {
			return c1.getName().compareTo(c2.getName());
		}
	}

}
