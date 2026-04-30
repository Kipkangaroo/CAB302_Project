package com.lockedin.lockedin.logic;
/**
 * Logic class for handling basic diet-related calculations and validation.
 * Provides simple arithmetic and numeric input checking for the Diet feature.
 */
public class DietLogic {
    /**
     * Adds a new value to the current total.
     *
     * @param current the existing value
     * @param added   the value to add
     * @return the sum of current and added
     */
    public double add(double current, double added){
        return current + added;
    }

    public boolean isValidNumber(String value){
        if(value == null || value.isEmpty()){
            return false;
        }
        try{
            return Double.parseDouble(value) >= 0;
        }
        catch(NumberFormatException e){
            return false;
        }
    }
}
