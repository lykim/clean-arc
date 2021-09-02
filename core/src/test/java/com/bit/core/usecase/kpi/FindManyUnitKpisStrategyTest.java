package com.bit.core.usecase.kpi;

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
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockUnitKpiGateway;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindUnitKpis;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindManyUnitKpisStrategyTest extends BaseUsecaseTest{
	private static String codeAbc = "abc";
	private static String codeCba = "cba";
	private static int numOfRows = 20;
	private static String token;
	private static RequestModelHelper<FindManyUnitKpiRequestModel> requestModelHelper;
	private static List<String> loginUnitKpis;
	private static List<String> viewUnitKpis;
	
	@BeforeAll
	public static void beforeAll() {
		viewUnitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW);
		loginUnitKpis = Arrays.asList(RoleCode.ROLE_UNIT_KPI_VIEW, RoleCode.ROLE_UNIT_KPI_CUD);
		token = TokenUtils.createJWT( "ruly", loginUnitKpis,  60000);
		requestModelHelper = new RequestModelHelper<FindManyUnitKpiRequestModel>(new FindManyUnitKpiRequestModel(), token);
		String code = codeAbc;
		String descriptionDomain = "@mail.com";
		for(int i=0; i < numOfRows; i++) {
			if(i >=10) {
				code = codeCba;
			}
			String name = code + "_" +i;
			createUnitKpi(name, name + descriptionDomain);			
		}
	}
	
	@Test
	public void givenSortAscending_thenSortedAscending() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.ASCENDING;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			if(i <10) {
				assertEquals(unitKpi.getName(), codeAbc+"_"+i);				
			}else {
				assertEquals(unitKpi.getName(), codeCba+"_"+i);
			}
		}
	}
	
	@Test
	public void givenSortDescending_thenSortedDescending() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(unitKpi.getName(), codeCba+"_"+index);				
			}else {
				assertEquals(unitKpi.getName(), codeAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenOrderBy_thenSortBasedByOrderBy() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		requestModel.orderBy = "name";
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(unitKpi.getName(), codeCba+"_"+index);				
			}else {
				assertEquals(unitKpi.getName(), codeAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenPageSize_thenResultSizeSameAsPageSize() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		assertEquals(requestModel.pageSize, result.size());
	}
	
	@Test
	public void givenPageSize_thenNumberOfPagesIsCountedCorrectly() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 3;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		Page<UnitKpi> page = response.page;
		assertTrue(response.isSuccess);
		int numPage = (int)Math.ceil(numOfRows / requestModel.pageSize);
		assertEquals(numPage, page.getNumOfPages());
	}
	
	@Test
	public void givenPage1With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		for(int i=0; i > result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			assertEquals(unitKpi.getName(), codeAbc+"_"+i);
		}
	}
	
	@Test
	public void givenPage2With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyUnitKpiRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 2;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			assertEquals(unitKpi.getName(), codeAbc+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize)));
		}
	}
	
	@Test
	public void givenUnitKpiname_thenSearchBasedByUnitKpiname() {
		FindManyUnitKpiRequestModel requestModel =requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		requestModel.nameLike = codeCba;
		int indexStart = requestModel.nameLike.equals(codeCba) ? 10 : 0;
		FindManyStrategy<UnitKpi, FindManyUnitKpiRequestModel> findManyStrategy = new FindUnitKpis(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<UnitKpi>(), findManyStrategy, viewUnitKpis);
		usecase.run();
		FindManyResponseModel<UnitKpi> response = (FindManyResponseModel<UnitKpi>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<UnitKpi> page = response.page;
		List<UnitKpi> result = page.getResult();
		assertEquals(2, page.getNumOfPages());
		for(int i=0; i < result.size(); i++) {
			UnitKpi unitKpi = result.get(i);
			assertEquals(unitKpi.getName(), codeCba+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize) + indexStart));
		}
	}
	
	private static String createUnitKpi(String code, String description) {
		UnitKpiRequestModel request = new UnitKpiRequestModel();
		request.token = token;
		request.name = code;
		request.description = description;
		Usecase<?, CreateResponseModel> usecase = new CreateUnitKpiUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}

}
