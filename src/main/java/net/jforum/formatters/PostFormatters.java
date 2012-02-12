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

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class PostFormatters extends ArrayList<Formatter> {
	public PostFormatters(List<Formatter> list) {
		clear();
		organizeOrder(list);
	}

	private void organizeOrder(List<Formatter> list) {
		List<Formatter> annotated = new ArrayList<Formatter>();

		for (Formatter f : list) {
			if (f.getClass().isAnnotationPresent(FormatAfter.class)) {
				annotated.add(f);
			}
		}

		// This haven't been extensively tested
		// Check PostFormattersTestCase in case of problems
		for (Formatter annotatedFormatter : annotated) {
			FormatAfter annotation = annotatedFormatter.getClass().getAnnotation(FormatAfter.class);
			Class<? extends Formatter>[] values = annotation.value();

			for (Formatter f : new ArrayList<Formatter>(list)) {
				for (Class<? extends Formatter> k : values) {
					if (f.getClass().equals(k)) {
						int index = list.indexOf(f);
						list.remove(annotatedFormatter);
						list.add(index + 1, annotatedFormatter);
					}
				}
			}
		}

		addAll(list);
	}
}
