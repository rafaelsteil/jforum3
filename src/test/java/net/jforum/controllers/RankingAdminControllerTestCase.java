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

import java.util.Arrays;

import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.services.RankingService;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil
 */
public class RankingAdminControllerTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RankingAdminController controller;
	private RankingRepository repository = context.mock(RankingRepository.class);
	private RankingService service = context.mock(RankingService.class);
	private RankingAdminController mockController = context.mock(RankingAdminController.class);
	private Result mockResult = context.mock(MockResult.class);

	public RankingAdminControllerTestCase() {
		super(RankingAdminController.class);
	}

	@Test
	public void add() {
		context.checking(new Expectations() {
			{
			}
		});
		context.assertIsSatisfied();
	}

	@Test
	public void addSave() {
		context.checking(new Expectations() {
			{
				one(service).add(with(aNonNull(Ranking.class)));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockController));
				one(mockController).list();
			}
		});

		controller.addSave(new Ranking());
		context.assertIsSatisfied();
	}

	@Test
	public void edit() {
		context.checking(new Expectations() {
			{
				Ranking ranking = new Ranking();
				one(repository).get(1); will(returnValue(ranking));
				one(mockResult).include("ranking", ranking);
				one(mockResult).forwardTo(controller); will(returnValue(mockController));
				one(mockController).add();
			}
		});

		controller.edit(1);
		context.assertIsSatisfied();
	}

	@Test
	public void editSave() {
		context.checking(new Expectations() {
			{
				one(service).update(with(aNonNull(Ranking.class)));
				one(mockResult).redirectTo(controller);
				will(returnValue(mockController));
				one(mockController).list();
			}
		});

		controller.editSave(new Ranking());
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {
			{
				one(service).delete(1, 2, 3, 4);
				one(mockResult).redirectTo(controller);
				will(returnValue(mockController));
				one(mockController).list();
			}
		});

		controller.delete(1, 2, 3, 4);
		context.assertIsSatisfied();
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {
			{
				one(repository).getAllRankings();
				will(returnValue(Arrays.asList(new Ranking())));
				one(mockResult).include("rankings",
						Arrays.asList(new Ranking()));
			}
		});

		controller.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		controller = new RankingAdminController(repository, service, mockResult);
	}
}
