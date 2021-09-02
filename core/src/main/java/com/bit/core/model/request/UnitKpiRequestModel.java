package com.bit.core.model.request;

import com.bit.core.model.request.base.RequestModel;

public class UnitKpiRequestModel extends RequestModel{
	public static class Label{
		public static String NAME = "name";
		public static String DESCRIPTION = "description";
	}
	public String name;
	public String description;
}
