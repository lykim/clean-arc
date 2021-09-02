package com.bit.core.usecase;

import org.junit.jupiter.api.AfterAll;

import com.bit.core.config.ApplicationConfig;
import com.bit.core.gateway.BhaktiApiGateway;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.UserGateway;

public abstract class BaseUsecaseTest {
	protected static UserGateway userGateway;
	protected static UnitKpiGateway unitKpiGateway;
	protected static BhaktiApiGateway bhaktiApiGateway;
	protected static KpiGateway kpiGateway;
	protected static RoleGateway roleGateway;
	protected static RoleGroupGateway roleGroupGateway;

	static{
//		getApplicationConfig().addGatewayConfig("DEV", new MysqlGatewayConfig());
		getApplicationConfig().setGateways();
		userGateway =  getApplicationConfig().getGatewayConfig().getUserGateway();
		unitKpiGateway = getApplicationConfig().getGatewayConfig().getUnitKpiGateway();
		bhaktiApiGateway = getApplicationConfig().getGatewayConfig().getBhaktiApiGateway();
		kpiGateway = getApplicationConfig().getGatewayConfig().getKpiGateway();
		roleGateway = getApplicationConfig().getGatewayConfig().getRoleGateway();
		roleGroupGateway = getApplicationConfig().getGatewayConfig().getRoleGroupGateway();
	}
	@AfterAll
	public static void afterAll() {
		getApplicationConfig().cleanGatewayStoreage();
	}
	private static ApplicationConfig getApplicationConfig() {
		return ApplicationConfig.getInstance();
	}
}
