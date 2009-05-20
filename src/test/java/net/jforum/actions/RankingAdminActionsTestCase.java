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

import java.util.Arrays;

import net.jforum.actions.helpers.Actions;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.services.RankingService;
import net.jforum.services.ViewService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RankingAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RankingAdminActions component;
	private RankingRepository repository = context.mock(RankingRepository.class);
	private ViewService viewService = context.mock(ViewService.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);
	private RankingService service = context.mock(RankingService.class);

	public RankingAdminActionsTestCase() {
		super(RankingAdminActions.class);
	}

	@Test
	public void add() {
		context.checking(new Expectations() {{}});
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {{
			one(service).add(with(aNonNull(Ranking.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.addSave(new Ranking());
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Ranking()));
			one(propertyBag).put("ranking", new Ranking());
			one(viewService).renderView(Actions.ADD);
		}});

		component.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {{
			one(service).update(with(aNonNull(Ranking.class)));
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.editSave(new Ranking());
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(service).delete(1, 2, 3, 4);
			one(viewService).redirectToAction(Actions.LIST);
		}});

		component.delete(1, 2, 3, 4);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {{
			one(repository).getAllRankings(); will(returnValue(Arrays.asList(new Ranking())));
			one(propertyBag).put("rankings", Arrays.asList(new Ranking()));
		}});

		component.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new RankingAdminActions(repository, propertyBag, viewService, service);
	}
}
