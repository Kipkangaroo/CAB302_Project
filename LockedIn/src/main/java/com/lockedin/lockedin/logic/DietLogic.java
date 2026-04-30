package com.lockedin.lockedin.logic;

public class DietLogic {
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
