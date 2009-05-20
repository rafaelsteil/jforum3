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

import java.util.Arrays;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class CategoryServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private CategoryRepository repository = context.mock(CategoryRepository.class);
	private CategoryService service = new CategoryService(repository);

	@Test
	public void deleteUsingNullIdsShouldIgnore() {
		context.checking(new Expectations() {{

		}});

		service.delete(null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Category()));
			one(repository).get(2); will(returnValue(new Category()));

			exactly(2).of(repository).remove(with(aNonNull(Category.class)));
		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void add() {
		final Category c = newCategoryWithOrder(0, 2);

		context.checking(new Expectations() {{
			one(repository).add(c);
		}});

		service.add(c);

		context.assertIsSatisfied();
	}

	@Test(expected = ValidationException.class)
	public void addUsingACategoryWithIdBiggerThanZeroExpectsValidationException() {
		Category c = new Category();
		c.setName("c1");
		c.setId(2);

		service.add(c);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingACategoryWithoutAnIdExpectsException() {
		service.update(new Category());
	}

	@Test
	public void updateUsingAGoodCategoryExpectSuccess() {
		final Category c = newCategoryWithOrder(1, 2);
		c.setId(2);

		context.checking(new Expectations() {{
			one(repository).update(c);
		}});

		service.update(c);

		context.assertIsSatisfied();
	}

	@Test(expected = NullPointerException.class)
	public void addUsingNullCategoryExpectsNPE() {
		service.add(null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingCategoryWithoutNameExpectsValidationException() {
		service.add(new Category());
	}

	@Test(expected = NullPointerException.class)
	public void updateUsingNullCategoryExpectsNPE() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingCategoryWithoutNameExpectsValidationException() {
		Category c = new Category();
		c.setId(1);
		service.add(c);
	}

	@Test
	public void upCategoryOrderExpectToBeInFirstPosition() {
		final Category categoryToChange = newCategoryWithOrder(1, 2);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(categoryToChange));
			one(repository).getAllCategories(); will(returnValue(Arrays.asList(newCategoryWithOrder(2, 1), newCategoryWithOrder(1, 2))));
			exactly(2).of(repository).update(with(aNonNull(Category.class)));
		}});

		service.upCategoryOrder(1);

		context.assertIsSatisfied();
		Assert.assertEquals(1, categoryToChange.getDisplayOrder());
	}

	@Test
	public void downCategoryOrderExpectToBeInLastPosition() {
		final Category categoryToChange = newCategoryWithOrder(1, 1);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(categoryToChange));
			one(repository).getAllCategories(); will(returnValue(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2))));
			exactly(2).of(repository).update(with(aNonNull(Category.class)));
		}});

		service.downCategoryOrder(1);

		context.assertIsSatisfied();
		Assert.assertEquals(2, categoryToChange.getDisplayOrder());
	}

	@Test
	public void upCategoryOrderCategoryAlreadyFistShouldIgnore() {
		final Category categoryToChange = newCategoryWithOrder(1, 1);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(categoryToChange));
			one(repository).getAllCategories(); will(returnValue(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2))));
			exactly(0).of(repository).update(with(aNonNull(Category.class)));
		}});

		service.upCategoryOrder(1);

		context.assertIsSatisfied();
	}

	@Test
	public void downCategoryOrderCategoryAlredyLastShouldIgnore() {
		final Category categoryToChange = newCategoryWithOrder(2, 2);

		context.checking(new Expectations() {{
			one(repository).get(2); will(returnValue(categoryToChange));
			one(repository).getAllCategories(); will(returnValue(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2))));
			exactly(0).of(repository).update(with(aNonNull(Category.class)));
		}});

		service.downCategoryOrder(2);

		context.assertIsSatisfied();
	}

	private Category newCategoryWithOrder(int categoryId, int order) {
		Category c = new Category();
		c.setId(categoryId);
		c.setName("c1");
		c.setDisplayOrder(order);

		return c;
	}
}
