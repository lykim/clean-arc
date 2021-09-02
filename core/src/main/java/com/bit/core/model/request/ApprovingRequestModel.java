package com.bit.core.model.request;

import java.util.List;

import com.bit.core.model.request.base.RequestModel;

public class ApprovingRequestModel  extends RequestModel{
	public final class Label{
//		public static final String type = "type";
		public static final String module = "module";
		public static final String idToApprove = "idToApprove";
	}
//	public String type;
	public String module;
	public String idToApprove;
}
