package com.bit.core.gateway;

import java.time.YearMonth;
import java.util.Set;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.base.Page;

public interface KpiGateway extends Gateway{
	Set<Salesman> getSalesmanByCodes(Set<String> id);
	
	Salesman getSalesmanByCode(String code);
	
	Salesman getSalesmanById(String code);

	void createSalesman(Salesman salesman);

	void updateSalesman(Salesman salesman);

	Page<Salesman> getSubordinateSalesmansBySalesmanCode(SalesmanRequestModel requestModel);

	void updateSalesmanTargetKpi(Salesman salesman);
	
	void updateTargetKpiStatus(Salesman salesman);
	
	void submitTargets(Set<String> ids, YearMonth period);
}
