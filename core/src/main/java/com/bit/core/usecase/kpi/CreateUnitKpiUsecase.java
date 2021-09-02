package com.bit.core.usecase.kpi;

import static com.bit.core.utils.ValidationUtils.setEmptyValidator;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;

public class CreateUnitKpiUsecase extends CommonTemplateUsecase<UnitKpiRequestModel, CreateResponseModel>{

	UnitKpiGateway gateway;
	UnitKpi unitKpi;
	public CreateUnitKpiUsecase(UnitKpiRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (UnitKpiGateway)GatewayFactory.UNIT_KPI_GATEWAY.get();
		List<String> unitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		authorize = new Authorizeable(unitKpis);
	}
	@Override
	protected void setupValidation() {
		setEmptyValidator(validator, UnitKpiRequestModel.Label.NAME, requestModel.name);
	}

	@Override
	protected void doBussinessProcess() {
		checkNameAvailability();
		this.gateway.save(unitKpi);
		responseModel.id = unitKpi.getId();
	}

	@Override
	protected void parseRequestModelToEntity() {
		unitKpi = new UnitKpi();
		unitKpi.setName(requestModel.name.trim());
		unitKpi.setDescription(requestModel.description);
		
	}
	private void checkNameAvailability() {
		UnitKpi entity = null;
		try {
			entity = gateway.findByName(requestModel.name);			
		}catch (Exception e) {}
		if(entity != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);
	}

}

