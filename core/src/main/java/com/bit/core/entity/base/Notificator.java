package com.bit.core.entity.base;

import java.util.Set;

import com.bit.core.entity.User;
import com.bit.core.response.base.NotificationResponse;

public abstract class Notificator {
	protected String type;
	
	public abstract NotificationResponse send(Set<User> receivers);

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	} 
}
