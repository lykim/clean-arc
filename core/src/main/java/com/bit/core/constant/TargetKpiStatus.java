package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum TargetKpiStatus {
	DRAFT("DRF"), SUBMITTED("SMT"), APPROVED("APR");
	private final String abbreviation;
	private static final Map<String, TargetKpiStatus> lookup = new HashMap<String, TargetKpiStatus>();
	static {
        for (TargetKpiStatus d : TargetKpiStatus.values()) {
            lookup.put(d.getAbbreviation(), d);
        }
    }

    private TargetKpiStatus(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static TargetKpiStatus get(String abbreviation) {
        return lookup.get(abbreviation);
    }
}
