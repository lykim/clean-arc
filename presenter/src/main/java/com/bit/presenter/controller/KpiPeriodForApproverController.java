package com.bit.presenter.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.core.entity.Salesman;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.presenter.KpiPeriodPresenter;
import com.bit.core.presenter.KpiPeriodPresenterImpl;
import com.bit.core.response.FindManyResponseModel;

@RestController
@RequestMapping("approver/kpiPeriods")
public class KpiPeriodForApproverController {
	
	KpiPeriodPresenter presenter;
	
	public KpiPeriodForApproverController() {
		presenter = new KpiPeriodPresenterImpl();
	}
	
	@PostMapping("many")
	public FindManyResponseModel<Salesman> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody FindManyRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.findForApprover(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
