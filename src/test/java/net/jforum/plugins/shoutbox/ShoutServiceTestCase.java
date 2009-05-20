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
package net.jforum.plugins.shoutbox;

import java.io.IOException;

import net.jforum.entities.User;
import net.jforum.repository.UserRepository;
import net.jforum.services.MessageFormatService;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ShoutServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ShoutRepository repository = context.mock(ShoutRepository.class);
	private UserRepository userRepository = context.mock(UserRepository.class);
	private I18n i18n = context.mock(I18n.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private MessageFormatService formatService = context.mock(MessageFormatService.class);
	private ShoutService service = new ShoutService(config,i18n,userRepository,repository,formatService);

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.addShout(null);
	}

	@Test
	public void addShoutWithNullBox() {
		Shout shout = new Shout();
		shout.setMessage("bulabula");
		String result = service.addShout(shout);
		Assert.assertNotNull(result);
	}

	@Test
	public void addShoutWithDisabledBox() {
		ShoutBox box = new ShoutBox();
		box.setId(1);
		box.setDisabled(true);

		Shout shout = new Shout();
		shout.setShoutBox(box);
		shout.setMessage("bulabula");
		String result = service.addShout(shout);
		Assert.assertNotNull(result);
	}

	@Test
	public void addShoutWithEmptyMssage() {
		ShoutBox box = new ShoutBox();
		box.setId(1);
		box.setDisabled(false);

		Shout shout = new Shout();
		shout.setShoutBox(box);
		shout.setMessage(null);
		String result = service.addShout(shout);
		Assert.assertNotNull(result);
	}

	@Test
	public void addExpectSuccess() throws IOException {
		ShoutBox box = new ShoutBox();
		box.setId(1);
		box.setDisabled(false);

		Shout shout = new Shout();
		shout.setShoutBox(box);
		shout.setMessage("bulabula");

		String result = service.addShout(shout);

		Assert.assertNull(result);
	}

	@Test
	public void getUserNameIfNotAnonymous(){
		Shout shout = new Shout();
		User user = new User();
		user.setId(2);
		user.setUsername("admin");
		shout.setUser(user);

		String shouter = service.getShouter(shout);
		Assert.assertTrue("admin".equals(shouter) );
	}

	@Test
	public void getUserNameIfAnonymousWithName(){
		Shout shout = new Shout();
		User user = new User();
		user.setId(1);
		shout.setUser(user);
		shout.setShouterName("Bill Gates");

		String shouter = service.getShouter(shout);
		Assert.assertTrue("Bill Gates".equals(shouter) );
	}

	@Test
	public void getUserNameIfAnonymousWithNoName(){
		Shout shout = new Shout();
		User user = new User();
		user.setId(1);
		shout.setUser(user);

		String shouter = service.getShouter(shout);
		Assert.assertTrue("Guest".equals(shouter) );
	}
}

