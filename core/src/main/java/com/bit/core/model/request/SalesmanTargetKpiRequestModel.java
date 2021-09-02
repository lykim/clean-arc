package com.bit.core.model.request;

import java.time.YearMonth;

public class SalesmanTargetKpiRequestModel {
	public static class Label{
		public static String UNIT_KPI_ID = "unit kpi id";
		public static String TARGET = "target";
		public static String TARGET_POINT = "target point";
		public static String BOBOT = "bobot";
	}
	public String unitKpiId;
	public long target;
	public int targetPoint;
	public double bobot;
}
