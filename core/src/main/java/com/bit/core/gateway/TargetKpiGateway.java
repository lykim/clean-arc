package com.bit.core.gateway;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.response.base.Page;

public interface TargetKpiGateway extends Gateway{
	Page<Salesman> getTargetKpi(FindManyRequestModel requestModel);
}
