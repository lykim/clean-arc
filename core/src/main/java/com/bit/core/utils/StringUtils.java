package com.bit.core.utils;

import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {
	public static boolean isNotEmpty(String token) {
		return token != null && token.trim().length() >= 1;
	}
	public static String convertWithStream(Map<?, ?> map) {
	    String mapAsString = map.keySet().stream()
	      .map(key -> key + "=" + map.get(key))
	      .collect(Collectors.joining(", ", "{", "}"));
	    return mapAsString;
	}
}
