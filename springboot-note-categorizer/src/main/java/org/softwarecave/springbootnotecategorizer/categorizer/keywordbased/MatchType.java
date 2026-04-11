package org.softwarecave.springbootnotecategorizer.categorizer.keywordbased;

public enum MatchType {
    SHOULD("should"),
    MAY("may");

    private final String value;

    MatchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MatchType fromString(String value) {
        for (MatchType type : MatchType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MatchType: " + value);
    }
}
