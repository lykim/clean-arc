package com.bit.core.presenter;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface TargetKpiPresenter {
	FindManyResponseModel<Salesman> findAllSubordinateByCode(SalesmanRequestModel requestModel);
	ResponseModel update(SalesmanKpiRequestModel request);
	DetailResponseModel<Salesman> findByCode(SalesmanRequestModel request);
	ResponseModel updateTargetKpiStatus(SalesmanKpiRequestModel request);
	ResponseModel submitTargetsKpis(SalesmanKpiRequestModel request);
	DetailResponseModel<Salesman> findById(SalesmanRequestModel request);
}
