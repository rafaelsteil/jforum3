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
package net.jforum.extensions;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.exceptions.ForumException;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.vraptor.annotations.Parameter;
import org.vraptor.component.ComponentManager;
import org.vraptor.component.DefaultComponentContainer;

/**
 * @author Rafael Steil
 */
public class ActionExtensionManagerTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private ApplicationContext applicationContext = context.mock(ApplicationContext.class);
	private DefaultComponentContainer componentContainer = new DefaultComponentContainer();
	private ComponentManager componentManager = componentContainer.getComponentManager();
	private ActionExtensionManager manager = (ActionExtensionManager) applicationContext.
			getBean(ActionExtensionManager.class.getName(), new Object[]{componentManager});

	private static boolean oneExecuted;
	private static boolean twoExecuted;
	private static boolean threeExecuted;

	@Test
	@SuppressWarnings("unchecked")
	public void registerExtensionExpectTwoResults() {
		manager.setExtensions(Arrays.asList(FooExtension.class.getName()));

		Map extensions = this.getExtensions();

		Assert.assertEquals(3, extensions.size());
		Assert.assertTrue(extensions.containsKey("foo.fooOne"));
		Assert.assertTrue(extensions.containsKey("foo.fooTwo"));
		Assert.assertTrue(extensions.containsKey("foo.fooThree"));
	}

	@Test
	public void getLogicDefinition() {
		context.checking(new Expectations() {{
			allowing(applicationContext).getBean(FooExtension.class.getName()); will(returnValue(new FooExtension()));
		}});

		manager.setExtensions(Arrays.asList(FooExtension.class.getName()));

		Assert.assertTrue(oneExecuted);
		Assert.assertTrue(twoExecuted);
		Assert.assertTrue(threeExecuted);
	}

	@Test(expected = ForumException.class)
	public void setExtensionDoestNotHaveAnnotationExpectException() {
		manager.setExtensions(Arrays.asList(WithoutAnnotation.class.getName()));
	}

	@SuppressWarnings("unchecked")
	private Map getExtensions() {
		Field[] fields = manager.getClass().getDeclaredFields();

		for (Field field: fields) {
			if (field.getName().equals("extensions")) {
				field.setAccessible(true);
				try {
					return (Map)field.get(manager);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return new HashMap();
	}

	@ActionExtension(Domain.USER)
	public static class FooExtension {
		@Extends("list")
		public void one(@Parameter(key = "start") int start ) {
			System.out.println(start);
			oneExecuted = true;
		}

		@Extends("list2")
		public void two() { twoExecuted = true; }

		public void notAnExtension() {}
	}

	private static class WithoutAnnotation {}
}
