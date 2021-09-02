package com.bit.core.strategy.findOne;

import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindUnitKpiById extends FindOneStrategy<UnitKpi, UnitKpiRequestModel>{
	UnitKpiGateway gateway;
	public FindUnitKpiById(UnitKpiRequestModel param) {
		this.gateway = (UnitKpiGateway)GatewayFactory.UNIT_KPI_GATEWAY.get();
		this.param = param;
	}
	
	@Override
	public UnitKpi findOne() {
		return gateway.findById(param.id);
	}
}
