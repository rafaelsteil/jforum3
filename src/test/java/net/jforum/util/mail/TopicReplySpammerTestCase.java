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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.TopicRepository;
import net.jforum.util.ConfigKeys;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class TopicReplySpammerTestCase extends MailTestCase {
	
	@Mock private TopicRepository repository;

	@Test
	public void send() {
		when(repository.getTotalPosts(notNull(Topic.class))).thenReturn(10);
		
		TopicReplySpammer spammer = new TopicReplySpammer(config);
		List<User> users = new ArrayList<User>();

		User u1 = new User(); u1.setEmail("email@addres.verify");
		User u2 = new User(); u2.setEmail("email@addres.two");

		users.add(u1); users.add(u2);

		Topic topic = new Topic(repository); topic.setId(1); topic.setSubject("subject x");
		topic.setLastPost(new Post()); topic.getLastPost().setId(123);

		spammer.prepare(topic, users);

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

		Assert.assertEquals(2, server.getReceivedEmailSize());
		SmtpMessage message = (SmtpMessage)server.getReceivedEmail().next();
		Assert.assertTrue(message.getBody().indexOf("subject x") > -1);
		Assert.assertTrue(message.getBody().indexOf("http://localhost/topics/list/1.page#123") > -1);
		Assert.assertTrue(message.getBody().indexOf("http://localhost/topics/unwatch/1.page") > -1);
	}
}
