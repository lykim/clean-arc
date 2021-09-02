package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockUnitKpiGateway;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findOne.FindUnitKpiById;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.TokenUtils;

public class FindUnitKpiByIdStrategyTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultCode1 = "UNIT_KPI1";
	private static String defaultDesc1 = "desc";
	private static String token;
	private static List<String> loginUnitKpis;
	private static List<String> viewUnitKpis;
	
	@BeforeAll
	public static void beforeAll() {
		viewUnitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW);
		loginUnitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW, RoleCode.ROLE_UNIT_KPI_CUD);
		token = TokenUtils.createJWT( "ruly", loginUnitKpis,  60000);
		defaultId1 = createUnitKpi(defaultCode1,defaultDesc1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findById_willReturnData() {
		UnitKpiRequestModel request = new UnitKpiRequestModel();
		request.token = token;
		request.id = defaultId1;
		FindOneStrategy<UnitKpi, UnitKpiRequestModel> findOneStrategy = new FindUnitKpiById(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<UnitKpi>(), findOneStrategy, viewUnitKpis);
		usecase.run();
		DetailResponseModel<UnitKpi> response = (DetailResponseModel<UnitKpi>) usecase.getResponseModel();
		assertEquals( response.entity.getDescription(), defaultDesc1);
		assertEquals( response.entity.getName(), defaultCode1);
	}

	
	private static String createUnitKpi(String code, String desc) {
		UnitKpiRequestModel request = new UnitKpiRequestModel();
		request.token = token;
		request.name = code;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}
