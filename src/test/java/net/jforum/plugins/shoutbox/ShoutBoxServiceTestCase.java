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
package net.jforum.plugins.shoutbox;

import java.io.IOException;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ShoutBoxServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ShoutBoxRepository repository = context.mock(ShoutBoxRepository.class);
	private ShoutBoxService service = new ShoutBoxService(repository);

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.add(null);
	}

	@Test(expected = ValidationException.class)
	public void add0IdExpectException() {
		ShoutBox s = new ShoutBox();
		s.setId(1);
		service.add(s);
	}

	@Test(expected = ValidationException.class)
	public void addShutBoxWithNullCategoryExpectException() {
		ShoutBox s = new ShoutBox();
		s.setId(0);
		service.add(s);
	}

	@Test
	public void addExpectSuccess() throws IOException {
		final ShoutBox s = new ShoutBox();
		Category category = new Category();
		category.setId(1);

		context.checking(new Expectations() {{
			one(repository).add(s);
		}});

		service.add(s);
		context.assertIsSatisfied();
		Assert.assertNotNull(s.getId());
	}

	@Test(expected = NullPointerException.class)
	public void updateNullExpectException() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void update0IdExceptException() {
		ShoutBox s = new ShoutBox();
		s.setId(0);
		service.update(s);
	}

	@SuppressWarnings("serial")
	@Test
	public void updateExpectSuccess() {
		final ShoutBox shoutBox = new ShoutBox(); shoutBox.setId(1);
		final ShoutBox current = new ShoutBox();

		current.setAllowAnonymous(true);
		current.setCategory(null);
		current.setDisabled(false);
		current.setShoutLength(100);

		context.checking(new Expectations() {{
			one(repository).get(shoutBox.getId()); will(returnValue(current));
			one(repository).update(current);
		}});

		shoutBox.setAllowAnonymous(true);
		shoutBox.setCategory(new Category(){{setId(1);}});
		shoutBox.setDisabled(false);
		shoutBox.setShoutLength(200);

		service.update(shoutBox);
		context.assertIsSatisfied();

		Assert.assertEquals(shoutBox.getCategory(), current.getCategory());
		Assert.assertEquals(shoutBox.getShoutLength(), current.getShoutLength());
		Assert.assertEquals(shoutBox.isAllowAnonymous(), current.isAllowAnonymous());
		Assert.assertEquals(shoutBox.isDisabled(), current.isDisabled());
	}

}

