package com.bit.core.utils;

import java.util.Collection;

public class CollectionUtils {
	public static boolean isEmpty(Collection<?> it) {
		if(it == null) return true;
		if(it.isEmpty()) return true;
		return false;
	}
}
