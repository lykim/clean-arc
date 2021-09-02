package com.bit.core.response.base;

import java.util.List;

public interface Page<E> {
	List<E> getResult();
	int getNumOfPages();
	long getTotalRows();
}
