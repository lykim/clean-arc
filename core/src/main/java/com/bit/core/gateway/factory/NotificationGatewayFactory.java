package com.bit.core.gateway.factory;

import com.bit.core.config.ApplicationConfig;
import com.bit.core.gateway.NotificationGateway;

public enum NotificationGatewayFactory {

	EMAIL_NOTIFICATION(){
		@Override
		public NotificationGateway<?> get() {
			return getApplicationConfig().getGatewayConfig().getEmailGateway();
		}
		
	};
	public abstract NotificationGateway<?> get(); 
	private static ApplicationConfig getApplicationConfig() {
		return ApplicationConfig.getInstance();
	}
}
