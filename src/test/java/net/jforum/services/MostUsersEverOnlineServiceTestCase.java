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
package net.jforum.services;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.entities.Config;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.repository.ConfigRepository;
import net.jforum.util.ConfigKeys;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class MostUsersEverOnlineServiceTestCase {

	@Mock private ConfigRepository repository;
	@InjectMocks private MostUsersEverOnlineService service;

	@Test
	public void currenTotalIsBiggerExpectsNewTotalAndNewTime() {
		final long time = System.currentTimeMillis();
		Config c = new Config(); c.setName(ConfigKeys.MOST_USERS_EVER_ONLINE); c.setValue(Long.toString(time - 100) + "/10");
		when(repository.getByName(ConfigKeys.MOST_USERS_EVER_ONLINE)).thenReturn(c);

		MostUsersEverOnline most = service.getMostRecentData(20);

		verify(repository).update(notNull(Config.class));
		assertEquals(20, most.getTotal());
		assertTrue(most.getDate().getTime() >= time);
	}

	@Test
	public void currentTotalIsSmallerExpectsStoredTotal() {
		final long time = System.currentTimeMillis();
		Config c = new Config(); c.setValue(Long.toString(time) + "/10");
		when(repository.getByName(ConfigKeys.MOST_USERS_EVER_ONLINE)).thenReturn(c);

		MostUsersEverOnline most = service.getMostRecentData(5);

		assertEquals(time, most.getDate().getTime());
		assertEquals(10, most.getTotal());
	}

	@Test
	public void expectsEmptyShouldCreateNew() {
		when(repository.getByName(ConfigKeys.MOST_USERS_EVER_ONLINE)).thenReturn(null);

		MostUsersEverOnline most = service.getMostRecentData(2);

		verify(repository).add(notNull(Config.class));
		assertEquals(2, most.getTotal());
		assertTrue(System.currentTimeMillis() >= most.getDate().getTime());
	}
}
