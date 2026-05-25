package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Unit tests for User, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class UserTest {

    /**
     * Builds a user with the given date of birth for age and macro calculations.
     *
     * @param dob date of birth
     * @return configured user
     */
    private User makeUserWithDob(LocalDate dob) {
        return new User(
                0,
                "Test",
                "User",
                "test@test.com",
                dob,
                170.0,
                70.0,
                "Male",
                ActivityLevel.SEDENTARY,
                FitnessGoal.MAINTAIN_FITNESS,
                "Password1!");
    }

    // Birthday tomorrow: person has NOT yet reached their next birthday this year

    /**
     * Verifies getAge: birthday Not Yet Passed This Year returns Age Minus One.
     */
    @Test
    void getAge_birthdayNotYetPassedThisYear_returnsAgeMinusOne() {
        LocalDate dob = LocalDate.now().minusYears(25).plusDays(1);
        assertEquals(24, makeUserWithDob(dob).getAge());
    }

    // Birthday yesterday: person HAS already passed their birthday this year

    /**
     * Verifies getAge: birthday Already Passed This Year returns Full Age.
     */
    @Test
    void getAge_birthdayAlreadyPassedThisYear_returnsFullAge() {
        LocalDate dob = LocalDate.now().minusYears(25).minusDays(1);
        assertEquals(25, makeUserWithDob(dob).getAge());
    }

    // Birthday is today: counts as having passed

    /**
     * Verifies getAge: birthday Is Today returns Full Age.
     */
    @Test
    void getAge_birthdayIsToday_returnsFullAge() {
        LocalDate dob = LocalDate.now().minusYears(30);
        assertEquals(30, makeUserWithDob(dob).getAge());
    }


    /**
     * Verifies getBMR: male uses Male Formula.
     */
    @Test
    void getBMR_male_usesMaleFormula() {
        User user = makeUserWithDob(LocalDate.of(1990, 1, 1));
        user.setSex("Male");
        user.setWeight(80);
        user.setHeight(180);
        double expected = (10 * 80) + (6.25 * 180) - (5 * user.getAge()) + 5;
        assertEquals(expected, user.getBMR(), 0.01);
    }


    /**
     * Verifies getTDEE: applies Activity Multiplier.
     */
    @Test
    void getTDEE_appliesActivityMultiplier() {
        User user = makeUserWithDob(LocalDate.of(1990, 1, 1));
        user.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        assertEquals(user.getBMR() * 1.55, user.getTDEE(), 0.01);
    }


    /**
     * Verifies getTargetCalories: adds Goal Adjustment.
     */
    @Test
    void getTargetCalories_addsGoalAdjustment() {
        User user = makeUserWithDob(LocalDate.of(1990, 1, 1));
        user.setFitnessGoal(FitnessGoal.BUILD_MUSCLE);
        assertEquals(user.getTDEE() + 500, user.getTargetCalories(), 0.01);
    }


    /**
     * Verifies getTargetProtein: uses Goal Ratio.
     */
    @Test
    void getTargetProtein_usesGoalRatio() {
        User user = makeUserWithDob(LocalDate.of(1990, 1, 1));
        double calories = 2000;
        double expected = (calories * FitnessGoal.BUILD_MUSCLE.getProteinRatio()) / 4.0;
        assertEquals(expected, user.getTargetProtein(calories, FitnessGoal.BUILD_MUSCLE), 0.01);
    }


    /**
     * Verifies getHash: returns Deterministic Sha256Hex.
     */
    @Test
    void getHash_returnsDeterministicSha256Hex() {
        String hash = User.getHash("Password1!");
        assertEquals(hash, User.getHash("Password1!"));
        assertEquals(64, hash.length());
        assertFalse(hash.isBlank());
        assertFalse(hash.equals("Password1!"));
    }
}
