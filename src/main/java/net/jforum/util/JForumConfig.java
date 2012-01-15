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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import net.jforum.core.exceptions.ForumException;
import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 * @author Jose Donizetti Brito Junior
 */
@Component
@ApplicationScoped
public class JForumConfig extends PropertiesConfiguration {
	private ConfigRepository configRepository;

	public JForumConfig(/*ConfigRepository configRepository, HibernateAwareTask hibernateTask*/) {
		this.setReloadingStrategy(new FileChangedReloadingStrategy());
		this.setDelimiterParsingDisabled(true);


		try {
			loadProps();

			/*
			//in test environment, hibernateTask could be null
			if(hibernateTask != null){
				hibernateTask.execute(new HibernateRunnable() {
					@Override
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
			*/
		}
		catch (Exception e) {
			throw new ForumException(e);
		}
	}

	private void loadProps() throws ConfigurationException, Exception {
		this.load(this.getClass().getResourceAsStream("/jforumConfig/SystemGlobals.properties"));
		this.loadCustomProperties();
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
			List<Config> databasesProperties = this.configRepository.getAll();

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
}
