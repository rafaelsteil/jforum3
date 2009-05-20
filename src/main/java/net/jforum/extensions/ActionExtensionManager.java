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
package net.jforum.extensions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.vraptor.LogicDefinition;
import org.vraptor.annotations.Component;
import org.vraptor.component.BeanConstructor;
import org.vraptor.component.Clazz;
import org.vraptor.component.ComponentManager;
import org.vraptor.component.ComponentNotFoundException;
import org.vraptor.component.ComponentType;
import org.vraptor.component.DefaultComponentType;
import org.vraptor.component.DefaultLogicMethod;
import org.vraptor.component.InvalidComponentException;
import org.vraptor.component.LogicMethod;
import org.vraptor.component.LogicNotFoundException;
import org.vraptor.component.MethodParameter;
import org.vraptor.component.ParameterInfoProvider;
import org.vraptor.core.DefaultLogicDefinition;
import org.vraptor.reflection.ReflectionUtil;
import org.vraptor.reflection.StringUtil;
import org.vraptor.scope.ScopeType;
import org.vraptor.webapp.DefaultComponentManager;

/**
 * @author Rafael Steil
 * @author Bill
 */
public class ActionExtensionManager {

	private static final Logger logger = Logger.getLogger(ActionExtensionManager.class);

	private ComponentManager componentManager;

	private final ParameterInfoProvider paramInfo = new ExtendedParameterInfoProvider();;

	private final ConcurrentMap<String, ConcurrentMap<String, List<LogicMethod>>> extensions =
		new ConcurrentHashMap<String, ConcurrentMap<String, List<LogicMethod>>>();

	public ActionExtensionManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}

	public List<LogicDefinition> getLogicDefinition(LogicDefinition logicDefinition) {
		List<LogicDefinition> definitions = new ArrayList<LogicDefinition>();
		String componentName = logicDefinition.getComponentType().getName();
		String preLogicMethodName = logicDefinition.getLogicMethod().getName();

		List<LogicMethod> extendedLogicMethods = this.getExtendedLogicMethod(componentName,preLogicMethodName);
		DefaultLogicDefinition definition = null;
		for(LogicMethod method : extendedLogicMethods){
			definition = new DefaultLogicDefinition(method.getComponentType(), method);
			definitions.add(definition);
		}

		return definitions;
	}

	public void setExtensions(List<String> list) {
		if(componentManager == null)
			throw new NullPointerException("Need Component Manager");

		Class<?> type;
		for (String beanName : list) {
			try {
				type = Class.forName(beanName);
				if (Modifier.isPublic(type.getModifiers())) {
					register(type);
				} else {
					logger.warn("Ignoring non public class " + beanName);
				}
			} catch (ClassNotFoundException e) {
				logger.warn("Class Not Found " + beanName);
			}
		}//end loop
	}

	@SuppressWarnings("unchecked")
	private void register(Class<?> type) {
		/*if(!type.isAnnotationPresent(ActionExtension.class)){
			return null;
		}*/
		String thisComponentName = null;
		BeanConstructor constructor = null;
		if (type.isAnnotationPresent(Component.class)) {
			thisComponentName = getComponentName(type);
		}else{
			try {
				Clazz clazz = new Clazz(type);
				constructor = clazz.findSingleConstructor();
			} catch (InvalidComponentException e) {
				logger.warn("Invalid Component" + type.getName());
				return;
			}
		}

		//super component
		//check the supperComponent exist?
		String preComponentName = getExtendedComponentName(type); // which compoent to extend
		Map<String,Method> methods = findExtendsMethod(type);

		for(String preLogicName:methods.keySet()){
			try{
				componentManager.getComponent(preComponentName, preLogicName);//validate component and logic
			}catch(ComponentNotFoundException e){
				//not found the component, return
				logger.warn("Extend Invalid Component" + preComponentName);
				return ;
			}catch(LogicNotFoundException e){
				//can no find correct action to extend
				logger.warn("Extend Invalid Logic Action" + preComponentName);
				//remove from extend method
				methods.remove(preLogicName);
				continue;
			}

			Method extendedMethod = methods.get(preLogicName);
			String extendedMethodName =extendedMethod.getName();

			//this extend component is annotated as component,
			//Reuse the LogicMethod
			if(thisComponentName!=null) {
				try {
					ComponentType definedComponentType = componentManager.getComponent(thisComponentName, extendedMethodName);
					LogicMethod extendedLogicMethod = definedComponentType.getLogic(extendedMethodName);

					this.register(preComponentName, preLogicName, extendedLogicMethod);
				} catch (ComponentNotFoundException e) {
					// should be find
				} catch (LogicNotFoundException e) {
					// should be find
				}

			}
		}

		// not annotated as component
		if(thisComponentName == null ) {
			ScopeType scope = ScopeType.REQUEST;

			//construcotr is aviable, otherwise it will not reach here

			Map<String, DefaultLogicMethod> actions = createLogics(methods);

			//Extended Component no need to support these feature
			//List<InterceptorType> interceptors = InterceptorType.getInterceptors(type);
			// read fields
			//List<FieldAnnotation<In>> ins = ReflectionUtil.readAnnotations(type, In.class);
			// destroy method
			//String destroyLogicName = "destroy";
			//List<ReadParameter> reads = findParameters(type);

			DefaultComponentType componentType = new DefaultComponentType(type, type.getName(),
					scope, constructor, actions, null, null, null, null);

			for(String preLogicName : actions.keySet()){
				DefaultLogicMethod extendedLogicMethod = actions.get(preLogicName);
				extendedLogicMethod.setComponentType(componentType);
				register(preComponentName, preLogicName,extendedLogicMethod);
			}
		}


	}

	private Map<String, DefaultLogicMethod> createLogics(
			Map<String, Method> methods) {
		Map<String, DefaultLogicMethod> actions = new HashMap<String, DefaultLogicMethod>();
		for(String name : methods.keySet()){
			Method method = methods.get(name);
			DefaultLogicMethod logicMethod = createLogicMethod(name,method);
			actions.put(name, logicMethod);
		}
		return actions;
	}

	private DefaultLogicMethod createLogicMethod(String name, Method method) {
		List<MethodParameter> parameters = paramInfo.provideFor(method);
		return new DefaultLogicMethod(null, name, method, null, parameters);
	}

	private Map<String,Method> findExtendsMethod(Class<?> type){
		Map<String,Method> methods = new HashMap<String,Method>();
		for (Method method : type.getMethods()) {
			if (isNotExtendLogicMethod(method)) {
				continue;
			}
			Extends annotation = method.getAnnotation(Extends.class);
			if(annotation.value().length==0){
				methods.put(method.getName(), method);
			}else{
				for (String name : annotation.value()) {
					methods.put(name, method);
				}
			}
		}
		return methods;
	}

	private boolean isNotExtendLogicMethod(Method m) {
		return !Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers())
		|| m.getDeclaringClass().equals(Object.class)
		|| m.getName().startsWith(DefaultComponentManager.VALIDATE_METHOD_INITIALS)
		|| ReflectionUtil.isGetter(m) || !m.isAnnotationPresent(Extends.class);
	}


	private String getComponentName(Class<?> type) {
		String componentName;

		if (type.isAnnotationPresent(Component.class)) {
			Component ann = (type.getAnnotation(Component.class));
			if (!ann.value().equals("")) {
				componentName = ann.value();
			} else {
				componentName = getAutoName(type);
			}
		} else {
			componentName = type.getSimpleName();
		}
		return componentName;
	}

	private String getExtendedComponentName(Class<?> type){
		String componentName;
		if(type.isAnnotationPresent(ActionExtension.class)){
			ActionExtension ann = (type.getAnnotation(ActionExtension.class));
			if (!ann.value().equals("")) {
				componentName = ann.value();
			} else {
				componentName = getAutoName(type);
			}
		}else{
			componentName = type.getSimpleName();
		}
		return componentName;
	}

	private String getAutoName(Class<?> type){
		String name = StringUtil.removeEnding(type.getSimpleName(),
				DefaultComponentManager.COMPONENT_TERMINATIONS);

		if (!name.equals(type.getSimpleName())) {
			return name.toLowerCase();
		} else {
			return type.getSimpleName();
		}
	}

	private void register(String component,String logicMethodName,LogicMethod method)  {
		List<LogicMethod> methods = this.getExtendedLogicMethod(component, logicMethodName);
		methods.add(method);

		if (!extensions.containsKey(component)) {
			extensions.put(component, new ConcurrentHashMap<String, List<LogicMethod>>());
		}

		extensions.get(component).put(logicMethodName, methods);
	}

	private List<LogicMethod> getExtendedLogicMethod(String preComponentName,
			String preLogicMethodName) {
		List<LogicMethod> methods;

		ConcurrentMap<String, List<LogicMethod>>  _logicMethod=  extensions.get(preComponentName);
		if(_logicMethod == null)
			return new ArrayList<LogicMethod>();

		methods = _logicMethod.get(preLogicMethodName);
		if(methods == null)
			methods = new ArrayList<LogicMethod>();

		return methods;
	}

}
