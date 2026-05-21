package com.lockedin.lockedin.model.entity.user;

/**
 * User activity level for TDEE calculation; display names match database values.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public enum ActivityLevel {
    SEDENTARY("Sedentary (little/no exercise)", 1.2),
    LIGHTLY_ACTIVE("Lightly active (1–3 days/week)", 1.375),
    MODERATELY_ACTIVE("Moderately active (3–5 days/week)", 1.55),
    VERY_ACTIVE("Very active (6–7 days/week)", 1.725),
    EXTRA_ACTIVE("Extra active (physical job + exercise)", 1.9);

    private final String displayName;
    private final double tdeeMultiplier;

    /**
     * Creates an activity level with display label and TDEE multiplier.
     *
     * @param displayName the label shown in the UI and stored in the database
     * @param tdeeMultiplier the multiplier applied to BMR when calculating TDEE
     */
    ActivityLevel(String displayName, double tdeeMultiplier) {
        this.displayName = displayName;
        this.tdeeMultiplier = tdeeMultiplier;
    }

    /**
     * Resolves an enum constant from its display name.
     * @param displayName The display name.
     * @return The matching enum constant, or null if not found.
     */
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

    /**
     * Returns the display name.
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the tdee multiplier.
     * @return The tdee multiplier.
     */
    public double getTdeeMultiplier() {
        return tdeeMultiplier;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return the display name of this activity level
     */
    @Override
    public String toString() {
        return displayName;
    }
}
