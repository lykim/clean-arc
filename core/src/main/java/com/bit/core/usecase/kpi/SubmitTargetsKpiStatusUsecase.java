package com.bit.core.usecase.kpi;

import com.bit.core.constant.ApproverModule;
import com.bit.core.entity.Salesman;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.response.NotificationResponseModel;
import com.bit.core.usecase.base.template.NotificationTemplateUsecase;
import com.bit.core.validator.EmptyCollectionValidator;
import com.bit.core.validator.NullValidator;

public class SubmitTargetsKpiStatusUsecase extends NotificationTemplateUsecase<SalesmanKpiRequestModel, NotificationResponseModel> {

	KpiGateway gateway;
	Salesman salesman;
	public SubmitTargetsKpiStatusUsecase(SalesmanKpiRequestModel requestModel) {
		super(ApproverModule.TARGET_KPI);
		this.requestModel = requestModel;
		this.responseModel = new NotificationResponseModel();
		gateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyCollectionValidator<>(SalesmanKpiRequestModel.Label.IDS, requestModel.ids));		
		validator.setNextValidator(new NullValidator(SalesmanKpiRequestModel.Label.PERIOD, requestModel.period));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.submitTargets(requestModel.ids, requestModel.period);
	}

	@Override
	protected void parseRequestModelToEntity() {
		// TODO Auto-generated method stub
		
	}

}
