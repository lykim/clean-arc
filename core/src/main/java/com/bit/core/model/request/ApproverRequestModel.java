package com.bit.core.model.request;

import java.util.List;

import com.bit.core.model.request.base.RequestModel;

public class ApproverRequestModel extends RequestModel{
	public final class Label{
		public static final String type = "type";
		public static final String module = "module";
		public static final String approverIds = "approverIds";
		public static final String notificatorTypes = "notificatorTypes";
	}
	
	public String type;
	public String module;
	public List<String> approverIds;
	public List<String> notificatorTypes;
}
