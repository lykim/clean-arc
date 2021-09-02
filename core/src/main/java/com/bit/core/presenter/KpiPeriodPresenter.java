package com.bit.core.presenter;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.response.FindManyResponseModel;

public interface KpiPeriodPresenter {
	FindManyResponseModel<Salesman> findForApprover(FindManyRequestModel requestModel);
}
