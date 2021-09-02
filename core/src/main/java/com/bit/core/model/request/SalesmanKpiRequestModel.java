package com.bit.core.model.request;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import com.bit.core.model.request.base.RequestModel;

public class SalesmanKpiRequestModel extends RequestModel{
	public static class Label{
		public static String SALESMAN_CODE = "Salesman Code";
		public static String TARGET_KPI__STATUS_CODE = "target kpi status code";
		public static String IDS = "ids";
		public static String ID = "id";
		public static String PERIOD = "period";
	}
	public String salesmanCode;
	public List<SalesmanTargetKpiRequestModel> targetKpiSalesman;
	public String targetKpiStatusCode;
	public Set<String> ids;
	public YearMonth period;
}
