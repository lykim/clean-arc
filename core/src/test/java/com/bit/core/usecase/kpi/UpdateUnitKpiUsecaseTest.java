package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateUnitKpiUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultId2;
	private static String defaultCode1 = "unitKpi1";
	private static String defaultCode2 = "unitKpi2";
	private static String defaultDesc1 = "desc1";
	private static String defaultDesc2 = "desc2";
	private static String token;
	private static RequestModelHelper<UnitKpiRequestModel> requestModelHelper;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> unitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		token = TokenUtils.createJWT( "ruly", unitKpis,  60000);
		requestModelHelper = new RequestModelHelper<UnitKpiRequestModel>(new UnitKpiRequestModel(), token);
		defaultId1 = createUnitKpi(defaultCode1,defaultDesc1);
		defaultId2 = createUnitKpi(defaultCode2,defaultDesc2);
	}
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new UpdateUnitKpiUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		Usecase<?,?> usecase = new UpdateUnitKpiUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenTakenName_willThrowError() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.name = defaultCode1;
		Usecase<?,?> usecase = new UpdateUnitKpiUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}

	@Test
	public void givenUpdatedFields_willSaveUpdatedField() {
		UnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.name = "mycode2";
		requestModel.description = "updatedesc";
		Usecase<?,?> usecase = new UpdateUnitKpiUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		UnitKpi entity = unitKpiGateway.findById(defaultId2);
		assertEquals(requestModel.name, entity.getName());
		assertEquals(requestModel.description, entity.getDescription());
	}

	private static String createUnitKpi(String name, String desc) {
		UnitKpiRequestModel request =  new UnitKpiRequestModel();
		request.token = token;
		request.name = name;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
}
