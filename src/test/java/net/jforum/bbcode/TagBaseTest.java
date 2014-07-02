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
package net.jforum.bbcode;

import java.util.HashMap;
import java.util.Map;

import net.jforum.formatters.BBCode;
import net.jforum.formatters.BBCodeConfigParser;
import net.jforum.formatters.BBConfigFormatter;
import net.jforum.formatters.PostOptions;

import org.junit.Before;

/**
 * @author Rafael Steil
 */
public abstract class TagBaseTest {
	protected BBConfigFormatter formatter = new BBConfigFormatter();
	protected Map<String, BBCode> bbCodes = new HashMap<String, BBCode>();

	@Before
	public void setup() {
		BBConfigFormatter customFormatter = new BBConfigFormatter() {
			@Override
			public void addBb(BBCode code) {
				bbCodes.put(code.getTagName(), code);
			}
		};

		new BBCodeConfigParser( customFormatter);
	}

	protected PostOptions defaultOptions() {
		return new PostOptions(false, false, true, false, null);
	}
}
