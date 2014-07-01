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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Rafael Steil
 * @version $Id: $
 */
public class TestCaseUtils {
	/**
	 * Retrive the root directory of the application
	 * @return The application root
	 */
	public static String getApplicationRoot() {
		String filePath = TestCaseUtils.class.getResource(".").getFile();
		int index = filePath.indexOf("/target/test");
		return filePath.substring(0, index)+"/src/main";
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

	public static void copyFile(File in, File out) {
		try {
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);

			byte[] buf = new byte[1024];
			int i = 0;

			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}

			fis.close();
			fos.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
