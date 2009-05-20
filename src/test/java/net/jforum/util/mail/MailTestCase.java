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
package net.jforum.util.mail;

import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;

/**
 * @author Rafael Steil
 */
public abstract class MailTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	protected JForumConfig config = context.mock(JForumConfig.class);

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(config).getBoolean(ConfigKeys.MAIL_SMTP_SSL); will(returnValue(false));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_LOCALHOST); will(returnValue("localhost"));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_AUTH); will(returnValue("true"));
			allowing(config).getBoolean(ConfigKeys.MAIL_SMTP_AUTH); will(returnValue(true));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_PORT); will(returnValue("25123"));
			allowing(config).getInt(ConfigKeys.MAIL_SMTP_PORT); will(returnValue(25123));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_HOST); will(returnValue("127.0.0.1"));
			allowing(config).getValue(ConfigKeys.MAIL_CHARSET); will(returnValue("ISO-8859-1"));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_USERNAME); will(returnValue("username"));
			allowing(config).getValue(ConfigKeys.MAIL_SMTP_PASSWORD); will(returnValue("password"));
			allowing(config).getValue(ConfigKeys.MAIL_MESSSAGE_FORMAT); will(returnValue("text"));
			allowing(config).getInt(ConfigKeys.MAIL_SMTP_DELAY); will(returnValue(0));
			allowing(config).getValue(ConfigKeys.FORUM_NAME); will(returnValue("forum name"));
			allowing(config).getValue(ConfigKeys.MAIL_SENDER); will(returnValue("sender@example.com"));
			allowing(config).getValue(ConfigKeys.FORUM_LINK); will(returnValue("http://localhost"));
			allowing(config).getInt(ConfigKeys.POSTS_PER_PAGE); will(returnValue(10));
			allowing(config).getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT); will(returnValue("new reply"));
			allowing(config).getValue(ConfigKeys.SERVLET_EXTENSION); will(returnValue(".page"));
			allowing(config).getValue(ConfigKeys.APPLICATION_PATH); will(returnValue(TestCaseUtils.getApplicationRoot()));
			allowing(config).getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE); will(returnValue("/webapp/templates/mail/mailNewReply.txt"));
			allowing(config).getValue(ConfigKeys.MAIL_LOST_PASSWORD_MESSAGE_FILE); will(returnValue("/webapp/templates/mail/lostPassword.txt"));
			allowing(config).getInt(ConfigKeys.MAIL_BATCH_SIZE); will(returnValue(50));
		}});
	}
}
