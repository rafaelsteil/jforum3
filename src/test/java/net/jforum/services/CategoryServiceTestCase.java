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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;
import net.jforum.repository.CategoryRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTestCase {
	
	@Mock private CategoryRepository repository;
	@InjectMocks private CategoryService service = new CategoryService(repository);

	@Test
	public void deleteUsingNullIdsShouldIgnore() {
		service.delete(null);
	}

	@Test
	public void delete() {
		when(repository.get(1)).thenReturn(new Category());
		when(repository.get(2)).thenReturn(new Category());

		service.delete(1, 2);
		
		verify(repository,times(2)).remove(notNull(Category.class));
	}

	@Test
	public void add() {
		final Category c = newCategoryWithOrder(0, 2);
		
		service.add(c);
		
		verify(repository).add(c);
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
		
		service.update(c);
		
		verify(repository).update(c);
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
		
		when(repository.get(1)).thenReturn(categoryToChange);
		when(repository.getAllCategories()).thenReturn(Arrays.asList(newCategoryWithOrder(2, 1), newCategoryWithOrder(1, 2)));
		
		service.upCategoryOrder(1);
		
		verify(repository,times(2)).update(notNull(Category.class));
		Assert.assertEquals(1, categoryToChange.getDisplayOrder());
	}

	@Test
	public void downCategoryOrderExpectToBeInLastPosition() {
		final Category categoryToChange = newCategoryWithOrder(1, 1);
		
		when(repository.get(1)).thenReturn(categoryToChange);
		when(repository.getAllCategories()).thenReturn(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2)));
		
		service.downCategoryOrder(1);

		verify(repository,times(2)).update(notNull(Category.class));
		Assert.assertEquals(2, categoryToChange.getDisplayOrder());
	}

	@Test
	public void upCategoryOrderCategoryAlreadyFistShouldIgnore() {
		final Category categoryToChange = newCategoryWithOrder(1, 1);
		when(repository.get(1)).thenReturn(categoryToChange);
		when(repository.getAllCategories()).thenReturn(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2)));

		service.upCategoryOrder(1);
		
		verify(repository,never()).update(notNull(Category.class));
	}

	@Test
	public void downCategoryOrderCategoryAlredyLastShouldIgnore() {
		final Category categoryToChange = newCategoryWithOrder(2, 2);
		
		when(repository.get(2)).thenReturn(categoryToChange);
		when(repository.getAllCategories()).thenReturn(Arrays.asList(newCategoryWithOrder(1, 1), newCategoryWithOrder(2, 2)));
	
		service.downCategoryOrder(2);

		verify(repository,never()).update(notNull(Category.class));
	}

	private Category newCategoryWithOrder(int categoryId, int order) {
		Category c = new Category();
		c.setId(categoryId);
		c.setName("c1");
		c.setDisplayOrder(order);

		return c;
	}
}
