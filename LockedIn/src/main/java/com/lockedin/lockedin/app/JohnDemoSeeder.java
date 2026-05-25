package com.lockedin.lockedin.app;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lockedin.lockedin.model.dao.ExercisesDAO;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.OtpDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.diet.Food;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;

/**
 * Seeds a demo user "John" with sample diet, workout, and progress data for
 * demonstrations.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public final class JohnDemoSeeder {

    private static final String JOHN_EMAIL = "john.demo@lockedin.app";
    private static final double START_WEIGHT_KG = 60.0;
    private static final double END_WEIGHT_KG = 65.0;

    /**
     * Creates a new JohnDemoSeeder.
     */
    private JohnDemoSeeder() {
    }

    /**
     * Removes the demo user and all related data created by
     * {@link #seedIfAbsent()}.
     * No-op if John does not exist.
     */
    public static void removeAll() {
        UserDAO userDAO = new UserDAO();
        Optional<User> john = userDAO.getUserByEmail(JOHN_EMAIL);
        if (john.isEmpty()) {
            return;
        }
        int userId = john.get().getId();
        new FoodDAO().deleteAllForUser(userId);
        new UserProgressDAO().deleteAllForUser(userId);
        new WorkoutRoutineDAO().deleteAllForUser(userId);
        new OtpDAO().deleteOtp(JOHN_EMAIL);
        userDAO.deleteUser(userId);
    }

    /**
     * Seeds demo data for John if the demo account does not already exist.
     */
    public static void seedIfAbsent() {
        UserDAO userDAO = new UserDAO();
        if (userDAO.getUserByEmail(JOHN_EMAIL).isPresent()) {
            return;
        }

        User john = buildJohnUser();
        if (!userDAO.createUser(john)) {
            return;
        }

        Optional<User> saved = userDAO.getUserByEmail(JOHN_EMAIL);
        if (saved.isEmpty()) {
            return;
        }
        john = saved.get();
        int userId = john.getId();

        ExercisesDAO exercisesDAO = new ExercisesDAO();
        WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
        FoodDAO foodDAO = new FoodDAO();
        UserProgressDAO progressDAO = new UserProgressDAO();

        List<WorkoutRoutineDAO.RoutineData> routines = createWorkoutRoutines(userId, exercisesDAO, workoutDAO);
        seedFebruaryData(john, userId, routines, foodDAO, progressDAO, workoutDAO);

        userDAO.updateWeight(userId, END_WEIGHT_KG);
        userDAO.updateFitnessGoal(userId, FitnessGoal.MAINTAIN_FITNESS);
    }

    /**
     * Performs build john user.
     */
    private static User buildJohnUser() {
        final String johnPassword = "Password1!";
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Mitchell");
        user.setEmail(JOHN_EMAIL);
        user.setDateOfBirth(LocalDate.of(1996, 3, 15));
        user.setHeight(178);
        user.setWeight(START_WEIGHT_KG);
        user.setSex("Male");
        user.setActivityLevel(ActivityLevel.VERY_ACTIVE);
        user.setFitnessGoal(FitnessGoal.BUILD_MUSCLE);
        user.setPasswordHash(User.getHash(johnPassword));
        return user;
    }

    /**
     * Performs create workout routines.
     * 
     * @param userId       The user id.
     * @param exercisesDAO The exercises dao.
     * @param workoutDAO   The workout dao.
     * @return A list of matching records.
     */
    private static List<WorkoutRoutineDAO.RoutineData> createWorkoutRoutines(
            int userId, ExercisesDAO exercisesDAO, WorkoutRoutineDAO workoutDAO) {
        List<RoutineTemplate> templates = List.of(
                new RoutineTemplate(
                        "Push Day",
                        "Chest, shoulders, and triceps.",
                        List.of(
                                entry(exercisesDAO, "Barbell Bench Press - Medium Grip", 4, 8, 90),
                                entry(exercisesDAO, "Barbell Incline Bench Press - Medium Grip", 3, 10, 75),
                                entry(exercisesDAO, "Cable Chest Press", 3, 12, 60),
                                entry(exercisesDAO, "Alternating Cable Shoulder Press", 3, 10, 60),
                                entry(exercisesDAO, "Anti-Gravity Press", 3, 12, 45),
                                entry(exercisesDAO, "Cable Hammer Curls - Rope Attachment", 3, 12, 45),
                                entry(exercisesDAO, "Cable Preacher Curl", 3, 10, 45))),
                new RoutineTemplate(
                        "Pull Day",
                        "Back and biceps.",
                        List.of(
                                entry(exercisesDAO, "Barbell Deadlift", 4, 6, 120),
                                entry(exercisesDAO, "Barbell Rear Delt Row", 3, 10, 75),
                                entry(exercisesDAO, "Dumbbell Incline Row", 3, 10, 60),
                                entry(exercisesDAO, "Cable Rope Rear-Delt Rows", 3, 12, 60),
                                entry(exercisesDAO, "Pull Through", 3, 12, 60),
                                entry(exercisesDAO, "Barbell Curl", 3, 10, 60),
                                entry(exercisesDAO, "Barbell Curls Lying Against An Incline", 3, 12, 45))),
                new RoutineTemplate(
                        "Leg Day",
                        "Quads, hamstrings, and calves.",
                        List.of(
                                entry(exercisesDAO, "Barbell Squat", 4, 8, 120),
                                entry(exercisesDAO, "Barbell Walking Lunge", 3, 10, 90),
                                entry(exercisesDAO, "Barbell Side Split Squat", 3, 10, 75),
                                entry(exercisesDAO, "Bodyweight Squat", 3, 15, 45),
                                entry(exercisesDAO, "Barbell Seated Calf Raise", 4, 15, 45),
                                entry(exercisesDAO, "Pull Through", 3, 12, 60))),
                new RoutineTemplate(
                        "Upper Body",
                        "Balanced upper-body strength.",
                        List.of(
                                entry(exercisesDAO, "Barbell Bench Press - Medium Grip", 3, 10, 90),
                                entry(exercisesDAO, "Barbell Incline Bench Press - Medium Grip", 3, 10, 75),
                                entry(exercisesDAO, "Barbell Deadlift", 3, 8, 120),
                                entry(exercisesDAO, "Barbell Rear Delt Row", 3, 10, 75),
                                entry(exercisesDAO, "Alternating Cable Shoulder Press", 3, 10, 60),
                                entry(exercisesDAO, "Barbell Curl", 3, 10, 60),
                                entry(exercisesDAO, "Cable Chest Press", 3, 12, 60),
                                entry(exercisesDAO, "Dumbbell Incline Row", 3, 10, 60))),
                new RoutineTemplate(
                        "Full Body",
                        "Compound-focused full-body session.",
                        List.of(
                                entry(exercisesDAO, "Barbell Squat", 3, 8, 120),
                                entry(exercisesDAO, "Barbell Bench Press - Medium Grip", 3, 8, 90),
                                entry(exercisesDAO, "Barbell Deadlift", 3, 6, 120),
                                entry(exercisesDAO, "Barbell Incline Bench Press - Medium Grip", 3, 10, 75),
                                entry(exercisesDAO, "Barbell Walking Lunge", 3, 10, 75),
                                entry(exercisesDAO, "Cable Chest Press", 3, 12, 60),
                                entry(exercisesDAO, "Barbell Curl", 3, 10, 60),
                                entry(exercisesDAO, "Barbell Seated Calf Raise", 3, 15, 45))));

        List<WorkoutRoutineDAO.RoutineData> saved = new ArrayList<>();
        for (RoutineTemplate template : templates) {
            int routineId = workoutDAO.saveRoutine(userId, template.name, template.notes, template.exercises);
            WorkoutRoutineDAO.RoutineData routine = workoutDAO.getRoutineById(routineId);
            if (routine != null) {
                saved.add(routine);
            }
        }
        return saved;
    }

    /**
     * Performs entry.
     * 
     * @param exercisesDAO The exercises dao.
     * @param name         The name.
     * @param sets         The sets.
     * @param reps         The reps.
     * @param restSeconds  The rest seconds.
     */
    private static WorkoutExerciseEntry entry(
            ExercisesDAO exercisesDAO, String name, int sets, int reps, int restSeconds) {
        int exerciseId = exercisesDAO.findExerciseIdByName(name);
        if (exerciseId == 0) {
            exerciseId = 1;
        }
        return new WorkoutExerciseEntry(0, exerciseId, name, sets, reps, restSeconds);
    }

    /**
     * Performs seed february data.
     * 
     * @param john        The john.
     * @param userId      The user id.
     * @param routines    The routines.
     * @param foodDAO     The food dao.
     * @param progressDAO The progress dao.
     * @param workoutDAO  The workout dao.
     */
    private static void seedFebruaryData(
            User john,
            int userId,
            List<WorkoutRoutineDAO.RoutineData> routines,
            FoodDAO foodDAO,
            UserProgressDAO progressDAO,
            WorkoutRoutineDAO workoutDAO) {
        final LocalDate periodEnd = LocalDate.of(2026, 5, 20);
        final LocalDate periodStart = periodEnd.minusDays(27);
        final LocalDate goalSwitchDate = periodStart.plusDays(19);
        int gymDayIndex = 0;
        for (LocalDate date = periodStart; !date.isAfter(periodEnd); date = date.plusDays(1)) {
            int dayNumber = (int) ChronoUnit.DAYS.between(periodStart, date) + 1;
            double weight = weightForDay(dayNumber);
            FitnessGoal goal = date.isBefore(goalSwitchDate) ? FitnessGoal.BUILD_MUSCLE : FitnessGoal.MAINTAIN_FITNESS;
            double targetCalories = targetCaloriesFor(john, weight, goal);

            progressDAO.addUserProgress(
                    new UserProgress(0, userId, goal, weight, targetCalories, date));

            for (Meal meal : mealsForDay(dayNumber)) {
                Food food = new Food();
                food.setUserId(userId);
                food.setName(meal.name);
                food.setCalories(meal.calories);
                food.setProtein(meal.protein);
                food.setCarbs(meal.carbs);
                food.setFats(meal.fats);
                foodDAO.addFood(food, date);
            }

            if (isGymDay(date) && !routines.isEmpty()) {
                WorkoutRoutineDAO.RoutineData routine = routines.get(gymDayIndex % routines.size());
                gymDayIndex++;
                List<WorkoutRoutineDAO.CompletedSetData> completedSets = buildCompletedSets(routine);
                LocalDateTime completedAt = date.atTime(7, 30).plusMinutes(gymDayIndex % 3 * 15L);
                workoutDAO.saveCompletedWorkout(userId, routine, completedSets, completedAt);
            }
        }
    }

    /**
     * Returns whether gym day.
     * 
     * @param date The date.
     * @return true if the condition holds; otherwise false.
     */
    private static boolean isGymDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Performs weight for day.
     * 
     * @param dayNumber The day number.
     * @return The computed value.
     */
    private static double weightForDay(int dayNumber) {
        if (dayNumber >= 20) {
            return END_WEIGHT_KG;
        }
        return START_WEIGHT_KG + (END_WEIGHT_KG - START_WEIGHT_KG) * (dayNumber - 1) / 19.0;
    }

    /**
     * Performs target calories for.
     * 
     * @param base   The base.
     * @param weight The weight.
     * @param goal   The goal.
     * @return The computed value.
     */
    private static double targetCaloriesFor(User base, double weight, FitnessGoal goal) {
        User snapshot = new User();
        snapshot.setDateOfBirth(base.getDateOfBirth());
        snapshot.setHeight(base.getHeight());
        snapshot.setWeight(weight);
        snapshot.setSex(base.getSex());
        snapshot.setActivityLevel(base.getActivityLevel());
        snapshot.setFitnessGoal(goal);
        return snapshot.getTargetCalories();
    }

    /**
     * Performs build completed sets.
     * 
     * @param routine The routine.
     * @return A list of matching records.
     */
    private static List<WorkoutRoutineDAO.CompletedSetData> buildCompletedSets(
            WorkoutRoutineDAO.RoutineData routine) {
        List<WorkoutRoutineDAO.CompletedSetData> completedSets = new ArrayList<>();
        for (WorkoutExerciseEntry exercise : routine.exercises) {
            for (int set = 1; set <= exercise.getSets(); set++) {
                int completedReps = exercise.getReps();
                if (set == exercise.getSets()) {
                    completedReps = Math.max(1, exercise.getReps() - 1);
                }
                completedSets.add(
                        new WorkoutRoutineDAO.CompletedSetData(
                                exercise.getExerciseId(),
                                exercise.getExerciseName(),
                                set,
                                exercise.getReps(),
                                completedReps,
                                exercise.getRestSeconds()));
            }
        }
        return completedSets;
    }

    /**
     * Performs meals for day.
     * 
     * @param dayNumber The day number.
     * @return A list of matching records.
     */
    private static List<Meal> mealsForDay(int dayNumber) {
        Meal[][] weeklyRotation = {
                {
                        meal("Greek yoghurt & berries", 220, 18, 28, 4),
                        meal("Scrambled eggs on toast", 380, 24, 32, 16),
                        meal("Chicken rice bowl", 620, 48, 72, 12),
                        meal("Protein shake", 280, 40, 18, 4),
                        meal("Salmon & sweet potato", 540, 42, 48, 18),
                        meal("Mixed nuts snack", 180, 6, 8, 14)
                },
                {
                        meal("Oatmeal with banana", 350, 12, 58, 8),
                        meal("Turkey wrap", 420, 32, 38, 14),
                        meal("Beef stir-fry & rice", 680, 46, 70, 22),
                        meal("Apple & peanut butter", 210, 6, 22, 12),
                        meal("Tuna salad", 390, 38, 18, 16),
                        meal("Cottage cheese snack", 160, 18, 8, 4),
                        meal("Dark chocolate square", 90, 2, 10, 5)
                },
                {
                        meal("Smoothie bowl", 310, 14, 52, 6),
                        meal("Ham & cheese sandwich", 450, 28, 42, 18),
                        meal("Pasta bolognese", 720, 36, 88, 20),
                        meal("Rice cakes & hummus", 190, 6, 28, 6),
                        meal("Grilled chicken salad", 410, 44, 16, 14),
                        meal("Trail mix", 200, 5, 18, 12)
                },
                {
                        meal("Pancakes & maple syrup", 480, 12, 72, 14),
                        meal("Egg white omelette", 260, 28, 6, 10),
                        meal("Burrito bowl", 640, 38, 78, 20),
                        meal("Protein bar", 220, 20, 24, 8),
                        meal("Pork chops & veggies", 520, 40, 24, 26),
                        meal("Yoghurt honey snack", 150, 10, 22, 3)
                }
        };
        Meal[] dayMeals = weeklyRotation[(dayNumber - 1) % weeklyRotation.length];
        int mealCount = 5 + (dayNumber % 3);
        List<Meal> selected = new ArrayList<>();
        for (int i = 0; i < mealCount; i++) {
            selected.add(dayMeals[i % dayMeals.length]);
        }
        return selected;
    }

    /**
     * Performs meal.
     * 
     * @param name     The name.
     * @param calories The calories.
     * @param protein  The protein.
     * @param carbs    The carbs.
     * @param fats     The fats.
     */
    private static Meal meal(String name, int calories, int protein, int carbs, int fats) {
        return new Meal(name, calories, protein, carbs, fats);
    }

    /**
     * Performs routine template.
     * 
     * @param name      The name.
     * @param notes     The notes.
     * @param exercises The exercises.
     */
    private record RoutineTemplate(String name, String notes, List<WorkoutExerciseEntry> exercises) {
    }

    /**
     * Performs meal.
     * 
     * @param name     The name.
     * @param calories The calories.
     * @param protein  The protein.
     * @param carbs    The carbs.
     * @param fats     The fats.
     */
    private record Meal(String name, int calories, int protein, int carbs, int fats) {
    }
}
