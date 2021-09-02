package com.bit.core.config;

import com.bit.core.mocks.gateway.MockApproverGateway;
import com.bit.core.mocks.gateway.MockBhaktiApiGateway;
import com.bit.core.mocks.gateway.MockEmailGateway;
import com.bit.core.mocks.gateway.MockKpiGateway;
import com.bit.core.mocks.gateway.MockLoginGateway;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.mocks.gateway.MockUnitKpiGateway;
import com.bit.core.mocks.gateway.MockUserGateway;

public class TestGatewayConfig extends GatewayConfig{

	@Override
	public void setGateways() {
		userGateway = MockUserGateway.getInstance();
		loginGateway = MockLoginGateway.getInstance();
		bhaktiApiGateway = new MockBhaktiApiGateway();
		roleGateway = MockRoleGateway.getInstance();
		roleGroupGateway = MockRoleGroupGateway.getInstance();
		unitKpiGateway = MockUnitKpiGateway.getInstance();
		kpiGateway = MockKpiGateway.getInstance();
		emailGateway = MockEmailGateway.getInstance();
		approveableGateway = MockApproverGateway.getInstance();
		targetKpiGateway = MockKpiGateway.getInstanceTargetKpi();
	}

	@Override
	public void cleanStoreage() {
		userGateway.clean();
		loginGateway.clean();
		bhaktiApiGateway.clean();
		roleGateway.clean();
		roleGroupGateway.clean();
		unitKpiGateway.clean();
		kpiGateway.clean();
		approveableGateway.clean();
	}

}
