package com.bit.core.usecase.findmany;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.api.bhakti.BhaktiApiGatewayImpl;
import com.bit.core.config.GatewayConfig;
import com.bit.core.entity.Salesman;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockBhaktiApiGateway;
import com.bit.core.mocks.gateway.MockUserGateway;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindSubordinateSalesmanFromApi;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.utils.TokenUtils;

public class FindManySalesmanSubordinateTest {
	private static String token;
	private static final String SALESMAN_CODE = "RUK-ATN";
	@BeforeAll
	public static void beforeAll() {
		token = TokenUtils.createJWT( "ruly", null,  60000);
		setConfig();
	}
	
	@Test
	public void givenSalesmanCode_thenGetSubordinate() {
		SalesmanRequestModel requestModel = new SalesmanRequestModel();
		requestModel.token = token;
		requestModel.salesmanCode = SALESMAN_CODE;
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesmanFromApi(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy);
		usecase.run();
		FindManyResponseModel<Salesman> response = (FindManyResponseModel<Salesman>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		assertTrue( response.page.getResult().size() > 0 );
		for(Salesman salesman : response.page.getResult()) {
			assertNotNull(salesman.getCode());
			assertNotNull(salesman.getName());
		}
	}
	
	@AfterAll
	public static void afterAll() {
		resetConfig();
	}
	
	private static void setConfig() {
		GatewayConfig.bhaktiApiGateway = new BhaktiApiGatewayImpl();
	}
	
	private static void resetConfig() {
		GatewayFactory.USER_GATEWAY.destroy();
	}
}
