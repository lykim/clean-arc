package com.bit.core.model.request;

import com.bit.core.model.request.base.RequestModel;

public class RoleRequestModel extends RequestModel{
	public static class Label{
		public static String CODE = "code";
		public static String DESCRIPTION = "description";
	}
	public String code;
	public String description;
}
