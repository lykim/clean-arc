package com.bit.core.usecase.approver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findOne.FindApproverById;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.utility.ApproverUsecaseHelper;
import com.bit.core.utility.UserUsecaseUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindApproverByIdStrategyTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<ApproverRequestModel> requestModelHelper;
	private static ApproverUsecaseHelper approvalHelper;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_APPROVER_VIEW);
		token = TokenUtils.createJWT( "ruly", viewRoles,  60000);
		requestModelHelper = new RequestModelHelper<ApproverRequestModel>(new ApproverRequestModel(), token);
		approvalHelper = new ApproverUsecaseHelper();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findById_willReturnData() {
		List<String> approverIds = UserUsecaseUtils.createUsers("abcUnique3", "@mail.com", "p@ssw0rd", 2);
		String approverId = approvalHelper.createApprover(ApproverableType.SIMPLE, ApproverModule.TARGET_KPI, approverIds);
		RequestModel request = new ApproverRequestModel();
		request.token = token;
		request.id = approverId;
		FindOneStrategy<Approver, RequestModel> findOneStrategy = new FindApproverById(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Approver>(), findOneStrategy, viewRoles);
		usecase.run();
		DetailResponseModel<Approver> response = (DetailResponseModel<Approver>) usecase.getResponseModel();
		assertEquals( response.entity.getModule(), ApproverModule.TARGET_KPI);
		assertEquals( response.entity.getApproverabelType(), ApproverableType.SIMPLE);
		for(User user :  response.entity.getApprovers()) {
			assertTrue(approverIds.contains(user.getId()));
		}
	}
}
