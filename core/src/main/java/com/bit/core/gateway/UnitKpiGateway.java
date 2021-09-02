package com.bit.core.gateway;

import com.bit.core.entity.UnitKpi;

public interface UnitKpiGateway extends CrudGateway<UnitKpi, String>, ActiveableGateway<UnitKpi, String>{
	UnitKpi findByName(String name);
}
