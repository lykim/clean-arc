package com.bit.core.presenter;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindManyTargetKpiApproval;
import com.bit.core.usecase.FindManyUsecase;

public class KpiPeriodPresenterImpl implements KpiPeriodPresenter{

	@Override
	public FindManyResponseModel<Salesman> findForApprover(FindManyRequestModel requestModel) {
		FindManyStrategy<Salesman, FindManyRequestModel> findManyStrategy = new FindManyTargetKpiApproval(requestModel);
		FindManyUsecase<?,?,FindManyResponseModel<Salesman>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy, null);
		usecase.run();
		return usecase.getResponseModel();
	}

}
