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
package net.jforum.core.events.post;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jforum.entities.BadWord;
import net.jforum.entities.Post;
import net.jforum.events.EmptyPostEvent;
import net.jforum.repository.BadWordRepository;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rafael Steil
 */
public class BadWordEvent extends EmptyPostEvent {
	private BadWordRepository repository;

	public BadWordEvent(BadWordRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see net.jforum.events.EmptyPostEvent#beforeAdd(net.jforum.entities.Post)
	 */
	@Override
	public void beforeAdd(Post post) {
		List<BadWord> words = this.repository.getAll();

		for (BadWord word : words) {
			Pattern pattern = Pattern.compile("\\b" + word.getWord() + "\\b", Pattern.CASE_INSENSITIVE);

			post.setText(this.applyFilter(post.getText(), word.getReplacement(), pattern));

			if (!StringUtils.isEmpty(post.getSubject())) {
				post.setSubject(this.applyFilter(post.getSubject(), word.getReplacement(), pattern));
			}

			if (!StringUtils.isEmpty(post.getTopic().getSubject())) {
				post.getTopic().setSubject(this.applyFilter(post.getTopic().getSubject(), word.getReplacement(), pattern));
			}
		}
	}

	/**
	 * @see net.jforum.events.EmptyPostEvent#updated(net.jforum.entities.Post)
	 */
	@Override
	public void updated(Post post) {
		this.beforeAdd(post);
	}

	private String applyFilter(String text, String replacement, Pattern pattern) {
		Matcher matcher = pattern.matcher(text);
		return matcher.replaceAll(replacement);
	}
}
