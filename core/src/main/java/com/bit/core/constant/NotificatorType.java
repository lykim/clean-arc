package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum NotificatorType {
	EMAIL("email");
	
	private final String abbreviation;
	private static final Map<String, NotificatorType> lookup = new HashMap<String, NotificatorType>();
	static {
        for (NotificatorType d : NotificatorType.values()) {
            lookup.put(d.getAbbreviation(), d);
        }
    }

    private NotificatorType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static NotificatorType get(String abbreviation) {
        return lookup.get(abbreviation);
    }
}
