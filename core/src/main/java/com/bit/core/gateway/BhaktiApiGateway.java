package com.bit.core.gateway;

import com.bit.core.entity.Salesman;
import com.bit.core.response.base.Page;

public interface BhaktiApiGateway extends Gateway{
	Page<Salesman> getSubordinateSalesmanBySalesmanCode(String salesmanCode);
}
