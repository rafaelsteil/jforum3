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
package net.jforum.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.MultipartRequestInterceptor;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Avatar;
import net.jforum.repository.AvatarRepository;
import net.jforum.services.AvatarService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.vraptor.Interceptor;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.interceptor.UploadedFileInformation;

public class AvatarAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private AvatarRepository repository = context.mock(AvatarRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private AvatarService service = context.mock(AvatarService.class);
	private AvatarAdminActions avatarAction = new AvatarAdminActions(propertyBag,repository,service, viewService);

	public AvatarAdminActionsTestCase() {
		super(SmilieAdminActions.class);
	}

	@Test
	public void shouldBeInterceptedByMultipartRequestInterceptor() throws Exception {
		Assert.assertTrue(avatarAction.getClass().isAnnotationPresent(InterceptedBy.class));
		InterceptedBy annotation = avatarAction.getClass().getAnnotation(InterceptedBy.class);
		List<Class<? extends Interceptor>> interceptors = Arrays.asList(annotation.value());
		Assert.assertTrue(interceptors.contains(MultipartRequestInterceptor.class));
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Avatar()));
			one(propertyBag).put("avatar", new Avatar());
			one(viewService).renderView(Actions.ADD);
		}});

		avatarAction.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {{
			one(service).update(with(aNonNull(Avatar.class)), with(aNull(UploadedFileInformation.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		avatarAction.editSave(new Avatar(), null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
        final Avatar avatar = new Avatar();

        context.checking(new Expectations() {{

            one(repository).get(1); will(returnValue(avatar));
			one(repository).remove(avatar);
            one(repository).get(2); will(returnValue(avatar));
			one(repository).remove(avatar);
            one(repository).get(3); will(returnValue(avatar));
			one(repository).remove(avatar);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		avatarAction.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {{
			one(repository).getGalleryAvatar(); will(returnValue(new ArrayList<Avatar>()));
            one(repository).getUploadedAvatar(); will(returnValue(new ArrayList<Avatar>()));
			one(propertyBag).put("GalleryAvatars", new ArrayList<Avatar>());
			one(propertyBag).put("UploadedAvatars", new ArrayList<Avatar>());
		}});

		avatarAction.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {{
			one(service).add(with(aNonNull(Avatar.class)), with(aNull(UploadedFileInformation.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		avatarAction.addSave(new Avatar(), null);
		context.assertIsSatisfied();
	}
}
