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
package net.jforum.controllers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.util.I18n;
import net.jforum.util.URLBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageControllerTestCase {

	@Mock private I18n i18n;
	@Mock private MessageController mockMessageController;
	@Spy private MockResult mockResult;
	@InjectMocks private MessageController controller;

	@Test
	public void replyWaitingModeration() {
		when(i18n.getFormattedMessage("PostShow.waitingModeration", URLBuilder.build(Domain.TOPICS, Actions.LIST, 1))).thenReturn("msg moderation 1");
		
		
		controller.replyWaitingModeration(1);
		
		assertEquals("msg moderation 1", mockResult.included("message"));
		verify(mockMessageController).message();;
	}

	@Test
	public void topicWaitingModeration() {
		when(i18n.getFormattedMessage("PostShow.waitingModeration", URLBuilder.build(Domain.FORUMS, Actions.SHOW, 1))).thenReturn("msg moderation 1");

		controller.topicWaitingModeration(1);
		
		assertEquals("msg moderation 1", mockResult.included("message"));
		verify(mockMessageController).message();;
	}

	@Test
	public void accessDenied() {
		when(i18n.getMessage("Message.accessDenied")).thenReturn("msg denied");
		
		controller.accessDenied();

		assertEquals("msg denied", mockResult.included("message"));
		verify(mockMessageController).message();;
	}
	
	@Before
	public void setup() {
		when(mockResult.of(controller)).thenReturn(mockMessageController);
	}
}
