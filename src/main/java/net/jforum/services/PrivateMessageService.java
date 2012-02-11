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

import java.util.Date;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.PrivateMessage;
import net.jforum.entities.PrivateMessageType;
import net.jforum.entities.User;
import net.jforum.repository.PrivateMessageRepository;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PrivateMessageService {
	private PrivateMessageRepository repository;

	public PrivateMessageService(PrivateMessageRepository repository) {
		this.repository = repository;
	}

	/**
	 * Delete a set of private messages
	 * @param owner the owner of the messages
	 * @param ids the id of the messages to delete
	 */
	public void delete(User owner, int... ids) {
		if (ids == null || ids.length == 0) {
			return;
		}

		for (int id : ids) {
			PrivateMessage pm = this.repository.get(id);

			if (this.canDeleteMessage(owner, pm)) {
				this.repository.remove(pm);
			}
		}
	}

	private boolean canDeleteMessage(User owner, PrivateMessage pm) {
		return (pm.getToUser().equals(owner) && pm.getType() != PrivateMessageType.SENT)
			|| (pm.getFromUser().equals(owner) && pm.getType() == PrivateMessageType.SENT);
	}

	/**
	 * Send a private message
	 * @param pm the private message to send
	 */
	public void send(PrivateMessage pm) {
		this.applySendConstraints(pm);

		if (pm.getDate() == null) {
			pm.setDate(new Date());
		}

		this.repository.add(pm);
	}

	private void applySendConstraints(PrivateMessage pm) {
		if (pm.getFromUser() == null) {
			throw new ValidationException("The sender was not specified");
		}

		if (pm.getToUser() == null) {
			throw new ValidationException("The recipient was not specified");
		}

		if (StringUtils.isEmpty(pm.getSubject())) {
			throw new ValidationException("The subject was not specified");
		}

		if (StringUtils.isEmpty(pm.getText())) {
			throw new ValidationException("The text was not specified");
		}
	}
}
