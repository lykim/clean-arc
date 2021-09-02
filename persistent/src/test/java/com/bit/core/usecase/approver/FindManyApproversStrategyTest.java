package com.bit.core.usecase.approver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.Direction;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.Role;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindApprovers;
import com.bit.core.strategy.findMany.FindRoles;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.utility.ApproverUsecaseHelper;
import com.bit.core.utility.UserUsecaseUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindManyApproversStrategyTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<FindManyRequestModel> requestModelHelper;
	private static ApproverUsecaseHelper approvalHelper;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_APPROVER_VIEW);
		token = TokenUtils.createJWT( "ruly", viewRoles,  60000);
		requestModelHelper = new RequestModelHelper<FindManyRequestModel>(new FindManyRequestModel(), token);
		approvalHelper = new ApproverUsecaseHelper();
		List<String> approverIds = UserUsecaseUtils.createUsers("abcUnique3", "@mail.com", "p@ssw0rd", 2);
		approvalHelper.createApprover(ApproverableType.TEST, ApproverModule.TEST, approverIds);
		approvalHelper.createApprover(ApproverableType.SIMPLE, ApproverModule.TARGET_KPI, approverIds);
	}
	
	@Test
	public void givenSortAscending_thenSortedAscending() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.ASCENDING;
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		List<Approver> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Approver approver = result.get(i);
			if(i == 0) {
				assertEquals(approver.getModule(), ApproverModule.TARGET_KPI);
			}
		}
	}
	
	@Test
	public void givenSortDescending_thenSortedDescending() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		List<Approver> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Approver approver = result.get(i);
			if(i == 0) {
				assertEquals(approver.getModule(), ApproverModule.TEST);
			}
		}
	}
	
	@Test
	public void givenOrderByType_thenSortBasedByOrderBy() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.orderBy = "type";
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		List<Approver> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Approver approver = result.get(i);
			if(i == 0) {
				assertEquals(approver.getApproverabelType(), ApproverableType.SIMPLE);
			}
		}
	}
	
	@Test
	public void givenOrderByTypeAndDesc_thenSortBasedByOrderBy() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.orderBy = "type";
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		List<Approver> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			Approver approver = result.get(i);
			if(i == 0) {
				assertEquals(approver.getApproverabelType(), ApproverableType.TEST);
			}
		}
	}
	
	@Test
	public void givenPageSize_thenResultSizeSameAsPageSize() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.orderBy = "type";
		requestModel.direction = Direction.DESCENDING;
		requestModel.pageSize = 2;
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		List<Approver> result = page.getResult();
		assertEquals(requestModel.pageSize, result.size());
	}
	
	@Test
	public void givenPageSize_thenNumberOfPagesIsCountedCorrectly() {
		FindManyRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.orderBy = "type";
		requestModel.direction = Direction.DESCENDING;
		requestModel.pageSize = 1;
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<Approver> response = (FindManyResponseModel<Approver>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<Approver> page = response.page;
		assertTrue(response.isSuccess);
		int numPage = (int)Math.ceil(2 / requestModel.pageSize);
		assertEquals(numPage, page.getNumOfPages());
	}

}
