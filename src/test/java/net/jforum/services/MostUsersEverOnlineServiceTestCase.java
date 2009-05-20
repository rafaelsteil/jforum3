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

import net.jforum.entities.Config;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.repository.ConfigRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author Rafael Steil
 */
public class MostUsersEverOnlineServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ConfigRepository repository = context.mock(ConfigRepository.class);
	private MostUsersEverOnlineService service = new MostUsersEverOnlineService(repository);

	@Test
	public void currenTotalIsBiggerExpectsNewTotalAndNewTime() {
		final long time = System.currentTimeMillis();

		context.checking(new Expectations() {{
			Config c = new Config(); c.setName(ConfigKeys.MOST_USERS_EVER_ONLINE); c.setValue(Long.toString(time - 100) + "/10");
			one(repository).getByName(ConfigKeys.MOST_USERS_EVER_ONLINE); will(returnValue(c));
			one(repository).update(with(aNonNull(Config.class)));
		}});

		MostUsersEverOnline most = service.getMostRecentData(20);
		context.assertIsSatisfied();

		Assert.assertEquals(20, most.getTotal());
		Assert.assertTrue(most.getDate().getTime() >= time);
	}

	@Test
	public void currentTotalIsSmallerExpectsStoredTotal() {
		final long time = System.currentTimeMillis();

		context.checking(new Expectations() {{
			Config c = new Config(); c.setValue(Long.toString(time) + "/10");
			one(repository).getByName(ConfigKeys.MOST_USERS_EVER_ONLINE); will(returnValue(c));
		}});

		MostUsersEverOnline most = service.getMostRecentData(5);
		context.assertIsSatisfied();

		Assert.assertEquals(time, most.getDate().getTime());
		Assert.assertEquals(10, most.getTotal());
	}

	@Test
	public void expectsEmptyShouldCreateNew() {
		context.checking(new Expectations() { {
			one(repository).getByName(ConfigKeys.MOST_USERS_EVER_ONLINE); will(returnValue(null));
			one(repository).add(with(aNonNull(Config.class)));
		}});

		MostUsersEverOnline most = service.getMostRecentData(2);
		context.assertIsSatisfied();

		Assert.assertEquals(2, most.getTotal());
		Assert.assertTrue(System.currentTimeMillis() >= most.getDate().getTime());
	}
}
