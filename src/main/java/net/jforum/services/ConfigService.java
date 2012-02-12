package net.jforum.services;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.Config;
import net.jforum.repository.ConfigRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.I18n;
import net.jforum.util.JForumConfig;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class ConfigService {
	private final JForumConfig config;
	private final ConfigRepository repository;
	private final I18n i18n;

	public ConfigService(JForumConfig config, ConfigRepository repository, I18n i18n) {
		this.config = config;
		this.repository = repository;
		this.i18n = i18n;
	}

	public void save(HttpServletRequest request) {
		for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if (key.startsWith("p_")) {
				String value = request.getParameter(key);

				String name = key.substring(key.indexOf('_') + 1);
				Config entry = this.repository.getByName(name);

				if (entry == null) {
					entry = new Config();
					entry.setName(name);
				}

				entry.setValue(value);

				this.config.clearProperty(name);
				this.config.setProperty(name, value);

				this.repository.update(entry);
			}
		}

		this.i18n.changeBoardDefaultLanguage(this.config.getValue(ConfigKeys.I18N_DEFAULT));
	}
}
