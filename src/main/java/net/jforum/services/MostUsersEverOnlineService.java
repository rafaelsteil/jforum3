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
package net.jforum.services;

import java.util.Date;

import net.jforum.entities.Config;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.repository.ConfigRepository;
import net.jforum.util.ConfigKeys;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class MostUsersEverOnlineService {
	private ConfigRepository repository;

	public MostUsersEverOnlineService(ConfigRepository repository) {
		this.repository = repository;
	}

	public MostUsersEverOnline getMostRecentData(int totalCurrentUsers) {
		Config config = this.repository.getByName(ConfigKeys.MOST_USERS_EVER_ONLINE);

		if (config == null) {
			MostUsersEverOnline most = new MostUsersEverOnline();
			most.setDate(new Date());
			most.setTotal(totalCurrentUsers);

			config = new Config();
			config.setName(ConfigKeys.MOST_USERS_EVER_ONLINE);
			config.setValue(most.getDate().getTime() + "/" + most.getTotal());

			this.repository.add(config);

			return most;

		}
		else {
			String[] p = config.getValue().split("/");
			MostUsersEverOnline most = new MostUsersEverOnline();

			int total = p.length == 2 ? Integer.parseInt(p[1]) : 0;

			if (totalCurrentUsers > total) {
				most.setDate(new Date());
				most.setTotal(totalCurrentUsers);

				config.setValue(most.getDate().getTime() + "/" + most.getTotal());

				this.repository.update(config);

				return most;
			}
			else {
				most.setDate(new Date(Long.parseLong(p[0])));
				most.setTotal(Integer.parseInt(p[1]));

				return most;
			}
		}
	}
}
