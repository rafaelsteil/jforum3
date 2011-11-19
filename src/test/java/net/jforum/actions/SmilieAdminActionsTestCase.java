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

import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.services.SmilieService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class SmilieAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private SmilieRepository repository = context.mock(SmilieRepository.class);
	private SmilieService service = context.mock(SmilieService.class);
	private MockResult mockResult = new MockResult();
	private SmilieAdminActions smilieAction = new SmilieAdminActions(service,
			repository, mockResult);

	public SmilieAdminActionsTestCase() {
		super(SmilieAdminActions.class);
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {
			{
				one(repository).get(1);
				will(returnValue(new Smilie()));
				one(mockResult).include("smilie", new Smilie());
				one(mockResult).forwardTo(SmilieAdminActions.class).add();
			}
		});

		smilieAction.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {
			{
				one(service).update(with(aNonNull(Smilie.class)),
						with(aNull(UploadedFile.class)));
				one(mockResult).redirectTo(SmilieAdminActions.class).list();
			}
		});

		smilieAction.editSave(new Smilie(), null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {
			{
				one(service).delete(1, 2, 3);
				one(mockResult).redirectTo(SmilieAdminActions.class).list();
			}
		});

		smilieAction.delete(1, 2, 3);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {
			{
				one(repository).getAllSmilies();
				will(returnValue(new ArrayList<Smilie>()));
				one(mockResult).include("smilies", new ArrayList<Smilie>());
			}
		});

		smilieAction.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {
			{
				one(service).add(with(aNonNull(Smilie.class)),
						with(aNull(UploadedFile.class)));
				one(mockResult).redirectTo(SmilieAdminActions.class).list();
			}
		});

		smilieAction.addSave(new Smilie(), null);
		context.assertIsSatisfied();
	}
}
