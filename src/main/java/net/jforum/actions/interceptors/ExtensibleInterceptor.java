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
package net.jforum.actions.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.extensions.ActionExtensionManager;
import net.jforum.extensions.Extends;
import net.jforum.services.ViewService;

import org.vraptor.Interceptor;
import org.vraptor.LogicDefinition;
import org.vraptor.LogicException;
import org.vraptor.LogicFlow;
import org.vraptor.LogicRequest;
import org.vraptor.component.BeanConstructor;
import org.vraptor.component.ComponentInstantiationException;
import org.vraptor.component.ComponentType;
import org.vraptor.component.LogicMethod;
import org.vraptor.component.MethodParameter;
import org.vraptor.i18n.ValidationMessage;
import org.vraptor.introspector.BeanProvider;
import org.vraptor.introspector.Introspector;
import org.vraptor.introspector.ReadParameter;
import org.vraptor.reflection.SettingException;
import org.vraptor.view.ViewException;
import org.vraptor.webapp.WebApplication;

/**
 * @author Rafael Steil
 * @author Bill
 */
public class ExtensibleInterceptor implements Interceptor {

	/**
	 * @see org.vraptor.Interceptor#intercept(org.vraptor.LogicFlow)
	 */
	public void intercept(LogicFlow flow) throws LogicException, ViewException {
		//if a extended component marked as component,
		//the extended method read as normal Logic Method
		//if Directly invoke the extended logicMethod,
		//redirect to index page
		LogicRequest logicRequest = flow.getLogicRequest();

		ServletContext servletContext = logicRequest.getServletContext();
		WebApplication application = (WebApplication)servletContext.getAttribute(WebApplication.class.getName());
		Introspector introspector = application.getIntrospector();
		BeanProvider beanProvider = introspector.getBeanProvider();

		if(isExtendedAnnotationPresent(logicRequest)){
			//redirect to index page
			ViewService viewService = (ViewService)beanProvider.findAttribute(logicRequest, ViewService.class.getName());
			viewService.redirectToAction(Domain.FORUMS, Actions.LIST);
		}

		//TODO: preMethed if need this feature

		//normal logic method execute
		flow.execute();

		//extend method exectute if need
		ActionExtensionManager manager = (ActionExtensionManager)servletContext.getAttribute(ActionExtensionManager.class.getName());

		LogicDefinition logicDefinition = logicRequest.getLogicDefinition();
		List<LogicDefinition> extendedLogicDefinitions = manager.getLogicDefinition(logicDefinition);

		for(LogicDefinition extendedLogicDefinition : extendedLogicDefinitions ){
			this.execute(extendedLogicDefinition, logicRequest);
		}
	}

	private void execute(LogicDefinition extendedLogicDefinition,LogicRequest logicRequest){

		ComponentType extendedComponentType = extendedLogicDefinition.getComponentType();
		LogicMethod extendedLogicMethod     = extendedLogicDefinition.getLogicMethod();

		WebApplication application = (WebApplication)logicRequest.getServletContext().getAttribute(WebApplication.class.getName());
		BeanProvider beanProvider = application.getIntrospector().getBeanProvider();

		try {
			BeanConstructor contructory = extendedComponentType.getConstructor();
			Object extendedComponent = contructory.newInstance(logicRequest, beanProvider);

			Object[] methodParamObjects = this.readParameter(logicRequest, extendedLogicMethod, extendedComponent);

			extendedLogicMethod.execute(extendedComponent, logicRequest, methodParamObjects);

		} catch (ComponentInstantiationException e) {
			e.printStackTrace();
		} catch (LogicException e) {
			e.printStackTrace();
		} catch (SettingException e) {
			e.printStackTrace();
		}
	}

	private Object[]readParameter(LogicRequest logicRequest,LogicMethod logicMethod,Object componentInstance) throws SettingException{
		WebApplication application = (WebApplication)logicRequest.getServletContext().getAttribute(WebApplication.class.getName());
		Introspector introspector = application.getIntrospector();

		List<MethodParameter> methodParams = logicMethod.getParameters();

		List<ReadParameter> allParams = new ArrayList<ReadParameter>();
		allParams.addAll(methodParams);

		// instantiate parameters
		Object[] methodParamObjects = new Object[methodParams.size()];
		for (int i = 0; i < methodParamObjects.length; i++) {
			try {
				methodParamObjects[i] = methodParams.get(i).newInstance();
			} catch (ComponentInstantiationException e) {
				methodParamObjects[i] = null;
			}
		}

		List<ValidationMessage> problems = introspector.readParameters(allParams, componentInstance, logicRequest, application.getConverterManager(), methodParamObjects);

		if (problems.size() != 0) {
			throw new SettingException(problems.toString());
		}
		return methodParamObjects;
	}

	private boolean isExtendedAnnotationPresent(LogicRequest logicRequest){
		return logicRequest.getLogicDefinition().getLogicMethod().getMetadata().isAnnotationPresent(Extends.class);
	}

}
