package com.bit.core.strategy.findOne;

import com.bit.core.entity.Salesman;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindSalesmanByCode extends FindOneStrategy<Salesman, SalesmanRequestModel>{
	KpiGateway gateway;
	public FindSalesmanByCode(SalesmanRequestModel param) {
		this.gateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
		this.param = param;
	}
	
	@Override
	public Salesman findOne() {
		return gateway.getSalesmanByCode(param.salesmanCode);
	}
}
