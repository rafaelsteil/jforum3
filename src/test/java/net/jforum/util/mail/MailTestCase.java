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

import static org.mockito.Mockito.*;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.junit.Before;

/**
 * @author Rafael Steil
 */
public abstract class MailTestCase {
	
	protected JForumConfig config = mock(JForumConfig.class);

	@Before
	public void setup() {
		when(config.getBoolean(ConfigKeys.MAIL_SMTP_SSL)).thenReturn(false);
		when(config.getValue(ConfigKeys.MAIL_SMTP_LOCALHOST)).thenReturn("localhost");
		when(config.getValue(ConfigKeys.MAIL_SMTP_AUTH)).thenReturn("true");
		when(config.getBoolean(ConfigKeys.MAIL_SMTP_AUTH)).thenReturn(true);
		when(config.getValue(ConfigKeys.MAIL_SMTP_PORT)).thenReturn("25123");
		when(config.getInt(ConfigKeys.MAIL_SMTP_PORT)).thenReturn(25123);
		when(config.getValue(ConfigKeys.MAIL_SMTP_HOST)).thenReturn("127.0.0.1");
		when(config.getValue(ConfigKeys.MAIL_CHARSET)).thenReturn("ISO-8859-1");
		when(config.getValue(ConfigKeys.MAIL_SMTP_USERNAME)).thenReturn("username");
		when(config.getValue(ConfigKeys.MAIL_SMTP_PASSWORD)).thenReturn("password");
		when(config.getValue(ConfigKeys.MAIL_MESSSAGE_FORMAT)).thenReturn("text");
		when(config.getInt(ConfigKeys.MAIL_SMTP_DELAY)).thenReturn(0);
		when(config.getValue(ConfigKeys.FORUM_NAME)).thenReturn("forum name");
		when(config.getValue(ConfigKeys.MAIL_SENDER)).thenReturn("sender@example.com");
		when(config.getValue(ConfigKeys.FORUM_LINK)).thenReturn("http://localhost");
		when(config.getInt(ConfigKeys.POSTS_PER_PAGE)).thenReturn(10);
		when(config.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT)).thenReturn("new reply");
		when(config.getValue(ConfigKeys.SERVLET_EXTENSION)).thenReturn(".page");
		when(config.getValue(ConfigKeys.APPLICATION_PATH)).thenReturn(TestCaseUtils.getApplicationRoot());
		when(config.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE)).thenReturn("/webapp/templates/mail/mailNewReply.txt");
		when(config.getValue(ConfigKeys.MAIL_LOST_PASSWORD_MESSAGE_FILE)).thenReturn("/webapp/templates/mail/lostPassword.txt");
		when(config.getInt(ConfigKeys.MAIL_BATCH_SIZE)).thenReturn(50);
	}
}
