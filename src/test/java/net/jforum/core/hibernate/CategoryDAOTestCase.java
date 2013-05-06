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
package net.jforum.core.hibernate;

import java.lang.reflect.Field;
import java.util.List;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.ForumRepository;

import org.hibernate.NonUniqueObjectException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class CategoryDAOTestCase extends AbstractDAOTestCase<Category> {
	@Test
	public void insertShouldIncrementDisplayOrder() {
		Category c1 = this.newCategory("c1", false);
		Category c2 = this.newCategory("c2", false);

		CategoryRepository dao = this.newDao();

		this.insert(c1, dao);
		this.insert(c2, dao);

		Assert.assertEquals(1, c1.getDisplayOrder());
		Assert.assertEquals(2, c2.getDisplayOrder());
	}

	@Test
	public void insert() {
		String name = "category test 1";
		boolean moderated = false;

		Category c = this.newCategory(name, moderated);
		CategoryRepository dao = this.newDao();

		this.insert(c, dao);
		Assert.assertTrue(c.getId() > 0);

		Category loaded = dao.get(c.getId());

		Assert.assertNotNull(loaded);
		Assert.assertEquals(name, loaded.getName());
		Assert.assertEquals(moderated, c.isModerated());
		Assert.assertEquals(1, c.getDisplayOrder());
	}

	@Test
	public void updateChangingAnInstanceLoadedFromTheDb() {
		CategoryRepository dao = this.newDao();
		Category c = this.newCategory("c1", false);

		this.insert(c, dao);

		Category loaded = dao.get(c.getId());

		loaded.setName("changed");
		loaded.setModerated(true);
		loaded.setDisplayOrder(3);

		this.update(loaded, dao);

		loaded = dao.get(c.getId());

		Assert.assertEquals("changed", loaded.getName());
		Assert.assertEquals(true, loaded.isModerated());
		Assert.assertEquals(3, loaded.getDisplayOrder());
	}

	@Test(expected=NonUniqueObjectException.class)
	public void updateChangingAnInstanceCreatedByHandUsingARealIdShouldFail() {
		CategoryRepository dao = this.newDao();
		Category c = this.newCategory("c1", false);
		this.insert(c, dao);
		this.commit();
		
		int id = c.getId();
		
		Category c2 = new Category();
		c2.setId(id);
		c2.setName("c2");
		c2.setModerated(true);
		c2.setDisplayOrder(2);

		this.update(c2, dao);

		Category loaded = dao.get(id);
		Assert.assertEquals("c2", loaded.getName());
		Assert.assertEquals(true, loaded.isModerated());
		Assert.assertEquals(2, loaded.getDisplayOrder());
	}

	@Test
	public void delete() {
		CategoryRepository dao = this.newDao();
		Category c = this.newCategory("c3", false);
		this.insert(c, dao);

		c = dao.get(c.getId());
		this.delete(c, dao);

		c = dao.get(c.getId());
		Assert.assertNull(c);
	}

	@Test
	public void selectAll() {
		CategoryRepository dao = this.newDao();

		for (int i = 0; i < 3; i++) {
			this.insert(this.newCategory("name" + i, false), dao);
		}

		List<Category> categories = dao.getAllCategories();

		Assert.assertEquals(3, categories.size());

		int lastDisplayOrder = -1;

		for (int i = 0; i < 3; i++) {
			Category c = categories.get(i);

			Assert.assertEquals("name" + i, c.getName());
			Assert.assertTrue(c.getDisplayOrder() > lastDisplayOrder);
			lastDisplayOrder = c.getDisplayOrder();
		}
	}

	@Test
	public void insertThreeCategoriesWithTwoForumsEachExpectSelectInCorrectOrder() {
		CategoryRepository dao = this.newDao();
		ForumRepository forumDao = new ForumRepository(session());
		int totalCategories = 3;

		for (int i = 1; i <= totalCategories; i++) {
			Category category = this.newCategory("name" + i, false);
			this.insert(category, dao);

			forumDao.add(this.newForum(String.format("f%d.1", i), category));
			forumDao.add(this.newForum(String.format("f%d.2", i), category));
		}

		List<Category> categories = dao.getAllCategories();

		Assert.assertEquals(totalCategories, categories.size());

		String[] expectedForumNames = { "f1.1", "f1.2", "f2.1", "f2.2", "f3.1", "f3.2" };

		int lastDisplayOrder = -1;

		int forumCounter = 0;
		int nameCounter = 1;
		for (Category c : categories) {
			this.injectRepository(c);

			Assert.assertEquals("name" + nameCounter++, c.getName());
			Assert.assertTrue(c.getDisplayOrder() > lastDisplayOrder);

			List<Forum> forums = c.getForums();
			Assert.assertEquals(2, forums.size());

			Assert.assertEquals(expectedForumNames[forumCounter], forums.get(0).getName());
			Assert.assertEquals(expectedForumNames[forumCounter + 1], forums.get(1).getName());

			lastDisplayOrder = c.getDisplayOrder();

			forumCounter += 2;
		}
	}

	private void injectRepository(Category c) {
		Field[] fields = c.getClass().getDeclaredFields();

		try {
			for (Field field: fields) {
				if (field.getName().equals("repository")) {
					field.setAccessible(true);
					field.set(c, this.newDao());
					break;
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createCategoryAndTwoForumsGetCategoryExpectForumsInCorrectOrder() {
		CategoryRepository dao = this.newDao();
		Category category = this.newCategory("cat1", false);
		this.insert(category, dao);

		ForumRepository forumDao = new ForumRepository(session());

		forumDao.add(this.newForum("f1", category));
		forumDao.add(this.newForum("f2", category));

		category = dao.get(category.getId());
		this.injectRepository(category);

		Assert.assertNotNull(category.getForums());
		Assert.assertEquals(2, category.getForums().size());
		Assert.assertEquals("f1", category.getForums().get(0).getName());
		Assert.assertEquals("f2", category.getForums().get(1).getName());
	}

	private Forum newForum(String name, Category category) {
		Forum f = new Forum();

		f.setName(name);
		f.setCategory(category);

		return f;
	}

	private Category newCategory(String name, boolean moderated) {
		Category c = new Category(this.newDao());

		c.setName(name);
		c.setModerated(moderated);

		return c;
	}

	private CategoryRepository newDao() {
		return new CategoryRepository(session());
	}
}
