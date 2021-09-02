package com.bit.core.usecase.kpi;

import static com.bit.core.utils.ValidationUtils.setEmptyValidator;
import static com.bit.core.utils.ValidationUtils.setNullValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.KeyPerformanceIndicator;
import com.bit.core.entity.KpiPeriod;
import com.bit.core.entity.Salesman;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanTargetKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;

public class CreateDraftTargetKpiSalesmanUsecase  extends CommonTemplateUsecase<SalesmanKpiRequestModel, CreateResponseModel>{

	KpiGateway gateway;
	Salesman salesman;
	public CreateDraftTargetKpiSalesmanUsecase(SalesmanKpiRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_TARGET_SALESMAN_KPI_CUD);
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		setEmptyValidator(validator, SalesmanKpiRequestModel.Label.ID, requestModel.id);
		setNullValidator(validator, SalesmanKpiRequestModel.Label.PERIOD, requestModel.period);
	}

	@Override
	protected void doBussinessProcess() {
		gateway.updateSalesmanTargetKpi(salesman);
		responseModel.id = salesman.getId();
	}

	@Override
	protected void parseRequestModelToEntity() {
		preParse();
		salesman.setKpiPeriods(generateKpiPeriods());
	}
	
	private List<KpiPeriod> generateKpiPeriods(){
		KpiPeriod period = generateKpiPeriod();
		List<KpiPeriod> kpiPeriods = new ArrayList<>();
		kpiPeriods.add(period);
		return kpiPeriods;
	}
	
	private KpiPeriod generateKpiPeriod() {
		KpiPeriod period = new KpiPeriod();
		period.setPeriod(requestModel.period);
		period.setKpiStatus(TargetKpiStatus.DRAFT);
		period.setTargetKpis(generateTargetKpis());
		return period;
	}
	
	private List<KeyPerformanceIndicator> generateTargetKpis(){
		List<KeyPerformanceIndicator> requestTargetKpis = new ArrayList<>();
		for(SalesmanTargetKpiRequestModel targetRequest : requestModel.targetKpiSalesman) {
			requestTargetKpis.add(parseRequestToKpi(targetRequest));
		}
		return requestTargetKpis;
	}
	
	private void preParse() {
		salesman = gateway.getSalesmanById(requestModel.id);
		checkSalesman();
		checkStatus();
		checkTotalBobot();
	}
	
	private void checkSalesman() {
		if(salesman == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
	}
	
	private void checkStatus() {
		Optional<KpiPeriod> optional =  salesman.getKpiPeriods().stream()
				.filter(kpiPeriod -> kpiPeriod.getPeriod()!= null && kpiPeriod.getPeriod().equals(requestModel.period)).findAny();
		if(optional.isPresent()) {
			KpiPeriod selectedPeriod = optional.get();
			if(selectedPeriod.getKpiStatus() != TargetKpiStatus.DRAFT) throw new RuntimeException(ErrorMessage.STATUS_IS_NOT_DRAFT);
		}
	}
	
	
	private KeyPerformanceIndicator parseRequestToKpi(SalesmanTargetKpiRequestModel request) {
		KeyPerformanceIndicator kpi = new KeyPerformanceIndicator();
		kpi.setBobot(request.bobot);
		kpi.setTarget(request.target);
		kpi.setTargetPoint(request.targetPoint);
		UnitKpi unitKpi = new UnitKpi();
		unitKpi.setId(request.unitKpiId);
		kpi.setUnitKpi(unitKpi);
		return kpi;
	}
	
	private void checkTotalBobot() {
		double totalBobot = 0;
		if(!CollectionUtils.isEmpty(requestModel.targetKpiSalesman)) {
			totalBobot =  requestModel.targetKpiSalesman.stream().mapToDouble(data -> data.bobot).sum();		
		}
		if(totalBobot != KeyPerformanceIndicator.BOBOT_MAXIMUM) {
			throw new RuntimeException(ErrorMessage.BOBOT_NOT_MAX);
		}
	}

}
