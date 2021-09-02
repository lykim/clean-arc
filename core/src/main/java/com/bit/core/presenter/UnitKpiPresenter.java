package com.bit.core.presenter;

import com.bit.core.entity.UnitKpi;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface UnitKpiPresenter {
	CreateResponseModel create(UnitKpiRequestModel request);
	ResponseModel update(UnitKpiRequestModel request);
	DetailResponseModel<UnitKpi> findById(UnitKpiRequestModel request);
	ResponseModel setActive(ActiveInactiveRequestModel requestModel);
	FindManyResponseModel<UnitKpi> findAll(FindManyUnitKpiRequestModel requestModel);
}
