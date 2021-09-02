package com.bit.core.usecase.roleGroup;

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
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockRoleGroupGateway;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindRoleGroups;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindManyRoleGroupsStrategyTest extends BaseUsecaseTest{
	private static String nameAbc = "abc";
	private static String nameCba = "cba";
	private static int numOfRows = 20;
	private static String token;
	private static RequestModelHelper<FindManyRoleGroupRequestModel> requestModelHelper;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW, RoleCode.ROLE_ROLE_GROUP_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		requestModelHelper = new RequestModelHelper<FindManyRoleGroupRequestModel>(new FindManyRoleGroupRequestModel(), token);
		String name = nameAbc;
		String descriptionDomain = "@mail.com";
		for(int i=0; i < numOfRows; i++) {
			if(i >=10) {
				name = nameCba;
			}
			String nameHyphen = name + "_" +i;
			createRoleGroup(nameHyphen, nameHyphen + descriptionDomain);			
		}
	}
	
	@Test
	public void givenSortAscending_thenSortedAscending() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.ASCENDING;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			if(i <10) {
				assertEquals(roleGroup.getName(), nameAbc+"_"+i);				
			}else {
				assertEquals(roleGroup.getName(), nameCba+"_"+i);
			}
		}
	}
	
	@Test
	public void givenSortDescending_thenSortedDescending() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(roleGroup.getName(), nameCba+"_"+index);				
			}else {
				assertEquals(roleGroup.getName(), nameAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenOrderBy_thenSortBasedByOrderBy() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		requestModel.orderBy = "name";
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(roleGroup.getName(), nameCba+"_"+index);				
			}else {
				assertEquals(roleGroup.getName(), nameAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenPageSize_thenResultSizeSameAsPageSize() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		assertEquals(requestModel.pageSize, result.size());
	}
	
	@Test
	public void givenPageSize_thenNumberOfPagesIsCountedCorrectly() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 3;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		Page<RoleGroup> page = response.page;
		assertTrue(response.isSuccess);
		int numPage = (int)Math.ceil(numOfRows / requestModel.pageSize);
		assertEquals(numPage, page.getNumOfPages());
	}
	
	@Test
	public void givenPage1With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		for(int i=0; i > result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			assertEquals(roleGroup.getName(), nameAbc+"_"+i);
		}
	}
	
	@Test
	public void givenPage2With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyRoleGroupRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 2;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			assertEquals(roleGroup.getName(), nameAbc+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize)));
		}
	}
	
	@Test
	public void givenRoleGroupname_thenSearchBasedByRoleGroupname() {
		FindManyRoleGroupRequestModel requestModel =requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		requestModel.nameLike = nameCba;
		int indexStart = requestModel.nameLike.equals(nameCba) ? 10 : 0;
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<RoleGroup> response = (FindManyResponseModel<RoleGroup>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<RoleGroup> page = response.page;
		List<RoleGroup> result = page.getResult();
		assertEquals(2, page.getNumOfPages());
		for(int i=0; i < result.size(); i++) {
			RoleGroup roleGroup = result.get(i);
			assertEquals(roleGroup.getName(), nameCba+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize) + indexStart));
		}
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
