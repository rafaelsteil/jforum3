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
package net.jforum.actions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.actions.interceptors.ActionSecurityInterceptor;
import net.jforum.core.SecurityConstraint;
import net.jforum.core.exceptions.ForumException;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.security.AdministrationRule;
import net.jforum.services.ConfigService;
import net.jforum.services.ViewService;
import net.jforum.util.JForumConfig;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.InterceptedBy;
import org.vraptor.http.VRaptorServletRequest;

/**
 * @author Jose Donizetti de Brito Junior
 * @author Rafael Steil
 */
@Component(Domain.CONFIG_ADMIN)
@InterceptedBy(ActionSecurityInterceptor.class)
@SecurityConstraint(value = AdministrationRule.class, displayLogin = true)
public class ConfigActions {
	private final ConfigService service;
	private final ViewPropertyBag propertyBag;
	private final VRaptorServletRequest request;
	private final JForumConfig config;
	private final ViewService viewService;

	public ConfigActions(ConfigService service, ViewPropertyBag propertyBag, VRaptorServletRequest request,
			JForumConfig config, ViewService viewService) {
		this.service = service;
		this.propertyBag = propertyBag;
		this.request = request;
		this.config = config;
		this.viewService = viewService;
	}

	public void list(){
		propertyBag.put("locales", this.loadLocaleNames());
		propertyBag.put("config", config);
	}

	public void save() {
		service.save(request);
		viewService.redirectToAction(Actions.LIST);
	}

	private List<String> loadLocaleNames() {
		Properties locales = new Properties();

		FileInputStream fis = null;

		try {
			locales.load(this.getClass().getResourceAsStream("/jforumConfig/languages/locales.properties"));
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
		}

		List<String> localesList = new ArrayList<String>();

		for (Enumeration<?> e = locales.keys(); e.hasMoreElements();) {
			localesList.add((String)e.nextElement());
		}

		return localesList;
	}
}
