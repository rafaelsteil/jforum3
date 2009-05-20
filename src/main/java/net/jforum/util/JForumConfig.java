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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import net.jforum.core.UrlPattern;
import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * @author Rafael Steil
 * @author Jose Donizetti Brito Junior
 */
public class JForumConfig extends PropertiesConfiguration {
	private Map<String, UrlPattern> urlPatterns = new HashMap<String, UrlPattern>();
	private ConfigRepository configRepository;

	public JForumConfig(ConfigRepository configRepository, HibernateAwareTask hibernateTask) {
		this.setReloadingStrategy(new FileChangedReloadingStrategy());

		try {
			this.load(this.getClass().getResourceAsStream("/jforumConfig/SystemGlobals.properties"));
			this.loadCustomProperties();

			hibernateTask.execute(new HibernateRunnable() {
				public void run() {
					try {
						loadDatabaseProperties();
					}
					catch (Exception e) {
						throw new ForumException(e);
					}
				}
			});
		}
		catch (Exception e) {
			throw new ForumException(e);
		}

		this.loadUrlPatterns();
	}

	public String getListAsString(String key) {
		String value = this.getList(key).toString();
		return value.substring(1, value.length() - 1);
	}

	private void loadCustomProperties() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/jforumConfig/jforum-custom.properties");

		if (is != null) {
			Properties custom = new Properties();
			custom.load(is);

			for (Enumeration<?> e = custom.keys(); e.hasMoreElements(); ) {
				String key = (String)e.nextElement();
				this.clearProperty(key);
				this.addProperty(key, custom.get(key));
			}
		}
	}

	private void loadDatabaseProperties() throws Exception{
		if (configRepository != null) {
			List<Config> databasesProperties = configRepository.getAll();

			for (Config config : databasesProperties) {
				this.clearProperty(config.getName());
				this.addProperty(config.getName(), config.getValue());
			}
		}
	}

	/**
	 * @see org.apache.commons.configuration.BaseConfiguration#addPropertyDirect(java.lang.String, java.lang.Object)
	 */
	@Override
	protected void addPropertyDirect(String key, Object value) {
		super.addPropertyDirect(key, value);
	}

	public UrlPattern getUrlPattern(String name) {
		return urlPatterns.get(name);
	}

	/**
	 * Gets the complete path to the application root directory
	 * @return the path to the root directory
	 */
	public String getApplicationPath() {
		return this.getString(ConfigKeys.APPLICATION_PATH);
	}

	/**
	 * Delegates to {@link #getString(String)}
	 * @param key the key to retrieve
	 * @return the key's value
	 */
	public String getValue(String key) {
		return this.getString(key);
	}

	/**
	 * Load the urlPatterns
	 */
	private void loadUrlPatterns() {
		FileInputStream fis = null;

		try {
			Properties p = new Properties();
			p.load(this.getClass().getResourceAsStream("/jforumConfig/urlPattern.properties"));

			for (Entry<Object, Object> entry : p.entrySet()) {
				String name = (String)entry.getKey();
				String value = (String)entry.getValue();

				urlPatterns.put(name, new UrlPattern(value));
			}
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
		}
	}
}
