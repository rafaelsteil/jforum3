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

import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.services.SmilieService;

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
public class SmilieAdminControllerTestCase extends AdminTestCase {
	
	@Mock private SmilieRepository repository;
	@Mock private SmilieService service;
	@Spy private MockResult mockResult;
	@Mock private SmilieAdminController mockSmilieAdminController;
	@InjectMocks private SmilieAdminController controller;

	public SmilieAdminControllerTestCase() {
		super(SmilieAdminController.class);
	}

	@Test
	public void edit() {
		when(repository.get(1)).thenReturn(new Smilie());
		when(mockResult.forwardTo(controller)).thenReturn(mockSmilieAdminController);
			
		controller.edit(1);
		
		assertEquals(new Smilie(), mockResult.included("smilie"));
		verify(mockSmilieAdminController).add();
	}

	@Test
	public void editSave() {
		when(mockResult.redirectTo(controller)).thenReturn(mockSmilieAdminController);
			
		controller.editSave(new Smilie(), null);
		
		verify(service).update(notNull(Smilie.class), isNull(UploadedFile.class));
		verify(mockSmilieAdminController).list();
	}

	@Test
	public void delete() {
		when(mockResult.redirectTo(controller)).thenReturn(mockSmilieAdminController );
			
		controller.delete(1, 2, 3);
		
		verify(service).delete(1, 2, 3);
		verify(mockSmilieAdminController).list();
	}

	@Test
	public void listExpectOneRecord() {
		when(repository.getAllSmilies()).thenReturn(new ArrayList<Smilie>());
			
		controller.list();
		
		assertEquals(new ArrayList<Smilie>(), mockResult.included("smilies"));
	}

	@Test
	public void addSave() {
		when(mockResult.redirectTo(controller)).thenReturn(mockSmilieAdminController);
			
		controller.addSave(new Smilie(), null);
		
		verify(service).add(notNull(Smilie.class), isNull(UploadedFile.class));
		verify(mockSmilieAdminController).list();
	}
}
