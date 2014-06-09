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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import net.jforum.actions.helpers.Actions;
import net.jforum.entities.Avatar;
import net.jforum.repository.AvatarRepository;
import net.jforum.services.AvatarService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class AvatarAdminControllerTestCase extends AdminTestCase {
	
	@Mock private AvatarRepository repository;
	@Mock private AvatarService service;
	@Spy private MockResult mockResult;
	@Mock private AvatarAdminController mockAdminController;
	@InjectMocks private AvatarAdminController avatarAction;

	public AvatarAdminControllerTestCase() {
		super(SmilieAdminController.class);
	}

	@Test
	public void edit() {
		when(repository.get(1)).thenReturn(new Avatar());
			
		avatarAction.edit(1);

		assertEquals(new Avatar(), mockResult.included("avatar"));
		verify(mockResult).forwardTo(Actions.ADD);
	}

	@Test
	public void editSave() {
		when(mockResult.redirectTo(avatarAction)).thenReturn(mockAdminController);

		avatarAction.editSave(new Avatar(), null);

		verify(service).update(notNull(Avatar.class), isNull(UploadedFile.class));
		verify(mockAdminController).list();
	}

	@Test
	public void delete() {
		final Avatar avatar = new Avatar();
		
		when(repository.get(1)).thenReturn(avatar);
		when(repository.get(2)).thenReturn(avatar);
		when(repository.get(3)).thenReturn(avatar);
		when(mockResult.redirectTo(avatarAction)).thenReturn(mockAdminController);

		avatarAction.delete(1, 2, 3);

		verify(repository, times(3)).remove(avatar);
		verify(mockAdminController).list();
	}

	@Test
	public void listExpectOneRecord() {
		when(repository.getGalleryAvatar()).thenReturn(new ArrayList<Avatar>());
		when(repository.getUploadedAvatar()).thenReturn(new ArrayList<Avatar>());
	
		avatarAction.list();

		assertEquals(new ArrayList<Avatar>(), mockResult.included("GalleryAvatars"));
		assertEquals(new ArrayList<Avatar>(), mockResult.included("UploadedAvatars"));
	}

	@Test
	public void addSave() {
		avatarAction.addSave(new Avatar(), null);
		
		verify(service).add(notNull(Avatar.class), isNull(UploadedFile.class));
		verify(mockResult).redirectTo(Actions.LIST);
	}
}
