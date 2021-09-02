package com.bit.core.usecase.kpi;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateUnitKpiUsecase extends CommonTemplateUsecase<UnitKpiRequestModel, ResponseModel>{

	private UnitKpiGateway gateway;
	private UnitKpi unitKpi;
	public UpdateUnitKpiUsecase(UnitKpiRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		gateway = (UnitKpiGateway)GatewayFactory.UNIT_KPI_GATEWAY.get();
		authorize = new Authorizeable(null);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.update(unitKpi, requestModel.id);
	}

	@Override
	protected void parseRequestModelToEntity() {
		unitKpi = getUnitKpiById();
		if(StringUtils.isNotEmpty(requestModel.name) && isNameNeedToSet(unitKpi)) {
			unitKpi.setName(requestModel.name);				
		}
		if(StringUtils.isNotEmpty(requestModel.description)) {
			unitKpi.setDescription(requestModel.description);
		}
	}
	private boolean isNameNeedToSet(UnitKpi unitKpiInStorage) {
		if(unitKpiInStorage.getName().equals(requestModel.name)) {
			return false;
		}
		checkUnitKpiCodeAvailability();
		return true;
	}
	private UnitKpi getUnitKpiById() {
		UnitKpi unitKpi = gateway.findById(requestModel.id);
		if(unitKpi == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
		return unitKpi;
	}
	private void checkUnitKpiCodeAvailability() {
		UnitKpi unitKpiLocal = gateway.findByName(requestModel.name);
		if(unitKpiLocal != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);			
	}
}
