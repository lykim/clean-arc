package com.bit.core.entity;

import java.util.ArrayList;
import java.util.List;

import com.bit.core.entity.base.Entity;

public class Salesman extends Entity{
	private String code;
	private String name;
	private String division;
	private String description;
	private int level;
	private String levelName;
	private String email;
	private String supervisorCode;
	private String area;
	private String branchCode;
	private String branchName;
	private List<KpiPeriod> kpiPeriods = new ArrayList<>();
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSupervisorCode() {
		return supervisorCode;
	}
	public void setSupervisorCode(String supervisorCode) {
		this.supervisorCode = supervisorCode;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public List<KpiPeriod> getKpiPeriods() {
		return kpiPeriods;
	}
	public void setKpiPeriods(List<KpiPeriod> kpiPeriods) {
		this.kpiPeriods = kpiPeriods;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Salesman other = (Salesman) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
}
