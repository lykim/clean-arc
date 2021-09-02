package com.bit.persistent;

import com.bit.core.config.GatewayConfig;
import com.bit.core.mocks.gateway.MockBhaktiApiGateway;
import com.bit.core.mocks.gateway.MockKpiGateway;
import com.bit.persistent.gateway.ApproverGatewayImpl;
import com.bit.persistent.gateway.KpiGatewayImpl;
import com.bit.persistent.gateway.LoginGatewayImpl;
import com.bit.persistent.gateway.RoleGatewayImpl;
import com.bit.persistent.gateway.RoleGroupGatewayImpl;
import com.bit.persistent.gateway.UnitKpiGatewayImpl;
import com.bit.persistent.gateway.UserGatewayImpl;
import com.bit.plugins.EmailGatewayImpl;

public class MysqlGatewayConfig extends GatewayConfig{

	@Override
	protected void setGateways() {
		userGateway = UserGatewayImpl.getInstance();
		loginGateway = LoginGatewayImpl.getInstance();
		bhaktiApiGateway = new MockBhaktiApiGateway();
		roleGateway = RoleGatewayImpl.getInstance();
		roleGroupGateway = RoleGroupGatewayImpl.getInstance();
		unitKpiGateway = UnitKpiGatewayImpl.getInstance();
		kpiGateway = KpiGatewayImpl.getInstance();
		approveableGateway = ApproverGatewayImpl.getInstance();
		emailGateway = EmailGatewayImpl.getInstance();
		targetKpiGateway = KpiGatewayImpl.getInstanceTargetKpi();
	}

	@Override
	protected void cleanStoreage() {
		userGateway.clean();
		loginGateway.clean();
		bhaktiApiGateway.clean();
		roleGroupGateway.clean();
		roleGateway.clean();
		unitKpiGateway.clean();
		kpiGateway.clean();
		approveableGateway.clean();
	}

}
