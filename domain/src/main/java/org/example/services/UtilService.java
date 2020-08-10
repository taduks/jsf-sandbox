package org.example.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service("utilService")
@Order(1)
public class UtilService implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	private int lastTick = 0;
	
	public int getNextTick() {
		lastTick++;
		return lastTick;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
