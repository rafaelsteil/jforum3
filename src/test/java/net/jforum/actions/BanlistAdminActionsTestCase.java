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

import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.Banlist;
import net.jforum.repository.BanlistRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class BanlistAdminActionsTestCase extends AdminTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private BanlistAdminActions component;
	private BanlistRepository repository = context.mock(BanlistRepository.class);
	private ViewPropertyBag propertyBag = context.mock(ViewPropertyBag.class);

	public BanlistAdminActionsTestCase() {
		super(BanlistAdminActions.class);
	}

	@Test
	public void listExpectOneRecord() {
		context.checking(new Expectations() {{
			one(repository).getAllBanlists(); will(returnValue(new ArrayList<Banlist>()));
			one(propertyBag).put("banlist", new ArrayList<Banlist>());
		}});

		component.list();
		context.assertIsSatisfied();
	}

	@Before
	public void setup() {
		component = new BanlistAdminActions(repository, propertyBag);
	}
}
