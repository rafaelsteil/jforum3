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
package net.jforum.repository;

import java.util.List;

import net.jforum.entities.Config;

/**
 * @author Rafael Steil
 * @author Jose Donizetti de Brito Junior
 */
public interface ConfigRepository extends Repository<Config> {
	public Config getByName(String configName);

	public List<Config> getAll();
}
