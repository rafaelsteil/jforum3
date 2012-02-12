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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Applies formatting for all contents of bb_config.xml.
 * Before using this class, all bb codes you want to be
 * processed should be added through the addBb() method.
 * @author Rafael Steil
 */
@Component
@ApplicationScoped
public class BBConfigFormatter implements Formatter {
	private Map<String, BBCode> bbTags = new LinkedHashMap<String, BBCode>();
	private Map<String, BBCode> alwaysProcessTags = new LinkedHashMap<String, BBCode>();

	public BBConfigFormatter() {
		BBCodeConfigParser parser = new BBCodeConfigParser(this);
		parser.parse();
	}

	public void addBb(BBCode code) {
		if (code.alwaysProcess()) {
			this.alwaysProcessTags.put(code.getTagName(), code);
		}
		else {
			this.bbTags.put(code.getTagName(), code);
		}
	}

	/**
	 * @see net.jforum.formatters.Formatter#format(java.lang.String, net.jforum.formatters.PostOptions)
	 */
	@Override
	public String format(String text, PostOptions postOptions) {
		boolean hasTags = this.hasTags(text);

		if (postOptions.isBbCodeEnabled() && hasTags) {
			text = this.processBB(text, postOptions);
		}

		text = this.formatAlwaysProcessBBCodes(text);

		return text;
	}

	/**
	 * Applies bb code formatting to the text
	 *
	 * @param text the contents to be formatted
	 * @param options post options
	 * @return the formatted text
	 */
	private String processBB(String text, PostOptions options) {
		String startCodeFragment = "[code";
		String endCodeFragment = "[/code]";

		int codeIndex = text.indexOf(startCodeFragment);
		int codeEndIndex = codeIndex > -1 ? text.indexOf(endCodeFragment) : -1;

		if (codeIndex == -1 || codeEndIndex == -1 || codeEndIndex < codeIndex) {
			text = this.processBBExceptCodeTag(text, options);
		}
		else {
			int nextStartPos = 0;
			StringBuilder result = new StringBuilder(text.length());

			// Applies formatting in steps, as all contents inside a [code] tag
			// should be considered as plain text, thus not being elegible for processing
			while (codeIndex > -1 && codeEndIndex > -1 && codeEndIndex > codeIndex) {
				codeEndIndex += endCodeFragment.length();

				// Format only the text between [code] and [/code]
				String codeResult = this.processCodeTag(text.substring(codeIndex, codeEndIndex));

				// Format the text before [code]
				String nonCodeResult = this.processBBExceptCodeTag(text.substring(nextStartPos, codeIndex), options);

				result.append(nonCodeResult).append(codeResult);

				nextStartPos = codeEndIndex;
				codeIndex = text.indexOf(startCodeFragment, codeEndIndex);
				codeEndIndex = codeIndex > -1 ? text.indexOf(endCodeFragment, codeIndex) : -1;
			}

			if (nextStartPos > -1) {
				String nonCodeResult = processBBExceptCodeTag(text.substring(nextStartPos), options);
				result.append(nonCodeResult);
			}

			text = result.toString();
		}

		return text;
	}

	/**
	 * Formats only the [code] tag
	 *
	 * @param text the text to format
	 * @return the formatted text
	 */
	private String processCodeTag(String text) {
		for (BBCode bb : this.bbTags.values()) {
			// There is "code" and "code-highlight"
			if (bb.getTagName().startsWith("code")) {
				Matcher matcher = Pattern.compile(bb.getRegex()).matcher(text);
				StringBuilder sb = new StringBuilder(text);

				while (matcher.find()) {
					String lang = null;
					String contents = null;

					if ("code".equals(bb.getTagName())) {
					    contents = matcher.group(1);
					}
					else {
						lang = matcher.group(1);
						contents = matcher.group(2);
					}

					contents = StringUtils.replace(contents, "<br/> ", "\n");

					// XML-like tags
					contents = StringUtils.replace(contents, "<", "&lt;");
					contents = StringUtils.replace(contents, ">", "&gt;");

					// Note: there is no replacing for spaces and tabs as
					// we are relying on the Javascript SyntaxHighlighter library
					// to do it for us

					StringBuilder replace = new StringBuilder(bb.getReplace());
					int index = replace.indexOf("$1");

					if ("code".equals(bb.getTagName())) {
						if (index > -1) {
							replace.replace(index, index + 2, contents.toString());
						}

						index = sb.indexOf("[code]");
					}
					else {
						if (index > -1) {
							replace.replace(index, index + 2, lang.toString());
						}

						index = replace.indexOf("$2");

						if (index > -1) {
							replace.replace(index, index + 2, contents.toString());
						}

						index = sb.indexOf("[code=");
					}

					int lastIndex = sb.indexOf("[/code]", index) + "[/code]".length();

					if (lastIndex > index) {
						sb.replace(index, lastIndex, replace.toString());
					}
				}

				text = sb.toString();
			}
		}

		return text;
	}

	/**
	 * Process all bb codes, except [code]
	 *
	 * @param text the text to format
	 * @param options post options
	 * @return the formatted text
	 */
	private String processBBExceptCodeTag(String text, PostOptions options) {
		for (BBCode bb : this.bbTags.values()) {
			if (!bb.getTagName().startsWith("code")) {
				text = text.replaceAll(bb.getRegex(), bb.getReplace());
			}
		}

		return text;
	}

	/**
	 * Process the bb tags that always should be applied
	 *
	 * @param text the text to format
	 * @return the formatted text
	 */
	private String formatAlwaysProcessBBCodes(String text) {
		for (BBCode bb : this.alwaysProcessTags.values()) {
			text = text.replaceAll(bb.getRegex(), bb.getReplace());
		}

		return text;
	}

	/**
	 * Checks if there is any bb tag in the text
	 *
	 * @param text the text to search for tags
	 * @return true if there is at least one tag in the text
	 */
	private boolean hasTags(String text) {
		return text.indexOf('[') > -1 && text.indexOf(']') > -1;
	}
}
