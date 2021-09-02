package com.bit.core.strategy.findMany;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.RoleCode;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.Salesman;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.usecase.FindManyUsecase;

public class FindSubordinateSalesman extends FindManyStrategy<Salesman, SalesmanRequestModel>{

	public FindSubordinateSalesman(SalesmanRequestModel requestModel) {
		this.param = requestModel;
	}
	
	@Override
	public Page<Salesman> find() {
		List<Salesman> salesmansFromApi = getSubordinateSalesmanFromApi();
		syncSalesmanApiToPersistent(salesmansFromApi);
		KpiGateway kpiGateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
		return kpiGateway.getSubordinateSalesmansBySalesmanCode(param);
	}
	
	private List<Salesman> getSubordinateSalesmanFromApi(){
		List<String> viewRoles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesmanFromApi(this.param);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(this.param, new FindManyResponseModel<Salesman>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Salesman> response = (FindManyResponseModel<Salesman>)usecase.getResponseModel();
		return response.page.getResult();
	}
	private void syncSalesmanApiToPersistent(List<Salesman> salesmans) {
		KpiGateway kpiGateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
		Set<String> salesmanCodes = salesmans.stream().map(salesman -> salesman.getCode()).collect(Collectors.toSet());
		Set<Salesman> persistedSalesmans = kpiGateway.getSalesmanByCodes(salesmanCodes);
		Set<Salesman> unPersistedSalesmans = salesmans.stream().filter(salesman -> { 
			Optional<Salesman> opt = persistedSalesmans.stream().filter(persist -> persist.getCode().equals(salesman.getCode())).findAny();
			return opt.isEmpty();
		}).map(salesman -> {
//			salesman.setTargetKpiStatus(TargetKpiStatus.DRAFT);
			return salesman;
		})
		.collect(Collectors.toSet());
		Set<Salesman> needUpdateSalesman = salesmans.stream().filter(salesman -> { 
			Optional<Salesman> opt = persistedSalesmans.stream().filter(persist -> persist.getCode().equals(salesman.getCode())).findAny();
			return opt.isPresent();
		}).collect(Collectors.toSet());
		unPersistedSalesmans.forEach(salesman -> kpiGateway.createSalesman(salesman));
		needUpdateSalesman.forEach(salesman -> kpiGateway.updateSalesman(salesman));
	}
}
