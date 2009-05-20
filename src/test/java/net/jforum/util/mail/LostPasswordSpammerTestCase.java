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

import net.jforum.entities.User;
import net.jforum.util.ConfigKeys;

import org.junit.Assert;
import org.junit.Test;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

/**
 * @author Rafael Steil
 */
public class LostPasswordSpammerTestCase extends MailTestCase {
	@Test
	public void send() {
		LostPasswordSpammer spammer = new LostPasswordSpammer(config);

		User user = new User();
		user.setEmail("email@addres.one");
		user.setActivationKey("123");

		spammer.prepare(user, "lost subject");

		SimpleSmtpServer server = null;

		try {
			server = SimpleSmtpServer.start(config.getInt(ConfigKeys.MAIL_SMTP_PORT));
			spammer.dispatchMessages();
		}
		finally {
			if (server != null) {
				server.stop();
			}
		}

		Assert.assertEquals(1, server.getReceivedEmailSize());
		SmtpMessage message = (SmtpMessage)server.getReceivedEmail().next();
		Assert.assertTrue(message.getBody().indexOf("http://localhost/user/recoverPassword/123.page") > -1);
	}
}
