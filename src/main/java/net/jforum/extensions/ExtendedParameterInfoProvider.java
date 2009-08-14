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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.vraptor.annotations.Parameter;
import org.vraptor.component.MethodParameter;
import org.vraptor.component.ParameterInfoProvider;
import org.vraptor.reflection.StringUtil;

/**
 * Same as vraptor DefaultParameterInfoProvider,
 * except it search the Parameter in Extends Annotation
 * 
 * @author Bill
 * @author Guilherme Silveira
 *
 */
public class ExtendedParameterInfoProvider implements ParameterInfoProvider {


	public List<MethodParameter> provideFor(Method method) {
		Class<?>[] params = method.getParameterTypes();
		Type[] generic = method.getGenericParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		List<MethodParameter> list = new ArrayList<MethodParameter>();

		Extends logic = method.getAnnotation(Extends.class);
		String[] parameterNames = logic != null ? logic.parameters() : null;
		if (parameterNames != null && parameterNames.length != 0
				&& parameterNames.length != method.getParameterTypes().length) {
			throw new IllegalArgumentException(
					"The number of parameters at @Extends does "
							+ "not match the number of this method arguments in method"
							+ method.getName());
		}

		for (int i = 0; i < params.length; i++) {
			Class<?> param = params[i];
			Parameter parameterAnnotation = searchParameterAnnotation(annotations, i);
			String key;

			if (parameterAnnotation != null
					&& !parameterAnnotation.key().equals("")) {
				// using @Parameter key name
				key = parameterAnnotation.key();
			} else {
				// using @Extends(parameters={..} value
				if (parameterNames != null && parameterNames.length != 0) {
					key = parameterNames[i];
				} else {
					key = StringUtil.classNameToInstanceName(param.getSimpleName());
				}
			}

			list.add(new MethodParameter(param, generic[i], i, key));
		}
		return list;
	}

	private Parameter searchParameterAnnotation(Annotation[][] annotations, int i) {
		for (Annotation a : annotations[i]) {
			if (a.annotationType().equals(Parameter.class)) {
				return (Parameter) a;
			}
		}
		return null;
	}

}
