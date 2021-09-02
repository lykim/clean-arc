package com.bit.core.usecase.roleGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findOne.FindRoleGroupById;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.TokenUtils;

public class FindRoleGroupByIdStrategyTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultName1 = "ROLE_GROUP1";
	private static String defaultDesc1 = "desc";
	private static String token;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW, RoleCode.ROLE_ROLE_GROUP_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		defaultId1 = createRoleGroup(defaultName1,defaultDesc1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findById_willReturnData() {
		RoleGroupRequestModel request = new RoleGroupRequestModel();
		request.token = token;
		request.id = defaultId1;
		FindOneStrategy<RoleGroup, RoleGroupRequestModel> findOneStrategy = new FindRoleGroupById(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<RoleGroup>(), findOneStrategy, viewRoles);
		usecase.run();
		DetailResponseModel<RoleGroup> response = (DetailResponseModel<RoleGroup>) usecase.getResponseModel();
		assertEquals( response.entity.getDescription(), defaultDesc1);
		assertEquals( response.entity.getName(), defaultName1);
	}
	
	private static String createRoleGroup(String name, String desc) {
		RoleGroupRequestModel request = new RoleGroupRequestModel();
		request.token = token;
		request.name = name;
		request.description = desc;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}
