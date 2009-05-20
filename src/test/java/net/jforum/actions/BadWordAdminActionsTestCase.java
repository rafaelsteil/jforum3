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
import java.util.List;

import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.BadWord;
import net.jforum.repository.BadWordRepository;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class BadWordAdminActionsTestCase extends AdminTestCase {
	public BadWordAdminActionsTestCase() {
		super(BadWordAdminActions.class);
	}

	private Mockery context = TestCaseUtils.newMockery();
	private ViewService viewService = context.mock(ViewService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private BadWordRepository repository = context.mock(BadWordRepository.class);
	private BadWordAdminActions action = new BadWordAdminActions(viewService, propertyBag, repository);

	@Test
	public void deleteUsingNullShouldIgnore() {
		context.checking(new Expectations() {{
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.delete(null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			BadWord w1 = new BadWord();
			BadWord w2 = new BadWord();

			one(repository).get(1); will(returnValue(w1));
			one(repository).get(2); will(returnValue(w2));

			one(repository).remove(w1);
			one(repository).remove(w2);

			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void list() {
		context.checking(new Expectations() {{
			List<BadWord> list = new ArrayList<BadWord>();
			one(repository).getAll(); will(returnValue(list));
			one(propertyBag).put("words", list);
		}});

		action.list();
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		final BadWord word = new BadWord();

		context.checking(new Expectations() {{
			one(repository).add(word);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.addSave(word);
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		final BadWord word = new BadWord();

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(word));
			one(propertyBag).put("word", word);
			one(viewService).renderView(Actions.ADD);
		}});

		action.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		final BadWord word = new BadWord();

		context.checking(new Expectations() {{
			one(repository).update(word);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		action.editSave(word);
		context.assertIsSatisfied();
	}
}
