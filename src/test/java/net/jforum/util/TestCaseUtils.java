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
package net.jforum.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * @author Rafael Steil
 * @version $Id: $
 */
public class TestCaseUtils {
	/**
	 * Create a new mockery using ClassImposteriser.INSTANCE
	 * @return the mockery instance
	 */
	public static Mockery newMockery() {
		return new Mockery() {{
			setImposteriser(ClassImposteriser.INSTANCE);
		}};
	}

	/**
	 * Retrieve the path from a real file in the webapp classpath
	 * @param file the file wanted, relative to the classpath
	 * @return the complete file path
	 */
	public static String getRealFilePath(String file) {
		return getApplicationRoot() + "/webapp/WEB-INF/classes" + file;
	}

	/**
	 * Retrive the root directory of the application
	 * @return
	 */
	public static String getApplicationRoot() {
		String filePath = TestCaseUtils.class.getResource("/").getFile();
		return filePath.substring(0, filePath.length() - "/target/tests/".length());
	}

	/**
	 * Executd a private method
	 * @param methodName the method name
	 * @param instance the instance the method belongs to
	 * @param args the (optional) arguments passd to the method
	 * @return the method original return type.
	 * @throws Exception
	 */
	public static Object executePrivateMethod(String methodName, Object instance, Object... args) throws Exception {
		Method method = searchMethod(methodName, instance.getClass());

		if (method == null) {
			method = searchMethod(methodName, instance.getClass().getSuperclass());
		}

		if (method != null) {
			return method.invoke(instance, args);
		}

		throw new IllegalArgumentException("Method not found");
	}

	private static Method searchMethod(String methodName, Class<?> klass) {
		for (Method method : klass.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static Object getPrivateField(String fieldName, Object instance) throws Exception {
		for (Field field : instance.getClass().getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field.get(instance);
			}
		}

		throw new IllegalArgumentException("Field not found");
	}

	/**
	 * Load a Spring configuration file
	 * @param file the file name, relative to /jforumConfig/ in the webapp classpath
	 * @return the xml contents, without the namescape
	 */
	public static String loadSpringFile(String file) {
		String filePath = TestCaseUtils.getRealFilePath("/jforumConfig/" + file);
		BufferedReader reader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(filePath);
			reader = new BufferedReader(fileReader);
			int c = 0;
			char[] ch = new char[4096];

			StringBuilder sb = new StringBuilder();

			while ((c = reader.read(ch, 0, ch.length)) != -1) {
				sb.append(ch, 0, c);
			}

			return sb.toString()
				.replaceAll("(?s)(?i)<beans (.*?)>", "<beans>")
				.replaceAll("<aop:", "<aop-"); // Remove the namespace
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				fileReader.close();
				reader.close();
			}
			catch (Exception e) {}
		}
	}
}
