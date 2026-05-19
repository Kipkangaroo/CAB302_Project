package com.lockedin.lockedin.model.entity.user;

/**
 * User fitness goal for calorie and macro targets. {@link #getDisplayName()} matches
 * values stored in the database.
 */
public enum FitnessGoal {
    LOSE_WEIGHT("Lose Weight", -500, 0.35, 0.35, 0.30),
    BUILD_MUSCLE("Build Muscle", 500, 0.30, 0.50, 0.20),
    MAINTAIN_FITNESS("Maintain Fitness", 0, 0.25, 0.45, 0.30);

    private final String displayName;
    private final double calorieAdjustment;
    private final double proteinRatio;
    private final double carbsRatio;
    private final double fatsRatio;

    FitnessGoal(
            String displayName,
            double calorieAdjustment,
            double proteinRatio,
            double carbsRatio,
            double fatsRatio) {
        this.displayName = displayName;
        this.calorieAdjustment = calorieAdjustment;
        this.proteinRatio = proteinRatio;
        this.carbsRatio = carbsRatio;
        this.fatsRatio = fatsRatio;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCalorieAdjustment() {
        return calorieAdjustment;
    }

    public double getProteinRatio() {
        return proteinRatio;
    }

    public double getCarbsRatio() {
        return carbsRatio;
    }

    public double getFatsRatio() {
        return fatsRatio;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static FitnessGoal fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        for (FitnessGoal goal : values()) {
            if (goal.displayName.equals(displayName)) {
                return goal;
            }
        }
        return null;
    }
}
