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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.BadWord;
import net.jforum.repository.BadWordRepository;

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
public class BadWordControllerActionsTestCase extends AdminTestCase {

	
	@Mock private BadWordRepository repository;
	@Spy private MockResult mockResult;
	@Mock private BadWordAdminController mockBadWordAdminController;
	@InjectMocks private BadWordAdminController action;

	public BadWordControllerActionsTestCase() {
		super(BadWordAdminController.class);
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		when(mockResult.redirectTo(action)).thenReturn(mockBadWordAdminController);
	
		action.delete(null);

		verify(mockBadWordAdminController).list();
	}

	@Test
	public void delete() {
		BadWord w1 = new BadWord();
		BadWord w2 = new BadWord();

		when(repository.get(1)).thenReturn(w1);
		when(repository.get(2)).thenReturn(w2);
		when(mockResult.redirectTo(action)).thenReturn(mockBadWordAdminController);
			
		action.delete(1, 2);

		verify(repository).remove(w1);
		verify(repository).remove(w2);
		verify(mockBadWordAdminController).list();
	}

	@Test
	public void list() {
		List<BadWord> list = new ArrayList<BadWord>();
		when(repository.getAll()).thenReturn(list);
			
		action.list();

		assertEquals(list, mockResult.included("words"));
	}

	@Test
	public void addSave() {
		final BadWord word = new BadWord();
		when(mockResult.redirectTo(action)).thenReturn(mockBadWordAdminController);
	
		action.addSave(word);

		verify(repository).add(word);
		verify(mockBadWordAdminController).list();
	}

	@Test
	public void edit() {
		final BadWord word = new BadWord();
		
		when(repository.get(1)).thenReturn(word);
		when(mockResult.forwardTo(action)).thenReturn(mockBadWordAdminController);
			
		action.edit(1);
		
		assertEquals(word, mockResult.included("word"));
		verify(mockBadWordAdminController).add();
	}

	@Test
	public void editSave() {
		final BadWord word = new BadWord();
		when(mockResult.redirectTo(action)).thenReturn(mockBadWordAdminController);
			
		action.editSave(word);
		
		verify(repository).update(word);
		verify(mockBadWordAdminController).list();
	}
}
