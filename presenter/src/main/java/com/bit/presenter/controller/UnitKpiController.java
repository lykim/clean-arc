package com.bit.presenter.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.core.entity.UnitKpi;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.model.request.UnitKpiRequestModel;
import com.bit.core.presenter.UnitKpiPresenter;
import com.bit.core.presenter.UnitKpiPresenterImpl;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

@RestController
@RequestMapping("unit-kpis")
public class UnitKpiController {

	UnitKpiPresenter presenter;
	
	public UnitKpiController() {
		presenter = new UnitKpiPresenterImpl();
	}
	
	@PostMapping
	public CreateResponseModel create(@RequestHeader HttpHeaders requestHeader, @RequestBody UnitKpiRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.create(requestModel);
	}
	
	@PostMapping("many")
	public FindManyResponseModel<UnitKpi> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody FindManyUnitKpiRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.findAll(requestModel);
	}
	
	@PatchMapping
	public ResponseModel update(@RequestHeader HttpHeaders requestHeader, @RequestBody UnitKpiRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.update(requestModel);
	}
	
	@GetMapping("{id}")
	public DetailResponseModel<UnitKpi> get(@RequestHeader HttpHeaders requestHeader, @PathVariable String id){
		UnitKpiRequestModel requestModel = new UnitKpiRequestModel();
		requestModel.token = getTokenFromHeader(requestHeader);
		requestModel.id = id;
		return presenter.findById(requestModel);
	}
	
	@PatchMapping("activate")
	public ResponseModel activate(@RequestHeader HttpHeaders requestHeader, @RequestBody ActiveInactiveRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.setActive(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
