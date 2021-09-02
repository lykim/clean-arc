package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockUnitKpiGateway;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class CreateUnitKpiUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<UnitKpiRequestModel> requestModelHelper;
	@BeforeAll
	public static void beforeAll() {
		List<String> unitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		token = TokenUtils.createJWT( "ruly", unitKpis,  60000);
		requestModelHelper = new RequestModelHelper<UnitKpiRequestModel>(new UnitKpiRequestModel(), token);
	}
	
	@Test
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateUnitKpiUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(UnitKpiRequestModel.Label.NAME));
	}
	
	@Test
	public void givenAllRequiredFields_thenDataIsStored() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.name = "UNIT_KPI_1";
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		UnitKpi unitKpi = unitKpiGateway.findById(response.id);
		assertEquals(requestModel.name, unitKpi.getName());
		assertEquals(response.id, unitKpi.getId());
	}
	
	@Test
	public void givenOptionalFields_thenDataIsStored() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.name = "UNIT_KPI_2";
		requestModel.description = "description of unitKpi";
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		UnitKpi unitKpi = unitKpiGateway.findById(response.id);
		assertEquals(requestModel.description, unitKpi.getDescription());
		assertEquals(response.id, unitKpi.getId());
	}
	
	@Test
	public void givenAlreadyExistName_thenReturnError() {
		UnitKpiRequestModel requestModel1 =  requestModelHelper.getRequestModelWithToken();
		requestModel1.name = "UNIT_KPI_CODE_TAKEN";
		Usecase<?, ?> usecase1 = new CreateUnitKpiUsecase(requestModel1);
		usecase1.run();
		
		Usecase<?, ?> usecase2 = new CreateUnitKpiUsecase(requestModel1);
		usecase2.run();
		ResponseModel response = usecase2.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}
	
}
