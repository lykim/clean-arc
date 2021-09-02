package com.bit.core.usecase.roleGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.role.CreateRoleUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class CreateRoleGroupUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<RoleGroupRequestModel> requestModelHelper;
	private static int numOfRows = 10;
	private static String defaultCode = "ROLE_";
	private static String defaultDESC = "DESC_";
	private static Set<String> roleIds = new HashSet<>();
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD, RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<RoleGroupRequestModel>(new RoleGroupRequestModel(), token);
		for(int i=0; i < numOfRows; i++) {
			String roleId =createRole(defaultCode + i, defaultDESC+i);		
			roleIds.add(roleId);
		}
	}
	
	@Test
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateRoleGroupUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RoleGroupRequestModel.Label.NAME));
	}
	
	@Test
	public void givenAllRequiredFields_thenDataIsStored() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.name = "ROLE_1";
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		RoleGroup roleGroup = roleGroupGateway.findById(response.id);
		assertEquals(requestModel.name, roleGroup.getName());
		assertEquals(response.id, roleGroup.getId());
	}
	
	@Test
	public void givenOptionalFields_thenDataIsStored() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.name = "ROLE_2";
		requestModel.description = "description of roleGroup";
		requestModel.roleIds = roleIds;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		RoleGroup roleGroup = roleGroupGateway.findById(response.id);
		assertEquals(requestModel.description, roleGroup.getDescription());
		assertEquals(response.id, roleGroup.getId());
		roleGroup.getRoles().stream().forEach(role -> {
			assertTrue( role.getCode().contains(defaultCode));	
			assertTrue( role.getDescription().contains(defaultDESC));	
		});
	}
	
	@Test
	public void givenAlreadyExistCpde_thenReturnError() {
		RoleGroupRequestModel requestModel1 =  requestModelHelper.getRequestModelWithToken();
		requestModel1.name = "ROLE_NAME_TAKEN";
		Usecase<?, ?> usecase1 = new CreateRoleGroupUsecase(requestModel1);
		usecase1.run();
		
		Usecase<?, ?> usecase2 = new CreateRoleGroupUsecase(requestModel1);
		usecase2.run();
		ResponseModel response = usecase2.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}
	
	private static String createRole(String code, String description) {
		RoleRequestModel request = new RoleRequestModel();
		request.token = token;
		request.code = code;
		request.description = description;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}