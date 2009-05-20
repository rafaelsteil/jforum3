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
package net.jforum.services;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.repository.PrivateMessageRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PrivateMessageServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private PrivateMessageRepository repository = context.mock(PrivateMessageRepository.class);
	private PrivateMessageService service = new PrivateMessageService(repository);
	private States state = context.states("state");
	private PrivateMessage pm = new PrivateMessage();

	@Test
	public void deleteIsSenderTypeSentShouldAccept() {
		state.become("delete");
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.SENT);

		context.checking(new Expectations() {{
			one(repository).remove(pm);
		}});

		service.delete(this.newUser(2), 1);
	}

	@Test
	public void deleteIsRecipientTypeNotSentShouldAccept() {
		state.become("delete");
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.READ);

		context.checking(new Expectations() {{
			one(repository).remove(pm);
		}});

		service.delete(this.newUser(3), 1);
	}

	@Test
	public void deleteIsSenderTypeNotSentShouldIgnore() {
		state.become("delete");
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.NEW);
		service.delete(this.newUser(2), 1);
	}

	@Test
	public void deleteIsRecipientTypeSentShouldIgnore() {
		state.become("delete");
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.SENT);
		service.delete(this.newUser(3), 1);
	}

	@Test
	public void deleteNotRecipientNotSenderShouldIgnore() {
		state.become("delete");
		pm.setToUser(this.newUser(2)); pm.setFromUser(this.newUser(3));
		service.delete(this.newUser(1), 1);
	}

	@Test
	public void deleteNullIdsShouldIgnore() {
		service.delete(null, null);
	}

	@Test
	public void sendWithNullDateShouldForceAValue() {
		context.checking(new Expectations() {{
			ignoring(repository);
		}});

		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("subject");
		pm.setDate(null);

		service.send(pm);
		context.assertIsSatisfied();

		Assert.assertNotNull(pm.getDate());
	}

	@Test
	public void sendExpectSuccess() {
		context.checking(new Expectations() {{
			one(repository).add(with(aNonNull(PrivateMessage.class)));
		}});

		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("subject");

		service.send(pm);
		context.assertIsSatisfied();
	}

	@Test(expected = ValidationException.class)
	public void textNullExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText(null);
		pm.setSubject("subject");

		service.send(pm);
	}

	@Test(expected = ValidationException.class)
	public void textEmptyExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("");
		pm.setSubject("subject");

		service.send(pm);
	}

	@Test(expected = ValidationException.class)
	public void subjectEmptyExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("");

		service.send(pm);
	}

	@Test(expected = ValidationException.class)
	public void subjectNullExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject(null);

		service.send(pm);
	}

	@Test(expected = ValidationException.class)
	public void toUserNullExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(null);
		pm.setText("text");
		pm.setSubject("subject");

		service.send(pm);
	}

	@Test(expected = ValidationException.class)
	public void fromUserNullExpectsException() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(null);
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("subject");

		service.send(pm);
	}

	@Before
	public void setup() {
		context.checking(new Expectations() {{
			allowing(repository).get(1); will(returnValue(pm)); when(state.is("delete"));
		}});
	}

	private User newUser(int id) {
		User user = new User(); user.setId(id);
		return user;
	}
}
