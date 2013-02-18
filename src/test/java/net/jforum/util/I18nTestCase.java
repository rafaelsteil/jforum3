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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class I18nTestCase {
	@Mock private JForumConfig config;
	private I18n i18n;

	@Before
	public void setUp() throws Exception {
		String applicationPath = TestCaseUtils.getApplicationRoot();

		when(config.getApplicationPath()).thenReturn(applicationPath);
		when(config.getValue(ConfigKeys.I18N_DEFAULT_ADMIN)).thenReturn("default");
		when(config.getValue(ConfigKeys.I18N_DEFAULT)).thenReturn("default");
		
		i18n = new I18n(config);
	}

	@Test
	public void loadExpectDefaultLanguageToBeLoaded() throws Exception {
		assertTrue(i18n.isLanguageLoaded("default"));
	}

	@Test
	public void allDefaultKeysShouldBeCorrectlyLoadedAndRetrieved() {
		assertEquals("default value 1", i18n.getMessage("defaultKey1"));
		assertEquals("default value 2", i18n.getMessage("defaultKey2"));
		assertEquals("default value 3", i18n.getMessage("defaultKey3"));
		assertEquals("default value 4", i18n.getMessage("defaultKey4"));
		assertEquals("default value 5", i18n.getMessage("defaultKey5"));
	}

	@Test
	public void loadCheeseLanguageExpectSuccess() throws Exception {
		assertFalse(i18n.isLanguageLoaded("cheese"));
		i18n.load("cheese");
		assertTrue(i18n.isLanguageLoaded("cheese"));
	}

	@Test
	public void retrieveCheeseKeysExpectSuccessAndKey5ShouldBeDefault() {
		assertEquals("default cheese 1", i18n.getMessage("defaultKey1", "cheese"));
		assertEquals("default cheese 2", i18n.getMessage("defaultKey2", "cheese"));
		assertEquals("default cheese 3", i18n.getMessage("defaultKey3", "cheese"));
		assertEquals("default cheese 4", i18n.getMessage("defaultKey4", "cheese"));
		assertEquals("default value 5", i18n.getMessage("defaultKey5", "cheese"));
	}

	@Test
	public void loadOrangeLanguageExpectSuccess() throws Exception {
		assertFalse(i18n.isLanguageLoaded("orange"));
		i18n.load("orange");
		assertTrue(i18n.isLanguageLoaded("orange"));
	}

	@Test
	public void retrieveOrangeKeysExpectSuccessAndTwoDefaultValuesAndOneExtraOrangeKey() {
		assertEquals("default orange 1", i18n.getMessage("defaultKey1", "orange"));
		assertEquals("default orange 2", i18n.getMessage("defaultKey2", "orange"));
		assertEquals("default orange 3", i18n.getMessage("defaultKey3", "orange"));
		assertEquals("default value 4", i18n.getMessage("defaultKey4", "orange"));
		assertEquals("default value 5", i18n.getMessage("defaultKey5", "orange"));
		assertEquals("orange is not cheese", i18n.getMessage("orange", "orange"));
	}

	@Test
	public void orangeIsDefault() {
		assertFalse(i18n.isLanguageLoaded("orange"));
		i18n.changeBoardDefaultLanguage("orange");
		assertTrue(i18n.isLanguageLoaded("default"));
		assertTrue(i18n.isLanguageLoaded("orange"));
		this.retrieveOrangeKeysExpectSuccessAndTwoDefaultValuesAndOneExtraOrangeKey();
	}
}
