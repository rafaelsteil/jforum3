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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.repository.PrivateMessageRepository;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class PrivateMessageServiceTestCase {

	@Mock private PrivateMessageRepository repository;
	@InjectMocks private PrivateMessageService service;
	private PrivateMessage pm = new PrivateMessage();

	@Test
	public void deleteIsSenderTypeSentShouldAccept() {
		when(repository.get(1)).thenReturn(pm);
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.SENT);

		service.delete(this.newUser(2), 1);

		verify(repository).remove(pm);
	}

	@Test
	public void deleteIsRecipientTypeNotSentShouldAccept() {
		when(repository.get(1)).thenReturn(pm);
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.READ);

		service.delete(this.newUser(3), 1);

		verify(repository).remove(pm);
	}

	@Test
	public void deleteIsSenderTypeNotSentShouldIgnore() {
		when(repository.get(1)).thenReturn(pm);
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.NEW);
		service.delete(this.newUser(2), 1);
	}

	@Test
	public void deleteIsRecipientTypeSentShouldIgnore() {
		when(repository.get(1)).thenReturn(pm);
		pm.setToUser(this.newUser(3)); pm.setFromUser(this.newUser(2)); pm.setType(PrivateMessageType.SENT);
		service.delete(this.newUser(3), 1);
	}

	@Test
	public void deleteNotRecipientNotSenderShouldIgnore() {
		when(repository.get(1)).thenReturn(pm);
		pm.setToUser(this.newUser(2)); pm.setFromUser(this.newUser(3));
		service.delete(this.newUser(1), 1);
	}

	@Test
	public void deleteNullIdsShouldIgnore() {
		service.delete(null, null);
	}

	@Test
	public void sendWithNullDateShouldForceAValue() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("subject");
		pm.setDate(null);

		service.send(pm);

		assertNotNull(pm.getDate());
	}

	@Test
	public void sendExpectSuccess() {
		PrivateMessage pm = new PrivateMessage();
		pm.setFromUser(new User());
		pm.setToUser(new User());
		pm.setText("text");
		pm.setSubject("subject");

		service.send(pm);
		
		verify(repository).add(notNull(PrivateMessage.class));
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

	private User newUser(int id) {
		User user = new User(); user.setId(id);
		return user;
	}
}
