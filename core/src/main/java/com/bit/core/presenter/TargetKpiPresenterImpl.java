package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Salesman;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findMany.FindSubordinateSalesman;
import com.bit.core.strategy.findOne.FindSalesmanByCode;
import com.bit.core.strategy.findOne.FindSalesmanById;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.kpi.CreateDraftTargetKpiSalesmanUsecase;
import com.bit.core.usecase.kpi.SubmitTargetKpiStatusUsecase;
import com.bit.core.usecase.kpi.SubmitTargetsKpiStatusUsecase;

public class TargetKpiPresenterImpl implements TargetKpiPresenter{

	@Override
	public FindManyResponseModel<Salesman> findAllSubordinateByCode(SalesmanRequestModel requestModel) {
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesman(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<Salesman>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(SalesmanKpiRequestModel request) {
		CreateDraftTargetKpiSalesmanUsecase usecase = new CreateDraftTargetKpiSalesmanUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<Salesman> findByCode(SalesmanRequestModel request) {
		FindOneStrategy<Salesman, SalesmanRequestModel> findOneStrategy = new FindSalesmanByCode(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Salesman>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<Salesman>)usecase.getResponseModel();
	}

	@Override
	public ResponseModel updateTargetKpiStatus(SalesmanKpiRequestModel request) {
		SubmitTargetKpiStatusUsecase usecase = new SubmitTargetKpiStatusUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel submitTargetsKpis(SalesmanKpiRequestModel request) {
		SubmitTargetsKpiStatusUsecase usecase = new SubmitTargetsKpiStatusUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<Salesman> findById(SalesmanRequestModel request) {
		FindOneStrategy<Salesman, SalesmanRequestModel> findOneStrategy = new FindSalesmanById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Salesman>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<Salesman>)usecase.getResponseModel();
	}

}
