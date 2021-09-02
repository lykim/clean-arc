package com.bit.core.config;

import java.util.HashMap;
import java.util.Map;

import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.BhaktiApiGateway;
import com.bit.core.gateway.EmailGateway;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.LoginGateway;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.TargetKpiGateway;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.UserGateway;

public abstract class GatewayConfig {
	protected EmailGateway emailGateway;
	protected UserGateway userGateway;
	protected LoginGateway loginGateway;
	protected BhaktiApiGateway bhaktiApiGateway;
	protected RoleGateway roleGateway;
	protected RoleGroupGateway roleGroupGateway;
	protected UnitKpiGateway unitKpiGateway;
	protected KpiGateway kpiGateway;
	protected ApproverGateway approveableGateway;
	protected TargetKpiGateway targetKpiGateway;
	protected abstract void setGateways();
	protected abstract void cleanStoreage();
	
	public static Map<String, GatewaysSetter> gatewaysSetterMap = new HashMap<>();
	public UserGateway getUserGateway() {
		return userGateway;
	}
	public LoginGateway getLoginGateway() {
		return loginGateway;
	}
	public BhaktiApiGateway getBhaktiApiGateway() {
		return bhaktiApiGateway;
	}
	public RoleGateway getRoleGateway() {
		return roleGateway;
	}
	public RoleGroupGateway getRoleGroupGateway() {
		return roleGroupGateway;
	}
	public UnitKpiGateway getUnitKpiGateway() {
		return unitKpiGateway;
	}
	public KpiGateway getKpiGateway() {
		return kpiGateway;
	}
	public static Map<String, GatewaysSetter> getGatewaysSetterMap() {
		return gatewaysSetterMap;
	}
	public ApproverGateway getApproveableGateway() {
		return approveableGateway;
	}
	public EmailGateway getEmailGateway() {
		return emailGateway;
	}
	public TargetKpiGateway getTargetKpiGateway() {
		return targetKpiGateway;
	}
	
}
