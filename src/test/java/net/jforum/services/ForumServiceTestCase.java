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
import net.jforum.entities.Forum;
import net.jforum.repository.ForumRepository;

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
public class ForumServiceTestCase {
	
	@Mock private ForumRepository repository;
	@InjectMocks private ForumService service;

	@Test
	public void deleteUsingNullIdsShouldIgnore() {
		service.delete(null);

		verifyZeroInteractions(repository);
	}

	@Test
	public void delete() {
		when(repository.get(1)).thenReturn(new Forum());
		when(repository.get(2)).thenReturn(new Forum());

		service.delete(1, 2);

		verify(repository, times(2)).remove(notNull(Forum.class));
	}

	@Test(expected = NullPointerException.class)
	public void updateUsingNullForumExpectsNPE() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingForumIdZeroExpectsValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);
		f.setId(0);

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingCategoryNullExpectsValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		f.setId(1);
		f.setCategory(null);

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingCategoryWithIdZeroExpectsValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		f.setId(1);
		Category category = new Category();
		category.setId(0);
		f.setCategory(category);

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingEmptyNameExpectsValidationException() {
		Forum f = new Forum();
		f.setName("");
		f.setId(1);
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingNullNameExpectsValidationException() {
		Forum f = new Forum();
		f.setName(null);
		f.setId(1);
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);

		service.update(f);
	}

	@Test
	public void addExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);

		service.add(f);

		verify(repository).add(f);
	}

	@Test(expected = ValidationException.class)
	public void addUsingForumIdBiggerThanZeroExpectValidationException() {
		Forum f = new Forum();
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);
		f.setName("f1");
		f.setId(1);

		service.add(f);
	}

	@Test(expected = NullPointerException.class)
	public void addNullForumExpectNPE() {
		service.add(null);
	}

	@Test(expected = ValidationException.class)
	public void addForumWithoutNameExpectValidationException() {
		Forum f = new Forum();
		f.setName(null);
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumWithEmptyNameExpectValidationException() {
		Forum f = new Forum();
		f.setName("");
		Category category = new Category();
		category.setId(1);
		f.setCategory(category);
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumWithNullCategoryExpectValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		f.setCategory(null);
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumUsingCategoryWithoutIdExpectValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		Category category = new Category();
		category.setId(0);
		f.setCategory(category);
		service.add(f);
	}

	@Test
	public void upCategoryOrderExpectToBeInFirstPosition() {
		final Forum forumToChange = newForumWithOrder(1, 2, newForumWithOrder(2, 1), newForumWithOrder(1, 2));
		when(repository.get(1)).thenReturn(forumToChange);

		service.upForumOrder(1);

		verify(repository, times(2)).update(notNull(Forum.class));
		Assert.assertEquals(1, forumToChange.getDisplayOrder());
	}

	@Test
	public void downCategoryOrderExpectToBeInLastPosition() {
		final Forum forumToChange = newForumWithOrder(1, 1, newForumWithOrder(1, 1), newForumWithOrder(2, 2));
		when(repository.get(1)).thenReturn(forumToChange);

		service.downForumOrder(1);

		verify(repository, times(2)).update(notNull(Forum.class));
		Assert.assertEquals(2, forumToChange.getDisplayOrder());
	}

	@Test
	public void upCategoryOrderCategoryAlreadyFistShouldIgnore() {
		final Forum forumToChange = newForumWithOrder(1, 1, newForumWithOrder(1, 1), newForumWithOrder(2, 2));
		when(repository.get(1)).thenReturn(forumToChange);

		service.upForumOrder(1);

		verify(repository, never()).update(notNull(Forum.class));
	}

	@Test
	public void downCategoryOrderCategoryAlredyLastShouldIgnore() {
		final Forum categoryToChange = newForumWithOrder(2, 2, newForumWithOrder(1, 1), newForumWithOrder(2, 2));
		when(repository.get(2)).thenReturn(categoryToChange);

		service.downForumOrder(2);

		verify(repository, never()).update(notNull(Forum.class));
	}

	private Forum newForumWithOrder(int forumId, int order, final Forum... categoryForums) {
		Forum f = new Forum();

		f.setId(forumId);
		f.setDisplayOrder(order);
		Category category = mock(Category.class);
		when(category.getForums()).thenReturn(Arrays.asList(categoryForums));

		f.setCategory(category);

		return f;
	}
}
