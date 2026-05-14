package com.lockedin.lockedin.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class UserTest {

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
                "Sedentary (little/no exercise)",
                "Maintain Fitness",
                "Password1!");
    }

    // Birthday tomorrow: person has NOT yet reached their next birthday this year
    @Test
    void getAge_birthdayNotYetPassedThisYear_returnsAgeMinusOne() {
        LocalDate dob = LocalDate.now().minusYears(25).plusDays(1);
        assertEquals(24, makeUserWithDob(dob).getAge());
    }

    // Birthday yesterday: person HAS already passed their birthday this year
    @Test
    void getAge_birthdayAlreadyPassedThisYear_returnsFullAge() {
        LocalDate dob = LocalDate.now().minusYears(25).minusDays(1);
        assertEquals(25, makeUserWithDob(dob).getAge());
    }

    // Birthday is today: counts as having passed
    @Test
    void getAge_birthdayIsToday_returnsFullAge() {
        LocalDate dob = LocalDate.now().minusYears(30);
        assertEquals(30, makeUserWithDob(dob).getAge());
    }
}
