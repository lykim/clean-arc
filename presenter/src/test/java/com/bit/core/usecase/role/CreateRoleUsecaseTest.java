package com.bit.core.usecase.role;

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
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class CreateRoleUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<RoleRequestModel> requestModelHelper;
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<RoleRequestModel>(new RoleRequestModel(), token);
	}
	
	@Test
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateRoleUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RoleRequestModel.Label.CODE));
	}
	
	@Test
	public void givenAllRequiredFields_thenDataIsStored() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.code = "ROLE_1";
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Role role = roleGateway.findById(response.id);
		assertEquals(requestModel.code, role.getCode());
		assertEquals(response.id, role.getId());
	}
	
	@Test
	public void givenOptionalFields_thenDataIsStored() {
		RoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.code = "ROLE_2";
		requestModel.description = "description of role";
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Role role = roleGateway.findById(response.id);
		assertEquals(requestModel.description, role.getDescription());
		assertEquals(response.id, role.getId());
	}
	
	@Test
	public void givenAlreadyExistCpde_thenReturnError() {
		RoleRequestModel requestModel1 =  requestModelHelper.getRequestModelWithToken();
		requestModel1.code = "ROLE_CODE_TAKEN";
		Usecase<?, ?> usecase1 = new CreateRoleUsecase(requestModel1);
		usecase1.run();
		
		Usecase<?, ?> usecase2 = new CreateRoleUsecase(requestModel1);
		usecase2.run();
		ResponseModel response = usecase2.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_ALREADY_TAKEN, response.errorMessage);
	}
	
}
