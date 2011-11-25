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

import java.util.ArrayList;

import net.jforum.actions.helpers.Actions;
import net.jforum.entities.Avatar;
import net.jforum.repository.AvatarRepository;
import net.jforum.services.AvatarService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;

public class AvatarAdminControllerTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private AvatarRepository repository = context.mock(AvatarRepository.class);
	private AvatarService service = context.mock(AvatarService.class);
	private Result mockResult = context.mock(MockResult.class);
	private AvatarAdminController mockAdminController = context
			.mock(AvatarAdminController.class);
	private AvatarAdminController avatarAction = new AvatarAdminController(
			mockResult, repository, service);

	public AvatarAdminControllerTestCase() {
		super(SmilieAdminController.class);
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {
			{
				one(repository).get(1);
				will(returnValue(new Avatar()));
				one(mockResult).include("avatar", new Avatar());
				one(mockResult).forwardTo(Actions.ADD);
			}
		});

		avatarAction.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {
			{
				one(service).update(with(aNonNull(Avatar.class)),
						with(aNull(UploadedFile.class)));
				one(mockResult).redirectTo(avatarAction);
				will(returnValue(mockAdminController));
				one(mockAdminController).list();
			}
		});

		avatarAction.editSave(new Avatar(), null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		final Avatar avatar = new Avatar();

		context.checking(new Expectations() {
			{

				one(repository).get(1);
				will(returnValue(avatar));
				one(repository).remove(avatar);
				one(repository).get(2);
				will(returnValue(avatar));
				one(repository).remove(avatar);
				one(repository).get(3);
				will(returnValue(avatar));
				one(repository).remove(avatar);
				one(mockResult).redirectTo(avatarAction);
				will(returnValue(mockAdminController));
				one(mockAdminController).list();

			}
		});

		avatarAction.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {
			{
				one(repository).getGalleryAvatar();
				will(returnValue(new ArrayList<Avatar>()));
				one(repository).getUploadedAvatar();
				will(returnValue(new ArrayList<Avatar>()));
				one(mockResult).include("GalleryAvatars",
						new ArrayList<Avatar>());
				one(mockResult).include("UploadedAvatars",
						new ArrayList<Avatar>());
			}
		});

		avatarAction.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {
			{
				one(service).add(with(aNonNull(Avatar.class)),
						with(aNull(UploadedFile.class)));
				one(mockResult).redirectTo(Actions.LIST);
			}
		});

		avatarAction.addSave(new Avatar(), null);
		context.assertIsSatisfied();
	}
}
