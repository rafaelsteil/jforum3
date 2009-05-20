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
		List<BadWord> words = repository.getAll();

		for (BadWord word : words) {
			Pattern pattern = Pattern.compile("\\b" + word.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(post.getText());
			post.setText(matcher.replaceAll(word.getReplacement()));
		}
	}
}
