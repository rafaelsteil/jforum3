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

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class I18nTestCase {
	private I18n i18n;
	private JForumConfig config;

	@Before
	public void setUp() throws Exception {
		String configFileName = "/jforum-test.properties";
		URL configFileUrl = this.getClass().getResource(configFileName);
		String applicationPath = new File(configFileUrl.getFile()).getParent();

		config = new JForumConfig(null, null);
		config.load(configFileUrl);

		config.setProperty(ConfigKeys.APPLICATION_PATH, applicationPath);
		config.setProperty(ConfigKeys.I18N_DEFAULT_ADMIN, "default");
		config.setProperty(ConfigKeys.I18N_DEFAULT, "default");

		i18n = new I18n(config);
	}

	@Test
	public void loadExpectDefaultLanguageToBeLoaded() throws Exception {
		Assert.assertTrue(i18n.isLanguageLoaded("default"));
	}

	@Test
	public void allDefaultKeysShouldBeCorrectlyLoadedAndRetrieved() {
		Assert.assertEquals("default value 1", i18n.getMessage("defaultKey1"));
		Assert.assertEquals("default value 2", i18n.getMessage("defaultKey2"));
		Assert.assertEquals("default value 3", i18n.getMessage("defaultKey3"));
		Assert.assertEquals("default value 4", i18n.getMessage("defaultKey4"));
		Assert.assertEquals("default value 5", i18n.getMessage("defaultKey5"));
	}

	@Test
	public void loadCheeseLanguageExpectSuccess() throws Exception {
		Assert.assertFalse(i18n.isLanguageLoaded("cheese"));
		i18n.load("cheese");
		Assert.assertTrue(i18n.isLanguageLoaded("cheese"));
	}

	@Test
	public void retrieveCheeseKeysExpectSuccessAndKey5ShouldBeDefault() {
		Assert.assertEquals("default cheese 1", i18n.getMessage("defaultKey1", "cheese"));
		Assert.assertEquals("default cheese 2", i18n.getMessage("defaultKey2", "cheese"));
		Assert.assertEquals("default cheese 3", i18n.getMessage("defaultKey3", "cheese"));
		Assert.assertEquals("default cheese 4", i18n.getMessage("defaultKey4", "cheese"));
		Assert.assertEquals("default value 5", i18n.getMessage("defaultKey5", "cheese"));
	}

	@Test
	public void loadOrangeLanguageExpectSuccess() throws Exception {
		Assert.assertFalse(i18n.isLanguageLoaded("orange"));
		i18n.load("orange");
		Assert.assertTrue(i18n.isLanguageLoaded("orange"));
	}

	@Test
	public void retrieveOrangeKeysExpectSuccessAndTwoDefaultValuesAndOneExtraOrangeKey() {
		Assert.assertEquals("default orange 1", i18n.getMessage("defaultKey1", "orange"));
		Assert.assertEquals("default orange 2", i18n.getMessage("defaultKey2", "orange"));
		Assert.assertEquals("default orange 3", i18n.getMessage("defaultKey3", "orange"));
		Assert.assertEquals("default value 4", i18n.getMessage("defaultKey4", "orange"));
		Assert.assertEquals("default value 5", i18n.getMessage("defaultKey5", "orange"));
		Assert.assertEquals("orange is not cheese", i18n.getMessage("orange", "orange"));
	}

	@Test
	public void orangeIsDefault() {
		Assert.assertFalse(i18n.isLanguageLoaded("orange"));
		i18n.changeBoardDefaultLanguage("orange");
		Assert.assertTrue(i18n.isLanguageLoaded("default"));
		Assert.assertTrue(i18n.isLanguageLoaded("orange"));
		this.retrieveOrangeKeysExpectSuccessAndTwoDefaultValuesAndOneExtraOrangeKey();
	}
}
