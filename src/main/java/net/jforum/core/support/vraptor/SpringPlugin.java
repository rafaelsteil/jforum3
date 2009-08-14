package net.jforum.core.support.vraptor;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vraptor.introspector.Introspector;
import org.vraptor.plugin.VRaptorPlugin;
import org.vraptor.plugin.spring.SpringProvider;
import org.vraptor.webapp.WebApplication;

/**
 * Spring plugin which reads the application bean configuration and allows it to
 * be used for VRaptor's dependencies injection.
 *
 * @author Guilherme Silveira
 * @author Fabio Patricio
 * @author Rafael Steil
 */
public class SpringPlugin implements VRaptorPlugin {
	private static final String CONTEXT_KEY_DEFAULT_VALUE = "springContext";
	protected static final String CONFIG_FILE_KEY = "configFile";
	protected static final String CONTEXT_KEY_KEY = "contextName";
	private static final Logger LOG = Logger.getLogger(SpringPlugin.class);
	private final Map<String, String> properties;

	public SpringPlugin(Map<String, String> properties) {
		this.properties = properties;
	}

	public void init(WebApplication application) {
		long startTime = System.currentTimeMillis();
		LOG.info("Loading Spring root WebApplicationContext");

		final String configFile = this.properties.get(CONFIG_FILE_KEY);
		String contextName = this.getContextName(application);

		LOG.info("Configuration file for Spring is " + configFile);
		LOG.info("Context name for Spring is " + contextName);

		ApplicationContext context = new ClassPathXmlApplicationContext(configFile);

		application.getApplicationContext().setAttribute(contextName, context);
		application.getApplicationContext().setAttribute(ApplicationContext.class.getName(), context);

		LOG.debug("Published root WebApplicationContext [" + context + "] as ServletContext attribute");
		LOG.info("Using context class [" + context.getClass().getName() + "] for root ApplicationContext");
		long elapsedTime = System.currentTimeMillis() - startTime;
		LOG.info("Root ApplicationContext: initialization completed in " + elapsedTime + " ms");

		Introspector introspector = application.getIntrospector();
		SpringProvider provider = new SpringProvider(introspector.getBeanProvider(), context);

		LOG.debug("Registering new spring provider with the introspector");

		introspector.setBeanProvider(provider);
	}

	private String getContextName(WebApplication application) {
		return getProperty(CONTEXT_KEY_KEY, CONTEXT_KEY_DEFAULT_VALUE);
	}

	private String getProperty(String key, String defaultValue) {
		return properties.containsKey(key) ? properties.get(key) : defaultValue;
	}
}
