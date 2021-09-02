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

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.SalesmanKpiRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.presenter.TargetKpiPresenter;
import com.bit.core.presenter.TargetKpiPresenterImpl;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

@RestController
@RequestMapping("target-kpis")
public class TargetKpiController {

	TargetKpiPresenter presenter;
	
	public TargetKpiController() {
		presenter = new TargetKpiPresenterImpl();
	}
	
	@PostMapping("many")
	public FindManyResponseModel<Salesman> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody SalesmanRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.findAllSubordinateByCode(requestModel);
	}
	
	@PatchMapping
	public ResponseModel update(@RequestHeader HttpHeaders requestHeader, @RequestBody SalesmanKpiRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.update(requestModel);
	}
	
	@GetMapping("{id}")
	public DetailResponseModel<Salesman> get(@RequestHeader HttpHeaders requestHeader, @PathVariable String id){
		SalesmanRequestModel requestModel = new SalesmanRequestModel();
		requestModel.token = getTokenFromHeader(requestHeader);
		requestModel.id = id;
		return presenter.findById(requestModel);
	}
	
	@PatchMapping("status")
	public ResponseModel activate(@RequestHeader HttpHeaders requestHeader, @RequestBody SalesmanKpiRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.updateTargetKpiStatus(requestModel);
	}
	
	@PatchMapping("submits")
	public ResponseModel submits(@RequestHeader HttpHeaders requestHeader, @RequestBody SalesmanKpiRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.submitTargetsKpis(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
