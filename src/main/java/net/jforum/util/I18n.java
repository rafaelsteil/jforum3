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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.jforum.core.exceptions.ForumException;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 * @author James Yong
 */
@Component
public class I18n {
	private Map<String, Properties> messages = new HashMap<String, Properties>();
	private Properties localeNames = new Properties();
	private String defaultLocaleName;
	private JForumConfig config;

	public I18n(JForumConfig config) {
		this.config = config;
		this.loadConfiguration();
	}

	/**
	 * Changes the deafult language for the entire board
	 * @param newDefaultLanguage
	 */
	public void changeBoardDefaultLanguage(String newDefaultLanguage) {
		if (!this.isLanguageLoaded(newDefaultLanguage)) {
			this.loadLanguage(newDefaultLanguage, this.config.getValue(ConfigKeys.I18N_DEFAULT_ADMIN));
		}

		this.defaultLocaleName = newDefaultLanguage;
	}

	/**
	 * Loads a new Language. If <code>language</code> is either null or empty, or if it is already loaded, the
	 * method will return without executing any code.
	 * The language file will be merged with the default board language
	 * @param language The language to load
	 */
	public void load(String language) {
		this.loadLanguage(language, this.defaultLocaleName);
	}

	/**
	 * Gets a I18N message.
	 * @param key The message name to retrieve. Must be a valid entry into the file specified by
	 * <code>i18n.file</code> property.
	 * @param language The locale name to retrieve the messages from
	 * @param args Parameters needed by some messages. The messages with extra parameters are formated according to
	 * {@link java.text.MessageFormat}specification
	 *
	 * @return String With the message
	 */
	public String getFormattedMessage(String key, String language, Object[] args) {
		return MessageFormat.format(this.messages.get(language).getProperty(key), args);
	}

	/**
	 * @see #getMessage(String, String, Object[])
	 * @param key String
	 * @param args Object
	 * @return String
	 */
	public String getFormattedMessage(String key, Object... args) {
		return this.getFormattedMessage(key, this.defaultLocaleName, args);
	}

	public Object[] params(Object... args) {
		return args;
	}

	/**
	 * Gets an I18n message.
	 * @param key The message name to retrieve. Must be a valid entry into the file specified by <code>i18n.file</code> property.
	 * @param language The locale name to load the message from. If it is not loaded yet,
	 * a load operation will be automatically called. In case of failure to find the
	 * requested message in such locale, the default board locale will be used.
	 * @return String With the localized message
	 */
	public String getMessage(String key, String language) {
		Properties p = this.messages.get(language);

		if (p == null) {
			this.load(language);
			p = this.messages.get(language);
		}

		return p.getProperty(key);
	}

	/**
	 * Gets an I18n message from the default board locale.
	 * @param key the message key
	 * @return string with the localized message
	 */
	public String getMessage(String key) {
		return this.getMessage(key, this.defaultLocaleName);
	}

	/**
	 * Check whether the language file is loaded
	 *
	 * @param language String
	 * @return boolean
	 */
	public boolean isLanguageLoaded(String language) {
		return this.messages.containsKey(language);
	}

	/**
	 * Check if the given language exist.
	 *
	 * @param language The language to check
	 * @return <code>true</code> if the language is a valid and registered translation.
	 */
	public boolean languageExists(String language) {
		return (this.localeNames.getProperty(language) != null);
	}

	/**
	 * Start I18n, loading the locales list and default language boards
	 */
	private void loadConfiguration() {
		this.loadLocales();

		this.defaultLocaleName = this.config.getValue(ConfigKeys.I18N_DEFAULT_ADMIN);
		this.loadLanguage(defaultLocaleName, null);

		String custom = this.config.getValue(ConfigKeys.I18N_DEFAULT);

		if (!custom.equals(defaultLocaleName)) {
			this.loadLanguage(custom, defaultLocaleName);
			this.defaultLocaleName = custom;
		}
	}

	/**
	 * Load all configured locale names
	 */
	private void loadLocales() {
		try {
			this.localeNames.load(this.getClass().getResourceAsStream("/jforumConfig/languages/locales.properties"));
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
	}

	/**
	 * Load a language file
	 * @param language the language name to load
	 * @param mergeWith if not null, merge the language file with the language
	 * specified in this parameter
	 */
	private void loadLanguage(String language, String mergeWith) {
		Properties p = new Properties();

		if (mergeWith != null) {
			if (!this.isLanguageLoaded(mergeWith)) {
				this.loadLanguage(mergeWith, null);
			}

			p.putAll(this.messages.get(mergeWith));
		}

		try {
			p.load(this.getClass().getResourceAsStream("/jforumConfig/languages/" + this.localeNames.getProperty(language)));
		}
		catch (IOException e) {
			throw new ForumException(e);
		}

		this.messages.put(language, p);
	}
}
