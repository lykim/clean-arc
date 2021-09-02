package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.strategy.findMany.FindUnitKpis;
import com.bit.core.strategy.findOne.FindUnitKpiById;
import com.bit.core.usecase.ActiveInactiveUsecase;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.kpi.CreateUnitKpiUsecase;
import com.bit.core.usecase.kpi.UpdateUnitKpiUsecase;

public class UnitKpiPresenterImpl implements UnitKpiPresenter{

	@Override
	public CreateResponseModel create(UnitKpiRequestModel request) {
		CreateUnitKpiUsecase usecase = new CreateUnitKpiUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(UnitKpiRequestModel request) {
		UpdateUnitKpiUsecase usecase = new UpdateUnitKpiUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<UnitKpi> findById(UnitKpiRequestModel request) {
		FindOneStrategy<UnitKpi, UnitKpiRequestModel> findOneStrategy = new FindUnitKpiById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<UnitKpi>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<UnitKpi>)usecase.getResponseModel();
	}

	@Override
	public ResponseModel setActive(ActiveInactiveRequestModel requestModel) {
		OnOffStrategy<?, ?, ?> onOffAble = new OnOffStrategy<>(requestModel.id, (UnitKpiGateway)GatewayFactory.UNIT_KPI_GATEWAY.get());
		List<String> roles = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public FindManyResponseModel<UnitKpi> findAll(FindManyUnitKpiRequestModel requestModel) {
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<UnitKpi>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

}
