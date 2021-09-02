package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.Position;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.role.CreateRoleUsecase;
import com.bit.core.usecase.roleGroup.CreateRoleGroupUsecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.PasswordUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.EmailValidator;
import com.bit.core.validator.PasswordValidator;
import com.bit.core.validator.string.EmptyValidator;

public class CreateUserUsecaseTest extends BaseUsecaseTest{
	private String USERNAME_1 = "user1";
	private String PASSWORD_1 = "user1Password";
	private String VALID_EMAIL_1 = "user1@email.com";
	
	private String USERNAME_3 = "user3";
	private String PASSWORD_3 = "user3Password";
	private String VALID_EMAIL_3 = "user3@email.com";
	
	private String USERNAME_2 = "user2";
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
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<UserRequestModel>(new UserRequestModel(), token);
		createRoles(roleIds1, defaultCode1, defaultROleDesc1);
		createRoles(roleIds2, defaultCode2, defaultRoleDesc2);
		roleGroupId1 = createRoleGroup(defaultName1, defaultROleDesc1, roleIds1);
		roleGroupId2 = createRoleGroup(defaultName2, defaultRoleDesc2, roleIds2);
		roleGroupIds.add(roleGroupId1);
		roleGroupIds.add(roleGroupId2);
	}
	
	
	@Test
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.USERNAME));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.EMAIL));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.PASSWORD));
	}
	
	@Test
	public void givenInvalidPassword_willGetValidationMessage() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.password = "a";
		Usecase<?,?> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(PasswordValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.PASSWORD));
	}
	
	@Test
	public void givenInvalidEmail_willGetValidationMessage() {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.email = "invalid@email";
		Usecase<?,?> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmailValidator.VALIDATION_MESSAGE, response.validationMessages.get(UserRequestModel.Label.EMAIL));
	}
	
	@Test
	public void givenInvalidPositionCode_willGetErrorMessage() {
		UserRequestModel requestModel = createRequiredRequestModel(USERNAME_1, VALID_EMAIL_1);
		requestModel.positionCode = 99;
		Usecase<?,?> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenAllRequiredFields_thenDataIsStored() {
		UserRequestModel requestModel = createRequiredRequestModel(USERNAME_1, VALID_EMAIL_1);
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		User user = userGateway.findByIdShowPassword(response.id);
		assertEquals(requestModel.username, user.getUsername());
		assertEquals(response.id, user.getId());
		assertEquals(requestModel.email, user.getEmail());
		assertTrue(PasswordUtils.isVerified(requestModel.password, user.getPassword()));
	}
	
	@Test
	public void givenAllOptionalsFields_thenDataIsStored() {
		UserRequestModel requestModel = createRequiredRequestModel(USERNAME_3, VALID_EMAIL_3);
		requestModel.roleGroupIDs = roleGroupIds;
		requestModel.salesmanCode = "salesCode";
		requestModel.positionCode = Position.BRAND_MANAGER.getCode();
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		User user = userGateway.findById(response.id);
		assertEquals(requestModel.salesmanCode, user.getSalesmanCode());
		assertEquals(requestModel.positionCode, user.getPosition().getCode());
		assertEquals(requestModel.branchId, user.getBranchId());
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
	
	@Test
	public void givenAlreadyStoredUsername_thenReturnErrorMessage() {
		UserRequestModel requestModel1 = createRequiredRequestModel(USERNAME_2, VALID_EMAIL_1);
		Usecase<?, ?> usecase1 = new CreateUserUsecase(requestModel1);
		usecase1.run();
		
		Usecase<?, ?> usecase2 = new CreateUserUsecase(requestModel1);
		usecase2.run();
		ResponseModel response = usecase2.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}
	
	private UserRequestModel createRequiredRequestModel(String username, String email) {
		UserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.username = username;
		requestModel.email = email;
		requestModel.password = PASSWORD_1;
		return requestModel;
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
	
}
