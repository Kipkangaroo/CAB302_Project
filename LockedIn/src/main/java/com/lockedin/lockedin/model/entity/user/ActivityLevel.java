package com.lockedin.lockedin.model.entity.user;

/**
 * User activity level for TDEE calculation. {@link #getDisplayName()} matches
 * values stored in the database.
 */
public enum ActivityLevel {
    SEDENTARY("Sedentary (little/no exercise)", 1.2),
    LIGHTLY_ACTIVE("Lightly active (1–3 days/week)", 1.375),
    MODERATELY_ACTIVE("Moderately active (3–5 days/week)", 1.55),
    VERY_ACTIVE("Very active (6–7 days/week)", 1.725),
    EXTRA_ACTIVE("Extra active (physical job + exercise)", 1.9);

    private final String displayName;
    private final double tdeeMultiplier;

    ActivityLevel(String displayName, double tdeeMultiplier) {
        this.displayName = displayName;
        this.tdeeMultiplier = tdeeMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getTdeeMultiplier() {
        return tdeeMultiplier;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ActivityLevel fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        for (ActivityLevel level : values()) {
            if (level.displayName.equals(displayName)) {
                return level;
            }
        }
        return null;
    }
}
