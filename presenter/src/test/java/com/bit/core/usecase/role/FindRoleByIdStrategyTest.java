package com.bit.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findOne.FindRoleById;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.TokenUtils;

public class FindRoleByIdStrategyTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultCode1 = "ROLE1";
	private static String defaultDesc1 = "desc";
	private static String token;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW, RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		defaultId1 = createRole(defaultCode1,defaultDesc1);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void findById_willReturnData() {
		RoleRequestModel request = new RoleRequestModel();
		request.token = token;
		request.id = defaultId1;
		FindOneStrategy<Role, RoleRequestModel> findOneStrategy = new FindRoleById(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Role>(), findOneStrategy, viewRoles);
		usecase.run();
		DetailResponseModel<Role> response = (DetailResponseModel<Role>) usecase.getResponseModel();
		assertEquals( response.entity.getDescription(), defaultDesc1);
		assertEquals( response.entity.getCode(), defaultCode1);
	}
	
	
	private static String createRole(String code, String desc) {
		RoleRequestModel request = new RoleRequestModel();
		request.token = token;
		request.code = code;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}
