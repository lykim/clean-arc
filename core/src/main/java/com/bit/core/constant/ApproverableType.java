package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum ApproverableType {
	SIMPLE("simple"),TEST("test");
	
	private final String abbreviation;
	private static final Map<String, ApproverableType> lookup = new HashMap<String, ApproverableType>();
	static {
        for (ApproverableType d : ApproverableType.values()) {
            lookup.put(d.getAbbreviation(), d);
        }
    }

    private ApproverableType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static ApproverableType get(String abbreviation) {
        return lookup.get(abbreviation);
    }
}
