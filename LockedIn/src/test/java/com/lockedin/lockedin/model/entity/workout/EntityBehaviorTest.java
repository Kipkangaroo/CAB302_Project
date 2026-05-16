package com.lockedin.lockedin.model.entity.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.lockedin.lockedin.model.entity.user.User;

import org.junit.jupiter.api.Test;

public class EntityBehaviorTest {

    @Test
    void exerciseImageUrl_usesImageIdAndOption() {
        Exercise exercise =
                new Exercise(7, "Push Up", "Keep body straight", "Strength", "Chest", "push-up");

        assertEquals(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"
                        + "push-up/1.jpg",
                exercise.getExerciseImageUrl(1));
    }

    @Test
    void exerciseToString_returnsExerciseName() {
        Exercise exercise =
                new Exercise(3, "Squat", "Brace and descend", "Strength", "Legs", "squat");

        assertEquals("Squat", exercise.toString());
    }

    @Test
    void workoutExerciseEntry_shortConstructor_defaultsIdAndRestSeconds() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(4, "Bench Press", 5, 8);

        assertEquals(0, entry.getId());
        assertEquals(60, entry.getRestSeconds());
    }

    @Test
    void workoutExerciseEntryToString_formatsExerciseSummary() {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(2, 4, "Deadlift", 3, 5, 120);

        assertEquals("Deadlift  \u2013  3 sets \u00d7 5 reps", entry.toString());
    }

    @Test
    void userGetHash_returnsDeterministicSha256HexNotPlainText() {
        String hash = User.getHash("Password1!");

        assertEquals(hash, User.getHash("Password1!"));
        assertEquals(64, hash.length());
        assertFalse(hash.isBlank());
        assertNotEquals("Password1!", hash);
    }
}
