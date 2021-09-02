package com.bit.core.strategy.findMany;

import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindUnitKpis extends FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel>{
	UnitKpiGateway gateway;
	public FindUnitKpis(FindManyUnitKpiRequestModel param) {
		this.gateway = (UnitKpiGateway)GatewayFactory.UNIT_KPI_GATEWAY.get();
		this.param = param;
	}
	@Override
	public Page<UnitKpi> find() {
		return gateway.find(param);
	}

}

