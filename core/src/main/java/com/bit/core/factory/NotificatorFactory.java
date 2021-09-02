package com.bit.core.factory;

import com.bit.core.constant.NotificatorType;
import com.bit.core.entity.EmailNotificator;
import com.bit.core.entity.base.Notificator;

public class NotificatorFactory {
	public static Notificator get(NotificatorType type) {
		if(type == NotificatorType.EMAIL) return new EmailNotificator();
		return null;
	}
}
