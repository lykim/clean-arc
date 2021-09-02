package com.bit.core.model.request;

import java.util.Set;

import com.bit.core.model.request.base.RequestModel;

public class RoleGroupRequestModel extends RequestModel{
	public static class Label{
		public static String NAME = "name";
		public static String DESCRIPTION = "description";
		public static String ROLE_IDS = "role ids";
	}
	public String name;
	public String description;
	public Set<String> roleIds;
}
