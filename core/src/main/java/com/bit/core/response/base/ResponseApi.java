package com.bit.core.response.base;

import java.util.List;

public class ResponseApi<T> {
	public boolean success;
	public String error;
	public List<T> data;
}
