package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.RoleCode;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.Approver;
import com.bit.core.entity.EmailNotificator;
import com.bit.core.entity.KpiPeriod;
import com.bit.core.entity.Salesman;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.model.request.SalesmanTargetKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.NotificationResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindSubordinateSalesman;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.EmptyCollectionValidator;
import com.bit.core.validator.NullValidator;

public class SubmitStatusTargetsKpiUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static String unitKpiToken;
	private static RequestModelHelper<SalesmanKpiRequestModel> requestModelHelper;
//	private final static String SALESMAN_CODE = "SPS-A01";
//	private final static String SALESMAN_CODE_2 = "SPS-A02";
//	private final static String SALESMAN_CODE_3 = "SPS-A03";
	private static Salesman salesmanIndex0;
	private static Salesman salesmanIndex1;
	private static Salesman salesmanIndex2;
	private static String idUnitKpi1 = "";
	private static String idUnitKpi2 = "";
	private static String idUnitKpi3 = "";
	private static String username = "Zuser1";
	private static YearMonth period = YearMonth.of(2021, 1);	
	@BeforeAll
	public static void beforeAll() {
		createUser(username, "lykim176@gmail.com", "password123");
		createSalesmanFromApi();
		List<String> unitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		unitKpiToken = TokenUtils.createJWT( username, unitKpis,  60000);
		List<String> roles = Arrays.asList(RoleCode.ROLE_TARGET_SALESMAN_KPI_CUD, RoleCode.ROLE_SALESMAN_VIEW);
		token = TokenUtils.createJWT( username, roles,  60000);
		requestModelHelper = new RequestModelHelper<SalesmanKpiRequestModel>(new SalesmanKpiRequestModel(), token);
		createUnitKpis();
		createTargetKpi(salesmanIndex0, period);
		createTargetKpi(salesmanIndex1, period);
	}
	
	@Test
	@Order(1)
	public void notFillRequiredRequest_thenValidationError() {
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		Usecase<?,?> usecase = new SubmitTargetsKpiStatusUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyCollectionValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.IDS));
		assertEquals(NullValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.PERIOD));
	}
	
	@Test
	@Order(2)
	public void givenIds_OnlyIdWithTargetIsSubmitted() {
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		requestModel.period = period;
		Set<String> ids = new HashSet<>();
		ids.add(salesmanIndex0.getId());
		ids.add(salesmanIndex2.getId());
		requestModel.ids = ids;
		Usecase<?,?> usecase = new SubmitTargetsKpiStatusUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Salesman salesmanWithTarget = kpiGateway.getSalesmanById(salesmanIndex0.getId());
		KpiPeriod kpiPeriodWithTarget = getKpiPeriod(salesmanWithTarget, period);
		Salesman salesmanWithoutTarget = kpiGateway.getSalesmanById(salesmanIndex2.getId());
		assertEquals(TargetKpiStatus.SUBMITTED, kpiPeriodWithTarget.getKpiStatus());
		assertNull( getKpiPeriod(salesmanWithoutTarget, period) );
	}
	
	private KpiPeriod getKpiPeriod(Salesman salesman, YearMonth period) {
		Optional<KpiPeriod> optional = salesman.getKpiPeriods().stream().filter(data -> period.equals(data.getPeriod())).findAny();
		return optional.isPresent() ? optional.get() : null;
	}
	
	
	@Test
	@Order(3)
	public void givenIds_willSendEmailToApprover() {
		createApprover();
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		Set<String> ids = new HashSet<>();
		ids.add(salesmanIndex1.getId());
		requestModel.ids = ids;
		requestModel.period = period;
		Usecase<?,?> usecase = new SubmitTargetsKpiStatusUsecase(requestModel);
		usecase.run();
		NotificationResponseModel response = (NotificationResponseModel)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		assertTrue(response.isSentNotificationToAllSuccess);
	}
	
	private static void persistApprover(ApproverModule approverModule, Set<User> users, Set<Notificator> notificators) {
		Approver approver = new Approver(approverModule,users,notificators,ApproverableType.SIMPLE);
		approverGateway.save(approver);
	}
	
	private void createApprover() {
		List<User> users = new ArrayList<>();
		users.add(userGateway.findByUsername(username));
		persistApprover(ApproverModule.TARGET_KPI, generateApprovers(users), generateNotificators());
	}
	private static Set<Notificator> generateNotificators(){
		Set<Notificator> notificators = new HashSet<>();
		EmailNotificator emailNotificator = new EmailNotificator();
		notificators.add(emailNotificator);
		return notificators;
	}
	
	private static Set<User> generateApprovers(List<User> users){
		Set<User> approvers = new HashSet<>();
		users.forEach(user -> approvers.add(user));
		return approvers;
	}
	private static List<SalesmanTargetKpiRequestModel> createTargetKpis() {
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 2000000, 100, idUnitKpi1);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 80, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		return targetKpis;
	}
	
	private static String createTargetKpi(Salesman salesman, YearMonth period) {
		List<SalesmanTargetKpiRequestModel> targetKpis = createTargetKpis();
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesman.getId();
		requestModel.period = period;
		requestModel.targetKpiSalesman = targetKpis;
		Usecase<?,CreateResponseModel> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}

	private static void createSalesmanFromApi() {
		List<String> myRoles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		SalesmanRequestModel requestModel = new SalesmanRequestModel();
		requestModel.token = TokenUtils.createJWT( username, myRoles,  60000);
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesman(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy, myRoles);
		usecase.run();
		FindManyResponseModel<Salesman> response = (FindManyResponseModel<Salesman>)usecase.getResponseModel();
		List<Salesman> salesmans = response.page.getResult();
		salesmanIndex0 = salesmans.get(0);
		salesmanIndex1 = salesmans.get(1);
		salesmanIndex2 = salesmans.get(2);
	}
	
	private static SalesmanTargetKpiRequestModel createSalesmanTargetKpiRequest(int bobot, int target, int targetPoint, String idUnitKpi) {
		SalesmanTargetKpiRequestModel targetSalesman = new SalesmanTargetKpiRequestModel();
		targetSalesman.bobot = bobot;
		targetSalesman.target = target;
		targetSalesman.targetPoint = targetPoint;
		targetSalesman.unitKpiId = idUnitKpi;
		return targetSalesman;
	}
	
	private static void createUnitKpis() {
		idUnitKpi1 = generateUnitKpi("Target 1");
		idUnitKpi2 = generateUnitKpi("Target 2");
		idUnitKpi3 = generateUnitKpi("Target 3");
	}
	
	private static String generateUnitKpi(String name) {
		UnitKpiRequestModel requestModel = new UnitKpiRequestModel();
		requestModel.token = unitKpiToken;
		requestModel.name = name;
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
	private static String createUser(String username, String email, String password) {
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD);
		String tokenForCreateUser = TokenUtils.createJWT( "any", roles,  60000);
		UserRequestModel request = new UserRequestModel();
		request.token = tokenForCreateUser;
		request.username = username;
		request.email = email;
		request.password = password;
		request.salesmanCode = "RUK-ATN";
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
}
