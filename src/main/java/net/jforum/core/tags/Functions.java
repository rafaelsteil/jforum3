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
package net.jforum.core.tags;

import java.util.List;

import net.jforum.entities.Ranking;
import net.jforum.entities.User;

/**
 * Misc functions
 * @author Rafael Steil
 */
public class Functions {
	/**
	 * Check if a list contains an element
	 * @param list the list with all elements
	 * @param element the element to search for
	 * @return true if the element exist in the list
	 */
	@SuppressWarnings("unchecked")
	public static boolean contains(List<?> list, Object element) {
		return list.contains(element);
	}

	public static String rankingTitle(List<Ranking> rankings, User user) {
		if (user.getTotalPosts() == 0) {
			return "";
		}

		if (user.getRanking() != null && user.getRanking().isSpecial()) {
			return user.getRanking().getTitle();
		}

		Ranking lastRanking = new Ranking();

		for (Ranking ranking : rankings) {
			if (user.getTotalPosts() == ranking.getMin() && !ranking.isSpecial()) {
				return ranking.getTitle();
			}
			else if (user.getTotalPosts() > lastRanking.getMin() && user.getTotalPosts() < ranking.getMin()) {
				return lastRanking.getTitle();
			}

			lastRanking = ranking;
		}

		return lastRanking.getTitle();
	}

	/**
	 * Calcule the last page of something`
	 * @param totalPosts the total of records
	 * @param postsPerPage the number of records per page
	 * @return the max possible page
	 */
	public static int lastPage(int totalPosts, int postsPerPage) {
		return (int)Math.ceil((double)totalPosts / (double)postsPerPage);
	}
}
