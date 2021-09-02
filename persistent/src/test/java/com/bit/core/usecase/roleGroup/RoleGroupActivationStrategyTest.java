package com.bit.core.usecase.roleGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.usecase.ActiveInactiveUsecase;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.NullValidator;
import com.bit.core.validator.string.EmptyValidator;

@TestMethodOrder(OrderAnnotation.class)
public class RoleGroupActivationStrategyTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultName1 = "roleGroup1";
	private static String defaultDescription1 = "description1@mail.com";
	private static String token;
	private static RequestModelHelper<ActiveInactiveRequestModel> requestModelHelper;
	private static List<String> roleGroupRoles;
	
	@BeforeAll
	public static void beforeAll() {
		roleGroupRoles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD);
		token = TokenUtils.createJWT( "ruly", roleGroupRoles,  60000);
		defaultId1 = createRoleGroup(defaultName1,defaultDescription1);
		requestModelHelper = new RequestModelHelper<ActiveInactiveRequestModel>(new ActiveInactiveRequestModel(), token);
	}
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		ActiveInactiveRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		OnOffStrategy<?,?,?> onOff = createOnOff(requestModel.id);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOff, roleGroupRoles);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}

	@Test
	public void givenNullStatus_willGetValidationMessage() {
		ActiveInactiveRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId1;
		OnOffStrategy<?,?,?> onOff = createOnOff(requestModel.id);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOff, roleGroupRoles);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(NullValidator.VALIDATION_MESSAGE, response.validationMessages.get(ActiveInactiveRequestModel.ACTIVE_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		ActiveInactiveRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		requestModel.active = Boolean.TRUE;
		OnOffStrategy<?, ?, ?> onOffAble = createOnOff(requestModel.id);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roleGroupRoles);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	@Order(1)
	public void givenInactive_thenWillInactive() {
		ActiveInactiveRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId1;
		requestModel.active = Boolean.FALSE;
		OnOffStrategy<?, ?, ?> onOffAble = createOnOff(requestModel.id);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roleGroupRoles);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		RoleGroup roleGroup = roleGroupGateway.findById(defaultId1);
		assertFalse(roleGroup.isActive());
	}
	
	@Test
	@Order(2)
	public void givenActive_thenWillActive() {
		ActiveInactiveRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId1;
		requestModel.active = Boolean.TRUE;
		OnOffStrategy<?, ?, ?> onOffAble = createOnOff(requestModel.id);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roleGroupRoles);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		RoleGroup roleGroup = roleGroupGateway.findById(defaultId1);
		assertTrue(roleGroup.isActive());
	}
	
	private OnOffStrategy<?,?,?> createOnOff(String id){
		return new OnOffStrategy<>(id, (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get());
	}
	
	private static String createRoleGroup(String name, String description) {
		RoleGroupRequestModel request = new RoleGroupRequestModel();
		request.token = token;
		request.name = name;
		request.description = description;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}

}
