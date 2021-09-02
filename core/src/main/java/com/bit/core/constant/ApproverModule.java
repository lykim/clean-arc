package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum ApproverModule {
	TARGET_KPI("target kpi"),TEST("test module");
	
	private final String abbreviation;
	private static final Map<String, ApproverModule> lookup = new HashMap<String, ApproverModule>();
	static {
        for (ApproverModule d : ApproverModule.values()) {
            lookup.put(d.getAbbreviation(), d);
        }
    }

    private ApproverModule(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static ApproverModule get(String abbreviation) {
        return lookup.get(abbreviation);
    }
}
