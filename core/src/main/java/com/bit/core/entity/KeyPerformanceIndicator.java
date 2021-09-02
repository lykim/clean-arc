package com.bit.core.entity;

import java.time.Instant;
import java.time.YearMonth;

import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.base.Entity;

public class KeyPerformanceIndicator extends Entity{
	public final static double BOBOT_MAXIMUM = 100;
	private double bobot;
	private int targetPoint;
	private long target;
	private UnitKpi unitKpi;
	public double getBobot() {
		return bobot;
	}
	public void setBobot(double bobot) {
		this.bobot = bobot;
	}
	public int getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(int targetPoint) {
		this.targetPoint = targetPoint;
	}
	public long getTarget() {
		return target;
	}
	public void setTarget(long target) {
		this.target = target;
	}
	public UnitKpi getUnitKpi() {
		return unitKpi;
	}
	public void setUnitKpi(UnitKpi unitKpi) {
		this.unitKpi = unitKpi;
	}
}
