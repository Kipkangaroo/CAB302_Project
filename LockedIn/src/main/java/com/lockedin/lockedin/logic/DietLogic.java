package com.lockedin.lockedin.logic;

/**
 * Logic class for diet-related calculations and numeric input validation.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class DietLogic {
    /**
     * Adds a new value to the current total.
     *
     * @param current the existing value
     * @param added the value to add
     * @return the sum of current and added
     */
    public double add(double current, double added) {
        return current + added;
    }

    /**
     * Checks whether a string represents a non-negative numeric value.
     *
     * @param value the text to validate
     * @return true if the value parses as a non-negative number; otherwise false
     */
    public boolean isValidNumber(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            return Double.parseDouble(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
