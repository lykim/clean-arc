package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.KpiPeriod;
import com.bit.core.entity.Salesman;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.model.request.SalesmanTargetKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindSubordinateSalesman;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.NullValidator;
import com.bit.core.validator.string.EmptyValidator;

@TestMethodOrder(OrderAnnotation.class)
public class SubmitStatusTargetKpiTest extends BaseUsecaseTest{
	private static String token;
	private static String unitKpiToken;
	private static RequestModelHelper<SalesmanKpiRequestModel> requestModelHelper;
//	private final static String SALESMAN_CODE = "SPS-A01";
	private static Salesman salesmanIndex0;
	private static Salesman salesmanIndex1;
	private static String idUnitKpi1 = "";
	private static String idUnitKpi2 = "";
	private static String idUnitKpi3 = "";
	private static String username = "Zuser1";
	
	@BeforeAll
	public static void beforeAll() {
		createUser(username, "mail@mail.com", "password123");
		createSalesmanFromApi();
		List<String> unitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_CUD);
		unitKpiToken = TokenUtils.createJWT( username, unitKpis,  60000);
		List<String> roles = Arrays.asList(RoleCode.ROLE_TARGET_SALESMAN_KPI_CUD, RoleCode.ROLE_SALESMAN_VIEW);
		token = TokenUtils.createJWT( username, roles,  60000);
		requestModelHelper = new RequestModelHelper<SalesmanKpiRequestModel>(new SalesmanKpiRequestModel(), token);
		createUnitKpis();
	}
	
	@Test
	@Order(1)
	public void notFillRequiredRequest_thenValidationError() {
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		Usecase<?,?> usecase = new SubmitTargetKpiStatusUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.ID));
		assertEquals(NullValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.PERIOD));
	}
	
	@Test
	@Order(2)
	public void givenInvalidId_thenError() {
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		requestModel.id = "CODE_TAIL_NONSENSE" ;
		requestModel.period = YearMonth.of(1999, 1);
		Usecase<?,?> usecase = new SubmitTargetKpiStatusUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	@Order(3)
	public void whenStatusCodeIsNotDraft_thenError() {
		YearMonth period = YearMonth.of(2017,6);
		createTargetKpiWithValueFields(salesmanIndex1, period);
		changeSalesmanStatus(salesmanIndex1, period, TargetKpiStatus.SUBMITTED);	
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		requestModel.id = salesmanIndex1.getId();
		requestModel.period = period;
		ResponseModel response = runUsecaseAndGetResponseModel(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.STATUS_IS_NOT_DRAFT, response.errorMessage);
	}
	

	
	@Test
	@Order(4)
	public void whenTargetKpisEmpty_thenError() {
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		requestModel.id = salesmanIndex0.getId();
		requestModel.period = YearMonth.of(2017, 6);
		ResponseModel response = runUsecaseAndGetResponseModel(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.TARGET_KPI_IS_EMPTY, response.errorMessage);
	}

	@Test
	@Order(5)
	public void whenTargetFieldsEmpty_thenSuccess() {
		YearMonth period = YearMonth.of(2017,6);
		createTargetKpiWithValueFields(salesmanIndex0, period);
		SalesmanKpiRequestModel requestModel = new SalesmanKpiRequestModel();
		requestModel.id = salesmanIndex0.getId();
		requestModel.period = period;
		ResponseModel response = runUsecaseAndGetResponseModel(requestModel);
		assertTrue(response.isSuccess);
		Salesman salesman = kpiGateway.getSalesmanById(salesmanIndex0.getId());
		Optional<KpiPeriod> optionalKpiPeriod = salesman.getKpiPeriods().stream().filter(kpiPeriod -> kpiPeriod.getPeriod().equals(period)).findAny();
		assertEquals(TargetKpiStatus.SUBMITTED, optionalKpiPeriod.get().getKpiStatus());
	}
	
	private ResponseModel runUsecaseAndGetResponseModel(SalesmanKpiRequestModel requestModel) {
		Usecase<?,?> usecase = new SubmitTargetKpiStatusUsecase(requestModel);
		usecase.run();
		return usecase.getResponseModel();
	}
	 
	private void createTargetKpiWithValueFields(Salesman salesman, YearMonth period) {
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 2000000, 100, idUnitKpi1);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 80, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		createTargetKpi(salesman, period, targetKpis);
	}
	
	private void createTargetKpi(Salesman salesman, YearMonth period, List<SalesmanTargetKpiRequestModel> targetKpis) {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesman.getId();
		requestModel.period = period;
		requestModel.targetKpiSalesman = targetKpis;
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
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
	}
	
	private void changeSalesmanStatus(Salesman salesman, YearMonth period, TargetKpiStatus status) {
		Salesman submitStatusSalesman = new Salesman();
		submitStatusSalesman.setId(salesman.getId());
		KpiPeriod submitPeriod = new KpiPeriod();
		submitPeriod.setPeriod(period);
		submitPeriod.setKpiStatus(status);
		List<KpiPeriod> submitPeriods = new ArrayList<>();
		submitPeriods.add(submitPeriod);
		submitStatusSalesman.setKpiPeriods(submitPeriods);
		kpiGateway.updateTargetKpiStatus(submitStatusSalesman);
	}
	
	private SalesmanTargetKpiRequestModel createSalesmanTargetKpiRequest(int bobot, int target, int targetPoint, String idUnitKpi) {
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
