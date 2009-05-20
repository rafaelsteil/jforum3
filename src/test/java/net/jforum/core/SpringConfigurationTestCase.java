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
package net.jforum.core;

import java.util.HashMap;
import java.util.Map;

import net.jforum.util.TestCaseUtils;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;

/**
 * @author Rafael Steil
 */
public class SpringConfigurationTestCase extends XMLTestCase {
	public void testEventsShouldHaveAspectJAutoProxy() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring/events.xml");
		Assert.assertTrue(contents.indexOf("<aop-aspectj-autoproxy />") > -1);
	}

	public void testTopicEvents() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring/events.xml");
		Map<String, String> m = new HashMap<String, String>();
		m.put("aop", "aop");

		NamespaceContext context = new SimpleNamespaceContext(m);
		XMLUnit.setXpathNamespaceContext(context);

		assertXpathExists("/beans/bean[@id='net.jforum.events.listeners.TopicEventListener'][@class='net.jforum.events.listeners.TopicEventListener']", contents);
	}

	public void testbbConfigFormatter() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring/bbcode-formatters.xml");

		assertXpathExists("/beans/bean[@id='net.jforum.formatters.BBConfigFormatter'][@class='net.jforum.formatters.BBConfigFormatter']", contents);
		assertXpathExists("/beans/bean[@class='net.jforum.formatters.BBCodeConfigParser']", contents);
		assertXpathExists("/beans/bean[@class='net.jforum.formatters.BBCodeConfigParser']/constructor-arg[@value='classpath:/jforumConfig/bb_config.xml']", contents);
		assertXpathExists("/beans/bean[@class='net.jforum.formatters.BBCodeConfigParser']/constructor-arg[@ref='net.jforum.formatters.BBConfigFormatter']", contents);
	}

	public void testPostFormatters() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring/bbcode-formatters.xml");

		String postFormattersPath = "/beans/bean[@id='net.jforum.formatters.PostFormatters'][@class='net.jforum.formatters.PostFormatters']";
		String formattersPath = postFormattersPath + "/property[@name='formatters']";
		assertXpathExists(postFormattersPath, contents);
		assertXpathExists(formattersPath, contents);
		assertXpathExists(formattersPath + "/list/bean[@class='net.jforum.formatters.HtmlEntitiesFormatter']", contents);
		assertXpathExists(formattersPath + "/list/bean[@class='net.jforum.formatters.NewLineToHtmlBreakFormatter']", contents);
		assertXpathExists(formattersPath + "/list/bean[@class='net.jforum.formatters.SmiliesFormatter'][@autowire='constructor']", contents);
		assertXpathExists(formattersPath + "/list/ref[@bean='net.jforum.formatters.BBConfigFormatter']", contents);

	}

	public void testCustomScope() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring-config.xml");

		String customScopePath = "/beans/bean[@class='org.springframework.beans.factory.config.CustomScopeConfigurer']";
		assertXpathExists(customScopePath, contents);
		assertXpathExists(customScopePath + "/property[@name='scopes']/map/entry[@key='request'][@value='org.springframework.web.context.request.RequestScope']", contents);
		assertXpathExists(customScopePath + "/property[@name='scopes']/map/entry[@key='session'][@value='org.springframework.web.context.request.SessionScope']", contents);
	}

	public void testImports() throws Exception {
		String contents = TestCaseUtils.loadSpringFile("spring-config.xml");

		assertXpathExists("/beans/import[@resource='spring/repositories.xml']", contents);
		assertXpathExists("/beans/import[@resource='spring/services.xml']", contents);
		assertXpathExists("/beans/import[@resource='spring/misc.xml']", contents);
		assertXpathExists("/beans/import[@resource='spring/bbcode-formatters.xml']", contents);
		assertXpathExists("/beans/import[@resource='spring/events.xml']", contents);
		assertXpathExists("/beans/import[@resource='spring/hibernate.xml']", contents);
	}


}
