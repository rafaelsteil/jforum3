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

import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.services.SmilieService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.vraptor.interceptor.UploadedFileInformation;

/**
 * @author Rafael Steil
 */
public class SmilieAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private SmilieRepository repository = context.mock(SmilieRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private ViewService viewService = context.mock(ViewService.class);
	private SmilieService service = context.mock(SmilieService.class);
	private SmilieAdminActions smilieAction = new SmilieAdminActions(service, repository, propertyBag, viewService);

	public SmilieAdminActionsTestCase() {
		super(SmilieAdminActions.class);
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Smilie()));
			one(propertyBag).put("smilie", new Smilie());
			one(viewService).renderView(Actions.ADD);
		}});

		smilieAction.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {{
			one(service).update(with(aNonNull(Smilie.class)), with(aNull(UploadedFileInformation.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		smilieAction.editSave(new Smilie(), null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(service).delete(1, 2, 3);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		smilieAction.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {{
			one(repository).getAllSmilies(); will(returnValue(new ArrayList<Smilie>()));
			one(propertyBag).put("smilies", new ArrayList<Smilie>());
		}});

		smilieAction.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {{
			one(service).add(with(aNonNull(Smilie.class)), with(aNull(UploadedFileInformation.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		smilieAction.addSave(new Smilie(), null);
		context.assertIsSatisfied();
	}
}
