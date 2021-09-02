package com.bit.core.usecase.role;

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
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateRoleUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultId2;
	private static String defaultCode1 = "role1";
	private static String defaultCode2 = "role2";
	private static String defaultDesc1 = "desc1";
	private static String defaultDesc2 = "desc2";
	private static String token;
	private static RequestModelHelper<RoleRequestModel> requestModelHelper;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<RoleRequestModel>(new RoleRequestModel(), token);
		defaultId1 = createRole(defaultCode1,defaultDesc1);
		defaultId2 = createRole(defaultCode2,defaultDesc2);
	}
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new UpdateRoleUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		Usecase<?,?> usecase = new UpdateRoleUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenTakencode_willThrowError() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.code = defaultCode1;
		Usecase<?,?> usecase = new UpdateRoleUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_ALREADY_TAKEN, response.errorMessage);
	}

	@Test
	public void givenUpdatedFields_willSaveUpdatedField() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.code = "mycode2";
		Usecase<?,?> usecase = new UpdateRoleUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Role entity = roleGateway.findById(defaultId2);
		assertEquals(requestModel.code, entity.getCode());
	}

	private static String createRole(String code, String desc) {
		RoleRequestModel request =  new RoleRequestModel();
		request.token = token;
		request.code = code;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}
