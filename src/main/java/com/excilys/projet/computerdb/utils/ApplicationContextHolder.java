package com.excilys.projet.computerdb.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {

	private static ApplicationContext context;
	
	static {
		new ClassPathXmlApplicationContext("spring-context.xml").registerShutdownHook();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static ApplicationContext getContext() {
		return context;
	}
}
