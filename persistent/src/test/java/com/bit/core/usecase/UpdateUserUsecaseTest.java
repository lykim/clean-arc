package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.role.CreateRoleUsecase;
import com.bit.core.usecase.roleGroup.CreateRoleGroupUsecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.usecase.user.UpdateUserUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.EmailValidator;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateUserUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultId2;
	private static String defaultUsername1 = "user1";
	private static String defaultUsername2 = "user2";
	private static String defaultEmail1 = "email1@mail.com";
	private static String defaultEmail2 = "email2@mail.com";
	private static String defaultPassword = "@Pas123";
	private static String token;
	
	private static String defaultCode1 = "ROLE_A_";
	private static String defaultROleDesc1 = "DESC_A_";
	private static Set<String> roleIds1 = new HashSet<>();
	private static String defaultCode2 = "ROLE_B_";
	private static String defaultRoleDesc2 = "DESC_B_";
	private static Set<String> roleIds2 = new HashSet<>();
	private static int numOfRoleRows = 5;
	private static String defaultName1 = "name1";
	private static String defaultName2 = "name2";
	private static String defaultDesc1 = "desc1";
	private static String defaultDesc2 = "desc2";
	private static Set<String> roleGroupIds = new HashSet<>();
	private static String roleGroupId1;
	private static String roleGroupId2;
	private static RequestModelHelper<UserRequestModel> requestModelHelper;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD, RoleCode.ROLE_ROLE_GROUP_CUD, RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  3600000);
		requestModelHelper = new RequestModelHelper<UserRequestModel>(new UserRequestModel(), token);
		createRoles(roleIds1, defaultCode1, defaultROleDesc1);
		createRoles(roleIds2, defaultCode2, defaultRoleDesc2);
		roleGroupId1 = createRoleGroup(defaultName1, defaultROleDesc1, roleIds1);
		roleGroupId2 = createRoleGroup(defaultName2, defaultRoleDesc2, roleIds2);
		defaultId1 = createUser(defaultUsername1,defaultEmail1,defaultPassword);
		defaultId2 = createUser(defaultUsername2,defaultEmail2,defaultPassword);
		roleGroupIds.add(roleGroupId1);
		roleGroupIds.add(roleGroupId2);
	}
	
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new UpdateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		Usecase<?,?> usecase = new UpdateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenTakenUsername_willThrowError() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.username = defaultUsername1;
		Usecase<?,?> usecase = new UpdateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}
	
	@Test
	public void givenInvalidEmail_willGetValidationMessage() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId1;
		requestModel.email = "user2@email";
		Usecase<?,?> usecase = new UpdateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmailValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.EMAIL));
	}
	
	@Test
	public void givenUpdatedFields_willSaveUpdatedField() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = defaultId2;
		requestModel.username = "username2";
		requestModel.email = "email@mail.com";
		requestModel.roleGroupIDs = roleGroupIds;
		requestModel.salesmanCode = "salesCode";
		Usecase<?,?> usecase = new UpdateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		User user = userGateway.findById(defaultId2);
		assertEquals(requestModel.salesmanCode, user.getSalesmanCode());
		assertEquals(requestModel.username, user.getUsername());
		assertEquals(requestModel.email, user.getEmail());
		Set<RoleGroup> roleGroups = user.getRoleGroups();
		roleGroups.stream().forEach(roleGroup -> {
			if(roleGroup.getId().equals(roleGroupId1)) {
				assertTrue(roleGroup.getName().equals(defaultName1));
				assertEquals(roleGroup.getDescription(), defaultROleDesc1);
				Set<Role> roles = roleGroup.getRoles();
				assertEquals(numOfRoleRows, roles.size());
				roles.stream().forEach(role -> {
					assertTrue(role.getCode().contains(defaultCode1));
					assertTrue(role.getDescription().contains(defaultROleDesc1));
				});
			}else {
				assertTrue(roleGroup.getName().equals(defaultName2));
				assertEquals(roleGroup.getDescription(), defaultRoleDesc2);
				Set<Role> roles = roleGroup.getRoles();
				assertEquals(numOfRoleRows, roles.size());
				roles.stream().forEach(role -> {
					assertTrue(role.getCode().contains(defaultCode2));
					assertTrue(role.getDescription().contains(defaultRoleDesc2));
				});
			}
		});
	}

	private static String createUser(String username, String email, String password) {
		UserRequestModel request =  new UserRequestModel();
		request.token = token;
		request.username = username;
		request.email = email;
		request.password = password;
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
	private static String createRoleGroup(String name, String description, Set<String> roleIds) {
		RoleGroupRequestModel request = new RoleGroupRequestModel();
		request.token = token;
		request.name = name;
		request.description = description;
		request.roleIds = roleIds;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleGroupUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
	private static void createRoles(Set<String> roleIds, String defaultCode, String defaultDESC) {
		for(int i=0; i < numOfRoleRows; i++) {
			String roleId =createRole(defaultCode + i, defaultDESC+i);		
			roleIds.add(roleId);
		}
	}
	
	private static String createRole(String code, String description) {
		RoleRequestModel request = new RoleRequestModel();
		request.token = token;
		request.code = code;
		request.description = description;
		Usecase<?, CreateResponseModel> usecase = new CreateRoleUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		if(response.isSuccess)
			return response.id;
		else {
			return "";
		}
	}
	
}
