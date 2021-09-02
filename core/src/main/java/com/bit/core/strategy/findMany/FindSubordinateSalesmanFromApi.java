package com.bit.core.strategy.findMany;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.Salesman;
import com.bit.core.entity.User;
import com.bit.core.gateway.BhaktiApiGateway;
import com.bit.core.gateway.KpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.presenter.UserPresenter;
import com.bit.core.presenter.UserPresenterImpl;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindSubordinateSalesmanFromApi extends FindManyStrategy<Salesman, SalesmanRequestModel>{
	
	public FindSubordinateSalesmanFromApi(SalesmanRequestModel requestModel) {
		this.param = requestModel;
	}
	
	@Override
	public Page<Salesman> find() {
		KpiGateway kpiGateway = (KpiGateway)GatewayFactory.KPI_GATEWAY.get();
		User user = findUserByToken();
		param.salesmanCode = user.getSalesmanCode();
		List<Salesman> salesmans = getSalesmanFromBhaktiApi();
		return new WebPage<>(salesmans, 0, 0);
	}

	private List<Salesman> getSalesmanFromBhaktiApi() {
		BhaktiApiGateway bhaktiApiGateway = (BhaktiApiGateway)GatewayFactory.BHAKTI_API_GATEWAY.get();
		Page<Salesman> page = bhaktiApiGateway.getSubordinateSalesmanBySalesmanCode(param.salesmanCode);
		return page.getResult();
	}
	
	private User findUserByToken() {
		UserPresenter presenter = new UserPresenterImpl();
		DetailResponseModel<User> userDetail = presenter.findByToken(param.token);
		return userDetail.entity;
	}
	
}
