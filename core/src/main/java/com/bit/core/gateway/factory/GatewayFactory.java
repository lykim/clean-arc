package com.bit.core.gateway.factory;

import com.bit.core.config.ApplicationConfig;
import com.bit.core.gateway.Gateway;

public enum GatewayFactory {
	APPROVEABLE_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getApproveableGateway();
		}
	},
	LOGIN_GATEWAY {
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getLoginGateway();
		}
	},
	BHAKTI_API_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getBhaktiApiGateway();
		}
	}, KPI_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getKpiGateway();
		}
	},USER_GATEWAY {
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getUserGateway();
		}
	},
	ROLE_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getRoleGateway();
		}
	},
	  ROLE_GROUP_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getRoleGroupGateway();
		}
	}, UNIT_KPI_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getUnitKpiGateway();
		}
	}, TARGET_KPI_GATEWAY{
		@Override
		public Gateway get() {
			return  getApplicationConfig().getGatewayConfig().getTargetKpiGateway();
		}
	};
	public abstract Gateway get(); 
	private static ApplicationConfig getApplicationConfig() {
		return ApplicationConfig.getInstance();
	}
	
}
