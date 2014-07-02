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

import net.jforum.entities.Banlist;
import net.jforum.repository.BanlistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.util.test.MockResult;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class BanlistAdminControllerTestCase extends AdminTestCase {
	
	private BanlistAdminController compverifynt;
	@Mock private BanlistRepository repository;
	@Spy private MockResult mockResult;

	public BanlistAdminControllerTestCase() {
		super(BanlistAdminController.class);
	}

	@Test
	public void listExpectOneRecord() {
		when(repository.getAllBanlists()).thenReturn(new ArrayList<Banlist>());
			
		compverifynt.list();
		
		assertEquals(new ArrayList<Banlist>(), mockResult.included("banlist"));
	}

	@Before
	public void setup() {
		compverifynt = new BanlistAdminController(repository, mockResult);
	}
}
