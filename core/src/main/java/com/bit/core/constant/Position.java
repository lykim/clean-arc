package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum Position {
	BRAND_MANAGER(1), SUPERVISOR(2);
	
	private final int code;
	private static final Map<Integer, Position> lookup = new HashMap<>();
	static {
        for (Position d : Position.values()) {
            lookup.put(d.getCode(), d);
        }
    }

    private Position(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Position get(int code) {
        return lookup.get(code);
    }
}
