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
package net.jforum.formatters;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PostFormattersTestCase {
	@Test
	public void organizeOrder() {
		List<Formatter> original = new ArrayList<Formatter>() {{
			add(new A());
			add(new B());
			add(new C());
			add(new D());
		}};

		PostFormatters pf = new PostFormatters(original);
		Assert.assertEquals(B.class, pf.get(0).getClass());
		Assert.assertEquals(D.class, pf.get(1).getClass());
		Assert.assertEquals(C.class, pf.get(2).getClass());
		Assert.assertEquals(A.class, pf.get(3).getClass());
	}
}

@FormatAfter(C.class)
class A extends EmptyFormatter { }
class B extends EmptyFormatter { }
class C extends EmptyFormatter { }
@FormatAfter(B.class)
class D extends EmptyFormatter { }

class EmptyFormatter implements Formatter {
	@Override
	public String format(String text, PostOptions postOptions) { return null; }
}
