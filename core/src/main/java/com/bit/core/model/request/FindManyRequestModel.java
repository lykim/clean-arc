package com.bit.core.model.request;

import java.util.HashMap;
import java.util.Map;

import com.bit.core.constant.Direction;
import com.bit.core.model.request.base.RequestModel;

public class FindManyRequestModel extends RequestModel{
	public Direction direction = Direction.ASCENDING;
	public String orderBy;
	public int pageSize;
	public int pageNumber = 1;
	public Map<String,String> filterEqual = new HashMap<>();
}
