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
package net.jforum.repository;

import java.util.List;
import java.util.Set;

import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.entities.User;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Bill
 */
@Component
public class AvatarRepository extends HibernateGenericDAO<Avatar> implements Repository<Avatar> {
	public AvatarRepository(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Avatar> getAll() {
		return session.createCriteria(this.persistClass)
			.setCacheable(true)
			.setCacheRegion("avatarDAO")
			.setComment("avatarDAO.getAllAvatar")
			.list();
	}

	/**
	 * Get all the Gallery Avatar
	 */
	public List<Avatar> getGalleryAvatar() {
		return this.getAllAvatars(AvatarType.AVATAR_GALLERY);
	}

	/**
	 * Get all the Upload Avatar
	 */
	public List<Avatar> getUploadedAvatar() {
		return this.getAllAvatars(AvatarType.AVATAR_UPLOAD);
	}

	@SuppressWarnings("unchecked")
	private List<Avatar> getAllAvatars(AvatarType type){
		return session.createCriteria(this.persistClass)
			.add(Restrictions.eq("avatarType", type))
			.setComment("AvatarDAO.getAvatar." + type)
			.list();
	}

	@Override
	public void remove(Avatar avatar) {
		Set<User> users = avatar.getUsers();

		if (users != null) {
			for(User user : users){
				user.setAvatar(null);
				session.save(user);
			}
		}

		super.remove(avatar);
	}
}
