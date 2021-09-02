package com.bit.core.entity;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.base.Entity;

public class KpiPeriod extends Entity{
	private YearMonth period;
	private TargetKpiStatus kpiStatus;
	private List<KeyPerformanceIndicator> targetKpis = new ArrayList<>();
	private List<KeyPerformanceIndicator> achievementKpis = new ArrayList<>();
	private Salesman salesman;
	
	public List<KeyPerformanceIndicator> getTargetKpis() {
		return targetKpis;
	}
	public void setTargetKpis(List<KeyPerformanceIndicator> targetKpis) {
		this.targetKpis = targetKpis;
	}
	public List<KeyPerformanceIndicator> getAchievementKpis() {
		return achievementKpis;
	}
	public void setAchievementKpis(List<KeyPerformanceIndicator> achievementKpis) {
		this.achievementKpis = achievementKpis;
	}
	public YearMonth getPeriod() {
		return period;
	}
	public void setPeriod(YearMonth period) {
		this.period = period;
	}
	public TargetKpiStatus getKpiStatus() {
		return kpiStatus;
	}
	public void setKpiStatus(TargetKpiStatus kpiStatus) {
		this.kpiStatus = kpiStatus;
	}
	public Salesman getSalesman() {
		return salesman;
	}
	public void setSalesman(Salesman salesman) {
		this.salesman = salesman;
	}
	
}
