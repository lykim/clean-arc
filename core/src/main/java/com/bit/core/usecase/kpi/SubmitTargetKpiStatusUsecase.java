package com.bit.core.usecase.kpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.KpiPeriod;
import com.bit.core.entity.Salesman;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.response.NotificationResponseModel;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.usecase.base.template.NotificationTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.validator.NullValidator;
import com.bit.core.validator.string.EmptyValidator;

public class SubmitTargetKpiStatusUsecase extends NotificationTemplateUsecase<SalesmanKpiRequestModel, NotificationResponseModel>{

	KpiGateway gateway;
	Salesman salesman;
	public SubmitTargetKpiStatusUsecase(SalesmanKpiRequestModel requestModel) {
		super(ApproverModule.TARGET_KPI);
		this.requestModel = requestModel;
		this.responseModel = new NotificationResponseModel();
		gateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
	}
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(SalesmanKpiRequestModel.Label.ID, requestModel.id));
		validator.setNextValidator(new NullValidator(SalesmanKpiRequestModel.Label.PERIOD, requestModel.period));
		
	}

	@Override
	protected void doBussinessProcess() {
		gateway.updateTargetKpiStatus(salesman);
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
		period.setKpiStatus(TargetKpiStatus.SUBMITTED);
		return period;
	}
	
	private void preParse() {
		setSalesmanById();
		checkSalesman();
		Optional<KpiPeriod> optionalKpiPeriod = getOptionalOfPeriod();
		if(optionalKpiPeriod.isPresent()) {
			checkTargetKpis(optionalKpiPeriod.get());
			checkStatus(optionalKpiPeriod.get());
		}else {
			checkTargetKpis(null);
		}
	}
	
	private void checkTargetKpis(KpiPeriod selectedPeriod) {
		if(selectedPeriod == null) throw new RuntimeException(ErrorMessage.TARGET_KPI_IS_EMPTY);
		if(CollectionUtils.isEmpty(selectedPeriod.getTargetKpis())) throw new RuntimeException(ErrorMessage.TARGET_KPI_IS_EMPTY);
	}
	
	private Optional<KpiPeriod> getOptionalOfPeriod(){
		return  salesman.getKpiPeriods().stream()
				.filter(kpiPeriod -> kpiPeriod.getPeriod()!= null && kpiPeriod.getPeriod().equals(requestModel.period)).findAny();		
	}
	
	private void checkSalesman() {
		if(salesman == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
	}
	private void checkStatus(KpiPeriod selectedPeriod) {
		if(selectedPeriod.getKpiStatus() != TargetKpiStatus.DRAFT) throw new RuntimeException(ErrorMessage.STATUS_IS_NOT_DRAFT);
	}
	private void setSalesmanById() {
		salesman = gateway.getSalesmanById(this.requestModel.id);
	}
}
