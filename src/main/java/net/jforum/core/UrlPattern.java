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
package net.jforum.core;

/**
 * URL Patterns holder. Represents a single URL pattern. Each pattern is
 * composed by a name, the pattern itself, the pattern's size and the splited
 * variables. <br>
 * <br>
 * 
 * The pattern is expected in the form <i>var1, var2, varN</i>, in the correct
 * order. This means that if <i>var1</i> comes first, it <b>must</b> come
 * first in the URL. The same is valid to others.<br>
 * <br>
 * 
 * Please note that "first" here is "first" after regular URL, which is composed
 * by server and servlet name, in the most simple case.<br>
 * <br>
 * 
 * <b>Example:</b><br>
 * 
 * URL: <i>http://localhost:8080/webappName/someDir/myServlet/news/view/3.page<i>.
 * <br>
 * In this case, <i>http://localhost:8080/webappName/someDir/myServlet/</i> is
 * the regular URL, the part that we don't care about. We only want the part
 * <i>news/view/3.page</i> ( where .page is the servlet extension ). <br>
 * For this URL, we could make the following pattern:<br>
 * <br>
 * 
 * <i>news.view.1 = news_id</i><br>
 * <br>
 * 
 * Here, <i>news.view.1</i> is the pattern's name, and <i>news_id</i> is the
 * patterns itself. <br>
 * Another example:<br>
 * <br>
 * 
 * <i>news.view.2 = page, news_id</i><br>
 * <br>
 * 
 * In this case we have a new var called <i>page</i>, that represents the page
 * being seen.<br>
 * Each entry is composed in the form:<br>
 * <br>
 * 
 * <i>&lt;moduleName&gt;.&lt;actionName&gt;.&lt;numberOfParameters&gt; = &lt;var
 * 1&gt;,&lt;var n&gt;</i> <br>
 * <br>
 * 
 * Please note that module and action's name aren't pattern's composition, so
 * don't put them there. The system will consider that the pattern only contains
 * the variables diferent to each request ( e.g, id's ). If the pattern you're
 * constructing doesn't have any variable, just leave it blank, like<br>
 * <br>
 * 
 * <i>myModule.myAction.0 = </i><br>
 * <br>
 * 
 * @author Rafael Steil
 * @version $Id: $
 */
public class UrlPattern {
	private String value;
	private int size;
	private String[] vars;

	public UrlPattern(String value) {
		this.value = value;

		this.processPattern();
	}

	private void processPattern() {
		String[] p = this.value.split(",");

		this.vars = new String[p.length];
		this.size = ((((p[0]).trim()).equals("")) ? 0 : p.length);

		for (int i = 0; i < this.size; i++) {
			this.vars[i] = (p[i]).trim();
		}
	}

	/**
	 * Get pattern's total vars
	 * 
	 * @return The total
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Gets the vars. The URL variables are in the correct order, which means
	 * that the first position always will be "something1", the second
	 * "something2" and so on. The system expects this order never changes from
	 * requisition to requisition.
	 * 
	 * @return The vars
	 */
	public String[] getVars() {
		return this.vars;
	}
}
