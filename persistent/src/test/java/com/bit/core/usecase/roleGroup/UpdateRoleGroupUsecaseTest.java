package com.bit.core.usecase.roleGroup;

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
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateRoleGroupUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultId2;
	private static String defaultName1 = "roleGroup1";
	private static String defaultName2 = "roleGroup2";
	private static String defaultDesc1 = "desc1";
	private static String defaultDesc2 = "desc2";
	private static String token;
	private static RequestModelHelper<RoleGroupRequestModel> requestModelHelper;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD, RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<RoleGroupRequestModel>(new RoleGroupRequestModel(), token);
		defaultId1 = createRoleGroup(defaultName1,defaultDesc1);
		defaultId2 = createRoleGroup(defaultName2,defaultDesc2);
	}
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new UpdateRoleGroupUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		Usecase<?,?> usecase = new UpdateRoleGroupUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenTakenname_willThrowError() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.name = defaultName1;
		Usecase<?,?> usecase = new UpdateRoleGroupUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}

	@Test
	public void givenUpdatedFields_willSaveUpdatedField() {
		RoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.name = "myname2";
		Usecase<?,?> usecase = new UpdateRoleGroupUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		RoleGroup entity = roleGroupGateway.findById(defaultId2);
		assertEquals(requestModel.name, entity.getName());
	}

	private static String createRoleGroup(String name, String desc) {
		RoleGroupRequestModel request =  new RoleGroupRequestModel();
		request.token = token;
		request.name = name;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}

