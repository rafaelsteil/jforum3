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
package net.jforum.actions.interceptors;

import net.jforum.extensions.ActionExtensionManager;
import net.jforum.extensions.Extends;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Rafael Steil
 * @author Bill
 */
//@Intercepts
public class ExtendsAnnotationInterceptor implements Interceptor {
	private final ActionExtensionManager manager;

	public ExtendsAnnotationInterceptor(ActionExtensionManager manager) {
		this.manager = manager;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		stack.next(method, resourceInstance);
		// TODO
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return method.getMethod().isAnnotationPresent(Extends.class);
	}
}
