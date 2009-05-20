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
import java.util.List;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.repository.ForumRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class ForumServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ForumRepository repository = context.mock(ForumRepository.class);
	private ForumService service = new ForumService(repository);

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
			one(repository).get(1); will(returnValue(new Forum()));
			one(repository).get(2); will(returnValue(new Forum()));

			exactly(2).of(repository).remove(with(aNonNull(Forum.class)));
		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test(expected = NullPointerException.class)
	public void updateUsingNullForumExpectsNPE() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingForumIdZeroExpectsValidationException() {
		Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
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
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(0); }});

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingEmptyNameExpectsValidationException() {
		Forum f = new Forum();
		f.setName("");
		f.setId(1);
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});

		service.update(f);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingNullNameExpectsValidationException() {
		Forum f = new Forum();
		f.setName(null);
		f.setId(1);
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});

		service.update(f);
	}

	@Test
	public void addExpectSuccess() {
		final Forum f = new Forum();
		f.setName("f1");
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});

		context.checking(new Expectations() {{
			one(repository).add(f);
		}});

		service.add(f);
		context.assertIsSatisfied();
	}

	@Test(expected = ValidationException.class)
	public void addUsingForumIdBiggerThanZeroExpectValidationException() {
		Forum f = new Forum();
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
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
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumWithEmptyNameExpectValidationException() {
		Forum f = new Forum();
		f.setName("");
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(1); }});
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumWithNullCategoryExpectValidationException() {
		Forum f = new Forum(); f.setName("f1");
		f.setCategory(null);
		service.add(f);
	}

	@Test(expected = ValidationException.class)
	public void addForumUsingCategoryWithoutIdExpectValidationException() {
		Forum f = new Forum(); f.setName("f1");
		f.setCategory(new Category() {/**
			 *
			 */
			private static final long serialVersionUID = 1L;

		{ setId(0); }});
		service.add(f);
	}

	@Test
	public void upCategoryOrderExpectToBeInFirstPosition() {
		final Forum forumToChange = newForumWithOrder(1, 2, newForumWithOrder(2, 1), newForumWithOrder(1, 2));

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(forumToChange));
			exactly(2).of(repository).update(with(aNonNull(Forum.class)));
		}});

		service.upForumOrder(1);

		context.assertIsSatisfied();
		Assert.assertEquals(1, forumToChange.getDisplayOrder());
	}

	@Test
	public void downCategoryOrderExpectToBeInLastPosition() {
		final Forum forumToChange = newForumWithOrder(1, 1, newForumWithOrder(1, 1), newForumWithOrder(2, 2));

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(forumToChange));
			exactly(2).of(repository).update(with(aNonNull(Forum.class)));
		}});

		service.downForumOrder(1);
		context.assertIsSatisfied();
		Assert.assertEquals(2, forumToChange.getDisplayOrder());
	}

	@Test
	public void upCategoryOrderCategoryAlreadyFistShouldIgnore() {
		final Forum forumToChange = newForumWithOrder(1, 1, newForumWithOrder(1, 1), newForumWithOrder(2, 2));

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(forumToChange));
			exactly(0).of(repository).update(with(aNonNull(Forum.class)));
		}});

		service.upForumOrder(1);
		context.assertIsSatisfied();
	}

	@Test
	public void downCategoryOrderCategoryAlredyLastShouldIgnore() {
		final Forum categoryToChange = newForumWithOrder(2, 2, newForumWithOrder(1, 1), newForumWithOrder(2, 2));

		context.checking(new Expectations() {{
			one(repository).get(2); will(returnValue(categoryToChange));
			exactly(0).of(repository).update(with(aNonNull(Forum.class)));
		}});

		service.downForumOrder(2);
		context.assertIsSatisfied();
	}

	private Forum newForumWithOrder(int forumId, int order, final Forum... categoryForums) {
		Forum f = new Forum();

		f.setId(forumId);
		f.setDisplayOrder(order);
		f.setCategory(new Category() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Forum> getForums() {
				return Arrays.asList(categoryForums);
			}
		});

		return f;
	}
}
