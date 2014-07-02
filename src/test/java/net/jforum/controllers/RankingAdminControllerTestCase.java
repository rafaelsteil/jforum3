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

import java.util.Arrays;

import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.services.RankingService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class RankingAdminControllerTestCase extends AdminTestCase {
	
	@InjectMocks private RankingAdminController controller;
	@Mock private RankingRepository repository;
	@Mock private RankingService service;
	@Mock private RankingAdminController mockController;
	@Spy private MockResult mockResult;

	public RankingAdminControllerTestCase() {
		super(RankingAdminController.class);
	}

	@Test
	@Ignore("is that suposed to test something")
	public void add() {
		
	}

	@Test
	public void addSave() {
		when(mockResult.redirectTo(controller)).thenReturn(mockController);
	
		controller.addSave(new Ranking());
		
		verify(service).add(notNull(Ranking.class));
		verify(mockController).list();
	}

	@Test
	public void edit() {
		Ranking ranking = new Ranking();
		when(repository.get(1)).thenReturn(ranking);
		when(mockResult.forwardTo(controller)).thenReturn(mockController);
			
		controller.edit(1);
		
		assertEquals(ranking, mockResult.included("ranking"));
		verify(mockController).add();
	}

	@Test
	public void editSave() {
		when(mockResult.redirectTo(controller)).thenReturn(mockController);
		
		controller.editSave(new Ranking());
		
		verify(service).update(notNull(Ranking.class));
		verify(mockController).list();
	}

	@Test
	public void delete() {
		when(mockResult.redirectTo(controller)).thenReturn(mockController);
			
		controller.delete(1, 2, 3, 4);
		
		verify(service).delete(1, 2, 3, 4);
		verify(mockController).list();
	}

	@Test
	public void listExpectOneRecord() {
		when(repository.getAllRankings()).thenReturn(Arrays.asList(new Ranking()));
	
		controller.list();
		
		assertEquals(Arrays.asList(new Ranking()), mockResult.included("rankings"));
	}
}
