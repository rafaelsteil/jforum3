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
package net.jforum.core.hibernate;

import net.jforum.entities.Attachment;
import net.jforum.repository.AttachmentRepository;

import org.hibernate.SessionFactory;

/**
 * @author Rafael Steil
 */
public class AttachmentDAO extends HibernateGenericDAO<Attachment> implements AttachmentRepository {
	public AttachmentDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
