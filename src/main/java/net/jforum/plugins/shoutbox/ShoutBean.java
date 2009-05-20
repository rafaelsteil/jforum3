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
package net.jforum.plugins.shoutbox;

import java.io.Serializable;

/**
 *
 *
 * @author Bill
 *
 */
public class ShoutBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1687281471602604152L;

	//------------ Properties to Serial to JSON/XML ---------------
	private Integer id;
	private String  name;
	private String  message;
	private String  date;
	private boolean  canDel;

	public ShoutBean(Shout shout, ShoutService shoutService,String contexPath) {
		id          = shout.getId();
		name        = shoutService.getShouter(shout);
		message     = shoutService.formatShoutMessage(shout,contexPath);
		date        = shoutService.formatShoutDate(shout);
	}

	//------------ Getter & Setter ---------------------------------
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isCanDel() {
		return canDel;
	}

	public void setCanDel(boolean canDel) {
		this.canDel = canDel;
	}

}
