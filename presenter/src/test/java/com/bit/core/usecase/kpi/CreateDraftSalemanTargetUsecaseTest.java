package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class CreateDraftSalemanTargetUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static String unitKpiToken;
	private static RequestModelHelper<SalesmanKpiRequestModel> requestModelHelper;
	private static Salesman salesmanIndex0;
	private static String idUnitKpi1 = "";
	private static String idUnitKpi2 = "";
	private static String idUnitKpi3 = "";
	private static String username = "Zuser1";
	private static int JULY_PERIOD_TARGET_SIZE;
	
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
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.ID));
		assertEquals(NullValidator.VALIDATION_MESSAGE, response.validationMessages.get(SalesmanKpiRequestModel.Label.PERIOD));
	}
	
	@Test
	public void whenTotalBobotIsNotMax_willError() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesmanIndex0.getId();
		requestModel.period = YearMonth.now();
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.BOBOT_NOT_MAX, response.errorMessage);
	}
	
	@Test
	public void whenIdIsNotExist_willError() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = "noid";
		requestModel.period = YearMonth.now();
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	@Order(1)
	public void givenNewSalesmanTarget_thenCreateNew() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesmanIndex0.getId();
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 2000000, 100, idUnitKpi1);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 80, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		JULY_PERIOD_TARGET_SIZE = targetKpis.size();
		requestModel.targetKpiSalesman = targetKpis;
		requestModel.period = YearMonth.of(2021, 6);
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Salesman salesman =  kpiGateway.getSalesmanById(requestModel.id);
		List<KpiPeriod> kpiPeriods = salesman.getKpiPeriods();
		assertEquals(kpiPeriods.size(), 1);
		assertEquals(targetKpis.size(), kpiPeriods.get(0).getTargetKpis().size());
		kpiPeriods.forEach(kpiPeriod -> {
			assertEquals(TargetKpiStatus.DRAFT, kpiPeriod.getKpiStatus());
			assertEquals(requestModel.period, kpiPeriod.getPeriod());
			kpiPeriod.getTargetKpis().forEach(kpi -> {
				if(idUnitKpi1.equals( kpi.getUnitKpi().getId() )) {
					assertEquals(target1.bobot, kpi.getBobot());
					assertEquals(target1.target, kpi.getTarget());
					assertEquals(target1.targetPoint, kpi.getTargetPoint());
				}else {
					assertEquals(target2.bobot, kpi.getBobot());
					assertEquals(target2.target, kpi.getTarget());
					assertEquals(target2.targetPoint, kpi.getTargetPoint());
				}
			});
		});	
	}
	
	@Test
	@Order(2)
	public void givenUpdatedSalesmanTargetWithNewPeriod_thenInsert() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesmanIndex0.getId();
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 2000000, 100, idUnitKpi1);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 80, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		requestModel.targetKpiSalesman = targetKpis;
		requestModel.period = YearMonth.of(2021, 7);
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Salesman salesman =  kpiGateway.getSalesmanById(requestModel.id);
		List<KpiPeriod> kpiPeriods = salesman.getKpiPeriods();
		assertEquals(kpiPeriods.size(), 2);
		kpiPeriods.forEach(kpiPeriod -> {
			assertEquals(TargetKpiStatus.DRAFT, kpiPeriod.getKpiStatus());
			if(kpiPeriod.getPeriod().equals(requestModel.period)) {
				assertEquals(targetKpis.size(), kpiPeriod.getTargetKpis().size());
				kpiPeriod.getTargetKpis().forEach(kpi -> {
					if(idUnitKpi1.equals( kpi.getUnitKpi().getId() )) {
						assertEquals(target1.bobot, kpi.getBobot());
						assertEquals(target1.target, kpi.getTarget());
						assertEquals(target1.targetPoint, kpi.getTargetPoint());
					}else {
						assertEquals(target2.bobot, kpi.getBobot());
						assertEquals(target2.target, kpi.getTarget());
						assertEquals(target2.targetPoint, kpi.getTargetPoint());
					}
				});				
			}else {
				assertEquals(JULY_PERIOD_TARGET_SIZE, kpiPeriod.getTargetKpis().size());
			}
		});
	}
	
	@Test
	@Order(3)
	public void givenUpdatedSalesmanTargetWithNewPeriod_thenUpdate() {
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesmanIndex0.getId();
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 2500000, 100, idUnitKpi3);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 800, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		requestModel.targetKpiSalesman = targetKpis;
		requestModel.period = YearMonth.of(2021, 7);
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Salesman salesman =  kpiGateway.getSalesmanById(requestModel.id);
		List<KpiPeriod> kpiPeriods = salesman.getKpiPeriods();
		assertEquals(kpiPeriods.size(), 2);
		kpiPeriods.forEach(kpiPeriod -> {
			assertEquals(TargetKpiStatus.DRAFT, kpiPeriod.getKpiStatus());
			if(kpiPeriod.getPeriod().equals(requestModel.period)) {
				assertEquals(targetKpis.size(), kpiPeriod.getTargetKpis().size());
				kpiPeriod.getTargetKpis().forEach(kpi -> {
					if(idUnitKpi3.equals( kpi.getUnitKpi().getId() )) {
						assertEquals(target1.bobot, kpi.getBobot());
						assertEquals(target1.target, kpi.getTarget());
						assertEquals(target1.targetPoint, kpi.getTargetPoint());
					}else {
						assertEquals(target2.bobot, kpi.getBobot());
						assertEquals(target2.target, kpi.getTarget());
						assertEquals(target2.targetPoint, kpi.getTargetPoint());
					}
				});				
			}else {
				assertEquals(JULY_PERIOD_TARGET_SIZE, kpiPeriod.getTargetKpis().size());
			}
		});
	}
	
	@Test
	@Order(4)
	public void updatingStatusNotDraft_ThenError() {
		changeSalesmanStatus(salesmanIndex0, TargetKpiStatus.SUBMITTED);
		SalesmanKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = salesmanIndex0.getId();
		List<SalesmanTargetKpiRequestModel> targetKpis = new ArrayList<>();
		SalesmanTargetKpiRequestModel target1 = createSalesmanTargetKpiRequest(50, 100, 100, idUnitKpi3);
		SalesmanTargetKpiRequestModel target2 = createSalesmanTargetKpiRequest(50, 100, 100, idUnitKpi2);
		targetKpis.add(target1);
		targetKpis.add(target2);
		requestModel.targetKpiSalesman = targetKpis;
		requestModel.period = salesmanIndex0.getKpiPeriods().get(0).getPeriod() ;
		Usecase<?,?> usecase = new CreateDraftTargetKpiSalesmanUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.STATUS_IS_NOT_DRAFT, response.errorMessage);
	}
	
	private void changeSalesmanStatus(Salesman inputSalesman, TargetKpiStatus status) {
		Salesman salesman = kpiGateway.getSalesmanById(inputSalesman.getId());
		Salesman submitStatusSalesman = new Salesman();
		submitStatusSalesman.setId(salesman.getId());
		KpiPeriod submitPeriod = salesman.getKpiPeriods().get(0);
		submitPeriod.setKpiStatus(status);
		List<KpiPeriod> submitPeriods = new ArrayList<>();
		submitPeriods.add(submitPeriod);
		submitStatusSalesman.setKpiPeriods(submitPeriods);
		inputSalesman.setKpiPeriods(submitPeriods);
		kpiGateway.updateTargetKpiStatus(submitStatusSalesman);
	}
	
	private static void createSalesmanFromApi() {
		List<String> myRoles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		SalesmanRequestModel requestModel = new SalesmanRequestModel();
		requestModel.token = TokenUtils.createJWT( username, myRoles,  60000);
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesman(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy, myRoles);
		usecase.run();
		FindManyResponseModel<Salesman> response =  (FindManyResponseModel<Salesman>)usecase.getResponseModel();
		List<Salesman> salesmans = response.page.getResult();
		salesmanIndex0 = salesmans.get(0);
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
