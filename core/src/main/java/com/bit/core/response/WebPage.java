package com.bit.core.response;

import java.util.List;

import com.bit.core.entity.base.Entity;
import com.bit.core.response.base.Page;

public class WebPage<E extends Entity> implements Page<E>{
	List<E> result;
	int size;
	int allRows;
	
	public WebPage(List<E> result, int allRows, int size) {
		this.result = result;
		this.size = size;
		this.allRows = allRows;
	}
	
	@Override
	public List<E> getResult() {
		return result;
	}

	@Override
	public int getNumOfPages() {
		if(size > 0) return (int)Math.ceil(allRows / size);
		return 1;
	}

	@Override
	public long getTotalRows() {
		return this.allRows;
	}
	

}
