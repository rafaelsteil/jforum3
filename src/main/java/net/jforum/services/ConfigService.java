package net.jforum.services;

import java.util.Enumeration;

import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;

import org.vraptor.http.VRaptorServletRequest;

/**
 * @author Rafael Steil
 */
public class ConfigService {
	private final JForumConfig config;
	private final ConfigRepository repository;
	private final I18n i18n;

	public ConfigService(JForumConfig config, ConfigRepository repository, I18n i18n) {
		this.config = config;
		this.repository = repository;
		this.i18n = i18n;
	}

	public void save(VRaptorServletRequest request) {
		for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = (String)e.nextElement();

			if (key.startsWith("p_")) {
				String value = request.getParameter(key);

				String name = key.substring(key.indexOf('_') + 1);
				Config entry = repository.getByName(name);

				if (entry == null) {
					entry = new Config();
					entry.setName(name);
				}

				entry.setValue(value);

				config.clearProperty(name);
				config.setProperty(name, value);

				repository.update(entry);
			}
		}

		i18n.changeBoardDefaultLanguage(config.getValue(ConfigKeys.I18N_DEFAULT));
	}
}
