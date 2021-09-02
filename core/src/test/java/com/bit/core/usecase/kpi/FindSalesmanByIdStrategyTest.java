package com.bit.core.usecase.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Salesman;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.model.request.SalesmanTargetKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findMany.FindSubordinateSalesman;
import com.bit.core.strategy.findOne.FindSalesmanById;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindSalesmanByIdStrategyTest  extends BaseUsecaseTest {
	private static String token;
	private static String unitKpiToken;
	private static RequestModelHelper<SalesmanKpiRequestModel> requestModelHelper;
//	private final static String SALESMAN_CODE = "SPS-A01";
	private static Salesman salesmanIndex0;
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
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findByCode_willReturnData() {
		SalesmanRequestModel request = new SalesmanRequestModel();
		request.token = token;
		request.id = salesmanIndex0.getId();
		FindOneStrategy<Salesman, SalesmanRequestModel> findOneStrategy = new FindSalesmanById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		token = TokenUtils.createJWT( username, roles,  60000);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Salesman>(), findOneStrategy, roles);
		usecase.run();
		DetailResponseModel<Salesman> response = (DetailResponseModel<Salesman>) usecase.getResponseModel();
		assertEquals( response.entity.getCode(), salesmanIndex0.getCode());
		assertEquals( response.entity.getName(), salesmanIndex0.getName());
		assertEquals( response.entity.getDivision(), salesmanIndex0.getDivision());
	}
	
	private static void createSalesmanFromApi() {
		List<String> myRoles = Arrays.asList(RoleCode.ROLE_SALESMAN_VIEW);
		SalesmanRequestModel requestModel = new SalesmanRequestModel();
		requestModel.token = TokenUtils.createJWT( username, myRoles,  60000);
		FindManyStrategy<Salesman, SalesmanRequestModel> findManyStrategy = new FindSubordinateSalesman(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Salesman>(), findManyStrategy, myRoles);
		usecase.run();
		FindManyResponseModel<Salesman> response = (FindManyResponseModel<Salesman>)usecase.getResponseModel();
		salesmanIndex0 = response.page.getResult().get(0);
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
