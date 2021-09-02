package com.bit.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
	ASCENDING("ASC"), DESCENDING("DESC");
	
	private final String abbreviation;
	private static final Map<String, Direction> lookup = new HashMap<String, Direction>();
	static {
        for (Direction d : Direction.values()) {
            lookup.put(d.getAbbreviation(), d);
        }
    }

    private Direction(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static Direction get(String abbreviation) {
        return lookup.get(abbreviation);
    }
}
