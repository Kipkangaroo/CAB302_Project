package com.lockedin.lockedin.model.entity.user;

/**
 * User fitness goal for calorie and macro targets; display names match database values.
 *
 * @author LockedIn Team
 * @version 1.0
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

    /**
     * Creates a fitness goal with display label, calorie adjustment, and macro ratios.
     *
     * @param displayName the label shown in the UI and stored in the database
     * @param calorieAdjustment daily calorie offset from TDEE (kcal)
     * @param proteinRatio fraction of calories from protein
     * @param carbsRatio fraction of calories from carbohydrates
     * @param fatsRatio fraction of calories from fats
     */
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

    /**
     * Resolves an enum constant from its display name.
     * @param displayName The display name.
     * @return The matching enum constant, or null if not found.
     */
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

            /**
     * Returns the display name.
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

            /**
     * Returns the calorie adjustment.
     * @return calorie adjustment
     */
    public double getCalorieAdjustment() {
        return calorieAdjustment;
    }

            /**
     * Returns the protein ratio.
     * @return protein ratio
     */
    public double getProteinRatio() {
        return proteinRatio;
    }

            /**
     * Returns the carbs ratio.
     * @return carbs ratio
     */
    public double getCarbsRatio() {
        return carbsRatio;
    }

            /**
     * Returns the fats ratio.
     * @return fats ratio
     */
    public double getFatsRatio() {
        return fatsRatio;
    }
    /**
     * Returns a string representation of this object.
     * @return The resulting text.
     */

    @Override
    public String toString() {
        return displayName;
    }
}
