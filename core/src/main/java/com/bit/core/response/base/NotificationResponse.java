package com.bit.core.response.base;

import java.util.HashMap;
import java.util.Map;

public class NotificationResponse {
	public boolean isAllSuccess;
	public Map<String,String> errorMessages = new HashMap<>();
}
