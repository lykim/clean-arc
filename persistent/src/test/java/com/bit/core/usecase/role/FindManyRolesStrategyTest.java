package com.bit.core.usecase.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.Direction;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGateway;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindRoles;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindManyRolesStrategyTest extends BaseUsecaseTest{
	private static String codeAbc = "abc";
	private static String codeCba = "cba";
	private static int numOfRows = 20;
	private static String token;
	private static RequestModelHelper<FindManyRoleRequestModel> requestModelHelper;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW, RoleCode.ROLE_ROLE_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		requestModelHelper = new RequestModelHelper<FindManyRoleRequestModel>(new FindManyRoleRequestModel(), token);
		String code = codeAbc;
		String descriptionDomain = "@mail.com";
		for(int i=0; i < numOfRows; i++) {
			if(i >=10) {
				code = codeCba;
			}
			String name = code + "_" +i;
			createRole(name, name + descriptionDomain);			
		}
	}
	
	@Test
	public void givenSortAscending_thenSortedAscending() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.ASCENDING;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Role role = result.get(i);
			if(i <10) {
				assertEquals(role.getCode(), codeAbc+"_"+i);				
			}else {
				assertEquals(role.getCode(), codeCba+"_"+i);
			}
		}
	}
	
	@Test
	public void givenSortDescending_thenSortedDescending() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Role role = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(role.getCode(), codeCba+"_"+index);				
			}else {
				assertEquals(role.getCode(), codeAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenOrderBy_thenSortBasedByOrderBy() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		requestModel.orderBy = "code";
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Role role = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(role.getCode(), codeCba+"_"+index);				
			}else {
				assertEquals(role.getCode(), codeAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenPageSize_thenResultSizeSameAsPageSize() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		assertEquals(requestModel.pageSize, result.size());
	}
	
	@Test
	public void givenPageSize_thenNumberOfPagesIsCountedCorrectly() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 3;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		Page<Role> page = response.page;
		assertTrue(response.isSuccess);
		int numPage = (int)Math.ceil(numOfRows / requestModel.pageSize);
		assertEquals(numPage, page.getNumOfPages());
	}
	
	@Test
	public void givenPage1With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		for(int i=0; i > result.size(); i++) {
			Role role = result.get(i);
			assertEquals(role.getCode(), codeAbc+"_"+i);
		}
	}
	
	@Test
	public void givenPage2With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyRoleRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 2;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Role role = result.get(i);
			assertEquals(role.getCode(), codeAbc+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize)));
		}
	}
	
	@Test
	public void givenRolename_thenSearchBasedByRolename() {
		FindManyRoleRequestModel requestModel =requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		requestModel.codeLike = codeCba;
		int indexStart = requestModel.codeLike.equals(codeCba) ? 10 : 0;
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Role> response = (FindManyResponseModel<Role>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Role> page = response.page;
		List<Role> result = page.getResult();
		assertEquals(2, page.getNumOfPages());
		for(int i=0; i < result.size(); i++) {
			Role role = result.get(i);
			assertEquals(role.getCode(), codeCba+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize) + indexStart));
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
		return response.id;
	}
	
}
