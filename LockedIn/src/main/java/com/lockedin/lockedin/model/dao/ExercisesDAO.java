package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.workout.Exercise;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ExercisesDAO {

    /** Shared database connection for all exercise operations. */
    private final Connection connection;

    /**
     * Initializes the DAO by connecting to the exercises database, creating the
     * table if needed,
     * and seeding default exercises.
     */
    public ExercisesDAO() {
        final String exercisesDbFile = "exercises.db";
        this.connection = SqliteConnection.getInstance(exercisesDbFile);
        createExercisesTable();
        seedExercises();
    }

    /** For unit tests with an in-memory database (table only, no seed). */
    public ExercisesDAO(Connection connection) {
        this.connection = connection;
        createExercisesTable();
    }

    /**
     * Creates the exercises table if it does not already exist. Stores exercise
     * metadata such as
     * name, instructions, category, primary muscle, and image URL.
     */
    private void createExercisesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS exercises ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "instruction TEXT, "
                + "category TEXT, "
                + "primary_muscle TEXT, "
                + "exercise_image_url TEXT"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create exercises table", e);
        }
    }

    public void seedExercises() {
        String countSql = "SELECT COUNT(*) FROM exercises";
        try (Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                addExercise(
                        new Exercise(
                                0,
                                "Alternate Leg Diagonal Bound",
                                "1) Assume a comfortable stance with one foot slightly in front of"
                                        + " the other.\\n"
                                        + "2) Begin by pushing off with the front leg, driving the"
                                        + " opposite knee forward and as high as possible before"
                                        + " landing. Attempt to cover as much distance to each side"
                                        + " with each bound.\\n"
                                        + "3) It may help to use a line on the ground to guage distance"
                                        + " from side to side.\\n"
                                        + "4) Repeat the sequence with the other leg.",
                                "plyometrics",
                                "Quadriceps",
                                "Alternate_Leg_Diagonal_Bound"));
                addExercise(
                        new Exercise(
                                0,
                                "Backward Drag",
                                "1) Load a sled with the desired weight, attaching a rope or straps"
                                        + " to the sled that you can hold onto.\\n"
                                        + "2) Begin the exercise by moving backwards for a given"
                                        + " distance. Leaning back, extend through the legs for short"
                                        + " steps to move as quickly as possible.",
                                "strongman",
                                "Quadriceps",
                                "Backward_Drag"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Side Split Squat",
                                "1) Stand up straight while holding a barbell placed on the back of"
                                        + " your shoulders (slightly below the neck). Your feet should"
                                        + " be placed wide apart with the foot of the lead leg angled"
                                        + " out to the side. This will be your starting position.\\n"
                                        + "2) Lower your body towards the side of your angled foot by"
                                        + " bending the knee and hip of your lead leg and while keeping"
                                        + " the opposite leg only slightly bent. Breathe in as you"
                                        + " lower your body.\\n"
                                        + "3) Return to the starting position by extending the hip and"
                                        + " knee of the lead leg. Breathe out as you perform this"
                                        + " movement.\\n"
                                        + "4) After performing the recommended amount of reps, repeat"
                                        + " the movement with the opposite leg.",
                                "strength",
                                "Quadriceps",
                                "Barbell_Side_Split_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Squat",
                                "1) This exercise is best performed inside a squat rack for safety"
                                        + " purposes. To begin, first set the bar on a rack to just"
                                        + " below shoulder level. Once the correct height is chosen and"
                                        + " the bar is loaded, step under the bar and place the back of"
                                        + " your shoulders (slightly below the neck) across it.\\n"
                                        + "2) Hold on to the bar using both arms at each side and lift"
                                        + " it off the rack by first pushing with your legs and at the"
                                        + " same time straightening your torso.\\n"
                                        + "3) Step away from the rack and position your legs using a"
                                        + " shoulder width medium stance with the toes slightly pointed"
                                        + " out. Keep your head up at all times and also maintain a"
                                        + " straight back. This will be your starting position. (Note:"
                                        + " For the purposes of this discussion we will use the medium"
                                        + " stance described above which targets overall development;"
                                        + " however you can choose any of the three stances discussed"
                                        + " in the foot stances section).\\n"
                                        + "4) Begin to slowly lower the bar by bending the knees and"
                                        + " hips as you maintain a straight posture with the head up."
                                        + " Continue down until the angle between the upper leg and the"
                                        + " calves becomes slightly less than 90-degrees. Inhale as you"
                                        + " perform this portion of the movement. Tip: If you performed"
                                        + " the exercise correctly, the front of the knees should make"
                                        + " an imaginary straight line with the toes that is"
                                        + " perpendicular to the front. If your knees are past that"
                                        + " imaginary line (if they are past your toes) then you are"
                                        + " placing undue stress on the knee and the exercise has been"
                                        + " performed incorrectly.\\n"
                                        + "5) Begin to raise the bar as you exhale by pushing the floor"
                                        + " with the heel of your foot as you straighten the legs again"
                                        + " and go back to the starting position.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Quadriceps",
                                "Barbell_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Walking Lunge",
                                "1) Begin standing with your feet shoulder width apart and a"
                                        + " barbell across your upper back.\\n"
                                        + "2) Step forward with one leg, flexing the knees to drop your"
                                        + " hips. Descend until your rear knee nearly touches the"
                                        + " ground. Your posture should remain upright, and your front"
                                        + " knee should stay above the front foot.\\n"
                                        + "3) Drive through the heel of your lead foot and extend both"
                                        + " knees to raise yourself back up.\\n"
                                        + "4) Step forward with your rear foot, repeating the lunge on"
                                        + " the opposite leg.",
                                "strength",
                                "Quadriceps",
                                "Barbell_Walking_Lunge"));
                addExercise(
                        new Exercise(
                                0,
                                "Bear Crawl Sled Drags",
                                "1) Wearing either a harness or a loose weight belt, attach the"
                                        + " chain to the back so that you will be facing away from the"
                                        + " sled. Bend down so that your hands are on the ground. Your"
                                        + " back should be flat and knees bent. This is your starting"
                                        + " position.\\n"
                                        + "2) Begin by driving with legs, alternating left and right."
                                        + " Use your hands to maintain balance and to help pull. Try to"
                                        + " keep your back flat as you move over a given distance.",
                                "strongman",
                                "Quadriceps",
                                "Bear_Crawl_Sled_Drags"));
                addExercise(
                        new Exercise(
                                0,
                                "Bench Sprint",
                                "1) Stand on the ground with one foot resting on a bench or box"
                                        + " with your heel close to the edge.\\n"
                                        + "2) Push off with your foot on top of the bench, extending"
                                        + " through the hip and knee.\\n"
                                        + "3) Land with the opposite foot on top of the box, returning"
                                        + " your other foot back to the start position.\\n"
                                        + "4) Continue alternating from one foot to another to complete"
                                        + " the set.",
                                "plyometrics",
                                "Quadriceps",
                                "Bench_Sprint"));
                addExercise(
                        new Exercise(
                                0,
                                "Bodyweight Squat",
                                "1) Stand with your feet shoulder width apart. You can place your"
                                        + " hands behind your head. This will be your starting"
                                        + " position.\\n"
                                        + "2) Begin the movement by flexing your knees and hips,"
                                        + " sitting back with your hips.\\n"
                                        + "3) Continue down to full depth if you are able,and quickly"
                                        + " reverse the motion until you return to the starting"
                                        + " position. As you squat, keep your head and chest up and"
                                        + " push your knees out.",
                                "strength",
                                "Quadriceps",
                                "Bodyweight_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Alternating Cable Shoulder Press",
                                "1) Move the cables to the bottom of the tower and select an"
                                        + " appropriate weight.\\n"
                                        + "2) Grasp the cables and hold them at shoulder height, palms"
                                        + " facing forward. This will be your starting position.\\n"
                                        + "3) Keeping your head and chest up, extend through the elbow"
                                        + " to press one side directly over head.\\n"
                                        + "4) After pausing at the top, return to the starting position"
                                        + " and repeat on the opposite side.",
                                "strength",
                                "Shoulders",
                                "Alternating_Cable_Shoulder_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Anti-Gravity Press",
                                "1) Place a bar on the ground behind the head of an incline"
                                        + " bench.\\n"
                                        + "2) Lay on the bench face down. With a pronated grip, pick"
                                        + " the barbell up from the floor. Flex the elbows, performing"
                                        + " a reverse curl to bring the bar near your chest. This will"
                                        + " be your starting position.\\n"
                                        + "3) To begin, press the barbell out in front of your head by"
                                        + " extending your elbows. Keep your arms parallel to the"
                                        + " ground throughout the movement.\\n"
                                        + "4) Return to the starting position and repeat to complete"
                                        + " the set.",
                                "strength",
                                "Shoulders",
                                "Anti-Gravity_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Back Flyes - With Bands",
                                "1) Run a band around a stationary post like that of a squat"
                                        + " rack.\\n"
                                        + "2) Grab the band by the handles and stand back so that the"
                                        + " tension in the band rises.\\n"
                                        + "3) Extend and lift the arms straight in front of you. Tip:"
                                        + " Your arms should be straight and parallel to the floor"
                                        + " while perpendicular to your torso. Your feet should be"
                                        + " firmly planted on the floor spread at shoulder width. This"
                                        + " will be your starting position.\\n"
                                        + "4) As you exhale, move your arms to the sides and back. Keep"
                                        + " your arms extended and parallel to the floor. Continue the"
                                        + " movement until the arms are extended to your sides.\\n"
                                        + "5) After a pause, go back to the original position as you"
                                        + " inhale.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Back_Flyes_-_With_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Backward Medicine Ball Throw",
                                "1) This exercise is best done with a partner. If you lack a"
                                        + " partner, the ball can be thrown and retrieved or thrown"
                                        + " against a wall.\\n"
                                        + "2) Begin standing a few meters in front of your partner,"
                                        + " both facing the same direction. Begin holding the ball"
                                        + " between your legs.\\n"
                                        + "3) Squat down and then forcefully reverse direction, coming"
                                        + " to full extension and you toss the ball over your head to"
                                        + " your partner.\\n"
                                        + "4) Your partner can then roll the ball back to you. Repeat"
                                        + " for the desired number of repetitions.",
                                "plyometrics",
                                "Shoulders",
                                "Backward_Medicine_Ball_Throw"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Incline Shoulder Raise",
                                "1) Lie back on an Incline Bench. Using a medium width grip (a grip"
                                        + " that is slightly wider than shoulder width), lift the bar"
                                        + " from the rack and hold it straight over you with your arms"
                                        + " straight. This will be your starting position.\\n"
                                        + "2) While keeping the arms straight, lift the bar by"
                                        + " protracting your shoulder blades, raising the shoulders"
                                        + " from the bench as you breathe out.\\n"
                                        + "3) Bring back the bar to the starting position as you"
                                        + " breathe in.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Barbell_Incline_Shoulder_Raise"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Rear Delt Row",
                                "1) Stand up straight while holding a barbell using a wide (higher"
                                        + " than shoulder width) and overhand (palms facing your body)"
                                        + " grip.\\n"
                                        + "2) Bend knees slightly and bend over as you keep the natural"
                                        + " arch of your back. Let the arms hang in front of you as"
                                        + " they hold the bar. Once your torso is parallel to the"
                                        + " floor, flare the elbows out and away from your body. Tip:"
                                        + " Your torso and your arms should resemble the letter \"T\"."
                                        + " Now you are ready to begin the exercise.\\n"
                                        + "3) While keeping the upper arms perpendicular to the torso,"
                                        + " pull the barbell up towards your upper chest as you squeeze"
                                        + " the rear delts and you breathe out. Tip: When performed"
                                        + " correctly, this exercise should resemble a bench press in"
                                        + " reverse. Also, refrain from using your biceps to do the"
                                        + " work. Focus on targeting the rear delts; the arms should"
                                        + " only act as hooks.\\n"
                                        + "4) Slowly go back to the initial position as you breathe"
                                        + " in.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Barbell_Rear_Delt_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Battling Ropes",
                                "1) For this exercise you will need a heavy rope anchored at its"
                                        + " center 15-20 feet away. Standing in front of the rope, take"
                                        + " an end in each hand with your arms extended at your side."
                                        + " This will be your starting position.\\n"
                                        + "2) Initiate the movement by rapidly raising one arm to"
                                        + " shoulder level as quickly as you can.\\n"
                                        + "3) As you let that arm drop to the starting position, raise"
                                        + " the opposite side.\\n"
                                        + "4) Continue alternating your left and right arms, whipping"
                                        + " the ropes up and down as fast as you can.",
                                "strength",
                                "Shoulders",
                                "Battling_Ropes"));
                addExercise(
                        new Exercise(
                                0,
                                "Bradford/Rocky Presses",
                                "1) Sit on a Military Press Bench with a bar at shoulder level with"
                                        + " a pronated grip (palms facing forward). Tip: Your grip"
                                        + " should be wider than shoulder width and it should create a"
                                        + " 90-degree angle between the forearm and the upper arm as"
                                        + " the barbell goes down. This is your starting position.\\n"
                                        + "2) Once you pick up the barbell with the correct grip, lift"
                                        + " the bar up over your head by locking your arms.\\n"
                                        + "3) Now lower the bar down to the back of the head slowly as"
                                        + " you inhale.\\n"
                                        + "4) Lift the bar back up to the starting position as you"
                                        + " exhale.\\n"
                                        + "5) Lower the bar down to the starting position slowly as you"
                                        + " inhale. This is one repetition.\\n"
                                        + "6) Alternate in this manner until you complete the"
                                        + " recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Bradford_Rocky_Presses"));
                addExercise(
                        new Exercise(
                                0,
                                "3/4 Sit-Up",
                                "1) Lie down on the floor and secure your feet. Your legs should be"
                                        + " bent at the knees.\\n"
                                        + "2) Place your hands behind or to the side of your head. You"
                                        + " will begin with your back on the ground. This will be your"
                                        + " starting position.\\n"
                                        + "3) Flex your hips and spine to raise your torso toward your"
                                        + " knees.\\n"
                                        + "4) At the top of the contraction your torso should be"
                                        + " perpendicular to the ground. Reverse the motion, going only"
                                        + " \u00be of the way down.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "3_4_Sit-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Air Bike",
                                "1) Lie flat on the floor with your lower back pressed to the"
                                        + " ground. For this exercise, you will need to put your hands"
                                        + " beside your head. Be careful however to not strain with the"
                                        + " neck as you perform it. Now lift your shoulders into the"
                                        + " crunch position.\\n"
                                        + "2) Bring knees up to where they are perpendicular to the"
                                        + " floor, with your lower legs parallel to the floor. This"
                                        + " will be your starting position.\\n"
                                        + "3) Now simultaneously, slowly go through a cycle pedal"
                                        + " motion kicking forward with the right leg and bringing in"
                                        + " the knee of the left leg. Bring your right elbow close to"
                                        + " your left knee by crunching to the side, as you breathe"
                                        + " out.\\n"
                                        + "4) Go back to the initial position as you breathe in.\\n"
                                        + "5) Crunch to the opposite side as you cycle your legs and"
                                        + " bring closer your left elbow to your right knee and"
                                        + " exhale.\\n"
                                        + "6) Continue alternating in this manner until all of the"
                                        + " recommended repetitions for each side have been completed.",
                                "strength",
                                "Abdominals",
                                "Air_Bike"));
                addExercise(
                        new Exercise(
                                0,
                                "Bent-Knee Hip Raise",
                                "1) Lay flat on the floor with your arms next to your sides.\\n"
                                        + "2) Now bend your knees at around a 75 degree angle and lift"
                                        + " your feet off the floor by around 2 inches.\\n"
                                        + "3) Using your lower abs, bring your knees in towards you as"
                                        + " you maintain the 75 degree angle bend in your legs."
                                        + " Continue this movement until you raise your hips off of the"
                                        + " floor by rolling your pelvis backward. Breathe out as you"
                                        + " perform this portion of the movement. Tip: At the end of"
                                        + " the movement your knees will be over your chest.\\n"
                                        + "4) Squeeze your abs at the top of the movement for a second"
                                        + " and then return to the starting position slowly as you"
                                        + " breathe in. Tip: Maintain a controlled motion at all"
                                        + " times.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Bent-Knee_Hip_Raise"));
                addExercise(
                        new Exercise(
                                0,
                                "Bottoms Up",
                                "1) Begin by lying on your back on the ground. Your legs should be"
                                        + " straight and your arms at your side. This will be your"
                                        + " starting position.\\n"
                                        + "2) To perform the movement, tuck the knees toward your chest"
                                        + " by flexing the hips and knees. Following this, extend your"
                                        + " legs directly above you so that they are perpendicular to"
                                        + " the ground. Rotate and elevate your pelvis to raise your"
                                        + " glutes from the floor.\\n"
                                        + "3) After a brief pause, return to the starting position.",
                                "strength",
                                "Abdominals",
                                "Bottoms_Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Butt-Ups",
                                "1) Begin a pushup position but with your elbows on the ground and"
                                        + " resting on your forearms. Your arms should be bent at a 90"
                                        + " degree angle.\\n"
                                        + "2) Arch your back slightly out rather than keeping your back"
                                        + " completely straight.\\n"
                                        + "3) Raise your glutes toward the ceiling, squeezing your abs"
                                        + " tightly to close the distance between your ribcage and"
                                        + " hips. The end result will be that you'll end up in a high"
                                        + " bridge position. Exhale as you perform this portion of the"
                                        + " movement.\\n"
                                        + "4) Lower back down slowly to your starting position as you"
                                        + " breathe in. Tip: Don't let your back sag downwards.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Butt-Ups"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Judo Flip",
                                "1) Connect a rope attachment to a tower, and move the cable to the"
                                        + " lowest pulley position. Stand with your side to the cable"
                                        + " with a wide stance, and grab the rope with both hands.\\n"
                                        + "2) Twist your body away from the pulley as you bring the"
                                        + " rope over your shoulder like you're performing a judo"
                                        + " flip.\\n"
                                        + "3) Shift your weight between your feet as you twist and"
                                        + " crunch forward, pulling the cable downward.\\n"
                                        + "4) Return to the starting position and repeat until"
                                        + " failure.\\n"
                                        + "5) Then, reposition and repeat the same series of movements"
                                        + " on the opposite side.",
                                "strength",
                                "Abdominals",
                                "Cable_Judo_Flip"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Russian Twists",
                                "1) Connect a standard handle attachment, and position the cable to"
                                        + " a middle pulley position.\\n"
                                        + "2) Lie on a stability ball perpendicular to the cable and"
                                        + " grab the handle with one hand. You should be approximately"
                                        + " arm's length away from the pulley, with the tension of the"
                                        + " weight on the cable.\\n"
                                        + "3) Grab the handle with both hands and fully extend your"
                                        + " arms above your chest. You hands should be directly in-line"
                                        + " with the pulley. If not, adjust the pulley up or down until"
                                        + " they are.\\n"
                                        + "4) Keep your hips elevated and abs engaged. Rotate your"
                                        + " torso away from the pulley for a full-quarter rotation."
                                        + " Your body should be flat from head to knees.\\n"
                                        + "5) Pause for a moment and in a slow and controlled manner"
                                        + " reset to the starting position. You should still have side"
                                        + " tension on the cable in the resting position.\\n"
                                        + "6) Repeat the same movement to failure.\\n"
                                        + "7) Then, reposition and repeat the same series of movements"
                                        + " on the opposite side.",
                                "strength",
                                "Abdominals",
                                "Cable_Russian_Twists"));
                addExercise(
                        new Exercise(
                                0,
                                "Cocoons",
                                "1) Begin by lying on your back on the ground. Your legs should be"
                                        + " straight and your arms extended behind your head. This will"
                                        + " be your starting position.\\n"
                                        + "2) To perform the movement, tuck the knees toward your"
                                        + " chest, rotating your pelvis to lift your glutes from the"
                                        + " floor. As you do so, flex the spine, bringing your arms"
                                        + " back over your head to perform a simultaneous crunch"
                                        + " motion.\\n"
                                        + "3) After a brief pause, return to the starting position.",
                                "strength",
                                "Abdominals",
                                "Cocoons"));
                addExercise(
                        new Exercise(
                                0,
                                "Alternating Floor Press",
                                "1) Lie on the floor with two kettlebells next to your"
                                        + " shoulders.\\n"
                                        + "2) Position one in place on your chest and then the other,"
                                        + " gripping the kettlebells on the handle with the palms"
                                        + " facing forward.\\n"
                                        + "3) Extend both arms, so that the kettlebells are being held"
                                        + " above your chest. Lower one kettlebell, bringing it to your"
                                        + " chest and turn the wrist in the direction of the locked out"
                                        + " kettlebell.\\n"
                                        + "4) Raise the kettlebell and repeat on the opposite side.",
                                "strength",
                                "Chest",
                                "Alternating_Floor_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Bench Press - Medium Grip",
                                "1) Lie back on a flat bench. Using a medium width grip (a grip"
                                        + " that creates a 90-degree angle in the middle of the"
                                        + " movement between the forearms and the upper arms), lift the"
                                        + " bar from the rack and hold it straight over you with your"
                                        + " arms locked. This will be your starting position.\\n"
                                        + "2) From the starting position, breathe in and begin coming"
                                        + " down slowly until the bar touches your middle chest.\\n"
                                        + "3) After a brief pause, push the bar back to the starting"
                                        + " position as you breathe out. Focus on pushing the bar using"
                                        + " your chest muscles. Lock your arms and squeeze your chest"
                                        + " in the contracted position at the top of the motion, hold"
                                        + " for a second and then start coming down slowly again. Tip:"
                                        + " Ideally, lowering the weight should take about twice as"
                                        + " long as raising it.\\n"
                                        + "4) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "5) When you are done, place the bar back in the rack.",
                                "strength",
                                "Chest",
                                "Barbell_Bench_Press_-_Medium_Grip"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Incline Bench Press - Medium Grip",
                                "1) Lie back on an incline bench. Using a medium-width grip (a grip"
                                        + " that creates a 90-degree angle in the middle of the"
                                        + " movement between the forearms and the upper arms), lift the"
                                        + " bar from the rack and hold it straight over you with your"
                                        + " arms locked. This will be your starting position.\\n"
                                        + "2) As you breathe in, come down slowly until you feel the"
                                        + " bar on you upper chest.\\n"
                                        + "3) After a second pause, bring the bar back to the starting"
                                        + " position as you breathe out and push the bar using your"
                                        + " chest muscles. Lock your arms in the contracted position,"
                                        + " squeeze your chest, hold for a second and then start coming"
                                        + " down slowly again. Tip: it should take at least twice as"
                                        + " long to go down than to come up.\\n"
                                        + "4) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "5) When you are done, place the bar back in the rack.",
                                "strength",
                                "Chest",
                                "Barbell_Incline_Bench_Press_-_Medium_Grip"));
                addExercise(
                        new Exercise(
                                0,
                                "Bench Press - With Bands",
                                "1) Using a flat bench secure a band under the leg of the bench"
                                        + " that is nearest to your head.\\n"
                                        + "2) Once the band is secure, grab it by both handles and lie"
                                        + " down on the bench.\\n"
                                        + "3) Extend your arms so that you are holding the band handles"
                                        + " in front of you at shoulder width.\\n"
                                        + "4) Once at shoulder width, rotate your wrists forward so"
                                        + " that the palms of your hands are facing away from you. This"
                                        + " will be your starting position.\\n"
                                        + "5) Bring down the handles slowly until your elbow forms a 90"
                                        + " degree angle. Keep full control at all times.\\n"
                                        + "6) As you breathe out, bring the handles up using your"
                                        + " pectoral muscles. Lock your arms in the contracted"
                                        + " position, squeeze your chest, hold for a second and then"
                                        + " start coming down slowly. Tip: It should take at least"
                                        + " twice as long to go down than to come up.\\n"
                                        + "7) Repeat the movement for the prescribed amount of"
                                        + " repetitions of your training program.",
                                "strength",
                                "Chest",
                                "Bench_Press_-_With_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Chest Press",
                                "1) Adjust the weight to an appropriate amount and be seated,"
                                        + " grasping the handles. Your upper arms should be about 45"
                                        + " degrees to the body, with your head and chest up. The"
                                        + " elbows should be bent to about 90 degrees. This will be"
                                        + " your starting position.\\n"
                                        + "2) Begin by extending through the elbow, pressing the"
                                        + " handles together straight in front of you. Keep your"
                                        + " shoulder blades retracted as you execute the movement.\\n"
                                        + "3) After pausing at full extension, return to th starting"
                                        + " position, keeping tension on the cables.\\n"
                                        + "4) You can also execute this movement with your back off the"
                                        + " pad, at an incline or decline, or alternate hands.",
                                "strength",
                                "Chest",
                                "Cable_Chest_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Chest Push (multiple response)",
                                "1) Begin in a kneeling position facing a wall or utilize a"
                                        + " partner. Hold the ball with both hands tight into the"
                                        + " chest.\\n"
                                        + "2) Execute the pass by exploding forward and outward with"
                                        + " the hips while pushing the ball as hard as possible.\\n"
                                        + "3) Follow through by falling forward, catching yourself with"
                                        + " your hands.\\n"
                                        + "4) Immediately return to an upright position. Repeat for the"
                                        + " desired number of repetitions.",
                                "plyometrics",
                                "Chest",
                                "Chest_Push_multiple_response"));
                addExercise(
                        new Exercise(
                                0,
                                "Chest Push (single response)",
                                "1) Begin in a kneeling position holding the medicine ball with"
                                        + " both hands tightly into the chest.\\n"
                                        + "2) Execute the pass by exploding forward and outward with"
                                        + " the hips while pushing the ball as far as possible.\\n"
                                        + "3) Follow through by falling forward, catching yourself with"
                                        + " your hands.",
                                "plyometrics",
                                "Chest",
                                "Chest_Push_single_response"));
                addExercise(
                        new Exercise(
                                0,
                                "Chest Push from 3 point stance",
                                "1) Begin in a three point stance, squatted down with your back"
                                        + " flat and one hand on the ground. Place the medicine ball"
                                        + " directly in front of you.\\n"
                                        + "2) To begin, take your first step as you pull the ball to"
                                        + " your chest, positioning both hands to prepare for the"
                                        + " throw.\\n"
                                        + "3) As you execute the second step, explosively release the"
                                        + " ball forward as hard as possible.",
                                "plyometrics",
                                "Chest",
                                "Chest_Push_from_3_point_stance"));
                addExercise(
                        new Exercise(
                                0,
                                "Band Good Morning",
                                "1) Using a 41 inch band, stand on one end, spreading your feet a"
                                        + " small amount. Bend at the hips to loop the end of the band"
                                        + " behind your neck. This will be your starting position.\\n"
                                        + "2) Keeping your legs straight, extend through the hips to"
                                        + " come to a near vertical position.\\n"
                                        + "3) Ensure that you do not round your back as you go down"
                                        + " back to the starting position.",
                                "powerlifting",
                                "Hamstrings",
                                "Band_Good_Morning"));
                addExercise(
                        new Exercise(
                                0,
                                "Band Good Morning (Pull Through)",
                                "1) Loop the band around a post. Standing a little ways away, loop"
                                        + " the opposite end around the neck. Your hands can help hold"
                                        + " the band in position.\\n"
                                        + "2) Begin by bending at the hips, getting your butt back as"
                                        + " far as possible. Keep your back flat and bend forward to"
                                        + " about 90 degrees. Your knees should be only slightly"
                                        + " bent.\\n"
                                        + "3) Return to the starting position be driving through with"
                                        + " the hips to come back to a standing position.",
                                "powerlifting",
                                "Hamstrings",
                                "Band_Good_Morning_Pull_Through"));
                addExercise(
                        new Exercise(
                                0,
                                "Box Jump (Multiple Response)",
                                "1) Assume a relaxed stance facing the box or platform"
                                        + " approximately an arm's length away. Arms should be down at"
                                        + " the sides and legs slightly bent.\\n"
                                        + "2) Using the arms to aid in the initial burst, jump upward"
                                        + " and forward, landing with feet simultaneously on top of the"
                                        + " box or platform.\\n"
                                        + "3) Immediately drop or jump back down to the original"
                                        + " starting place; then repeat the sequence.",
                                "plyometrics",
                                "Hamstrings",
                                "Box_Jump_Multiple_Response"));
                addExercise(
                        new Exercise(
                                0,
                                "Box Skip",
                                "1) You will need several boxes lined up about 8 feet apart.\\n"
                                        + "2) Begin facing the first box with one leg slightly behind"
                                        + " the other.\\n"
                                        + "3) Drive off the back leg, attempting to gain as much height"
                                        + " with the hips as possible.\\n"
                                        + "4) Immediately upon landing on the box, drive the other leg"
                                        + " forward and upward to gain height and distance, leaping"
                                        + " from the box. Land between the first two boxes with the"
                                        + " same leg that landed on the first box.\\n"
                                        + "5) Then, step to the next box and repeat.",
                                "plyometrics",
                                "Hamstrings",
                                "Box_Skip"));
                addExercise(
                        new Exercise(
                                0,
                                "Clean Deadlift",
                                "1) Begin standing with a barbell close to your shins. Your feet"
                                        + " should be directly under your hips with your feet turned"
                                        + " out slightly. Grip the bar with a double overhand grip or"
                                        + " hook grip, about shoulder width apart. Squat down to the"
                                        + " bar. Your spine should be in full extension, with a back"
                                        + " angle that places your shoulders in front of the bar and"
                                        + " your back as vertical as possible.\\n"
                                        + "2) Begin by driving through the floor through the front of"
                                        + " your heels. As the bar travels upward, maintain a constant"
                                        + " back angle. Flare your knees out to the side to help keep"
                                        + " them out of the bar's path.\\n"
                                        + "3) After the bar crosses the knees, complete the lift by"
                                        + " driving the hips into the bar until your hips and knees are"
                                        + " extended.",
                                "olympic weightlifting",
                                "Hamstrings",
                                "Clean_Deadlift"));
                addExercise(
                        new Exercise(
                                0,
                                "Front Box Jump",
                                "1) Begin with a box of an appropriate height 1-2 feet in front of"
                                        + " you. Stand with your feet should width apart. This will be"
                                        + " your starting position.\\n"
                                        + "2) Perform a short squat in preparation for jumping,"
                                        + " swinging your arms behind you.\\n"
                                        + "3) Rebound out of this position, extending through the hips,"
                                        + " knees, and ankles to jump as high as possible. Swing your"
                                        + " arms forward and up.\\n"
                                        + "4) Land on the box with the knees bent, absorbing the impact"
                                        + " through the legs. You can jump from the box back to the"
                                        + " ground, or preferably step down one leg at a time.",
                                "plyometrics",
                                "Hamstrings",
                                "Front_Box_Jump"));
                addExercise(
                        new Exercise(
                                0,
                                "Hurdle Hops",
                                "1) Set up a row of hurdles or other small barriers, placing them a"
                                        + " few feet apart.\\n"
                                        + "2) Stand in front of the first hurdle with your feet"
                                        + " shoulder width apart. This will be your starting"
                                        + " position.\\n"
                                        + "3) Begin by jumping with both feet over the first hurdle,"
                                        + " swinging both arms as you jump.\\n"
                                        + "4) Absorb the impact of landing by bending the knees,"
                                        + " rebounding out of the first leap by jumping over the next"
                                        + " hurdle. Continue until you have jumped over all of the"
                                        + " hurdles.",
                                "plyometrics",
                                "Hamstrings",
                                "Hurdle_Hops"));
                addExercise(
                        new Exercise(
                                0,
                                "Inchworm",
                                "1) Stand with your feet close together. Keeping your legs"
                                        + " straight, stretch down and put your hands on the floor"
                                        + " directly in front of you. This will be your starting"
                                        + " position.\\n"
                                        + "2) Begin by walking your hands forward slowly, alternating"
                                        + " your left and your right. As you do so, bend only at the"
                                        + " hip, keeping your legs straight.\\n"
                                        + "3) Keep going until your body is parallel to the ground in a"
                                        + " pushup position.\\n"
                                        + "4) Now, keep your hands in place and slowly take short steps"
                                        + " with your feet, moving only a few inches at a time.\\n"
                                        + "5) Continue walking until your feet are by hour hands,"
                                        + " keeping your legs straight as you do so.",
                                "stretching",
                                "Hamstrings",
                                "Inchworm"));
                addExercise(
                        new Exercise(
                                0,
                                "Bench Dips",
                                "1) For this exercise you will need to place a bench behind your"
                                        + " back. With the bench perpendicular to your body, and while"
                                        + " looking away from it, hold on to the bench on its edge with"
                                        + " the hands fully extended, separated at shoulder width. The"
                                        + " legs will be extended forward, bent at the waist and"
                                        + " perpendicular to your torso. This will be your starting"
                                        + " position.\\n"
                                        + "2) Slowly lower your body as you inhale by bending at the"
                                        + " elbows until you lower yourself far enough to where there"
                                        + " is an angle slightly smaller than 90 degrees between the"
                                        + " upper arm and the forearm. Tip: Keep the elbows as close as"
                                        + " possible throughout the movement. Forearms should always be"
                                        + " pointing down.\\n"
                                        + "3) Using your triceps to bring your torso up again, lift"
                                        + " yourself back to the starting position.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Triceps",
                                "Bench_Dips"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip Barbell Bench Press",
                                "1) Lie back on a flat bench. Using a close grip (around shoulder"
                                        + " width), lift the bar from the rack and hold it straight"
                                        + " over you with your arms locked. This will be your starting"
                                        + " position.\\n"
                                        + "2) As you breathe in, come down slowly until you feel the"
                                        + " bar on your middle chest. Tip: Make sure that - as opposed"
                                        + " to a regular bench press - you keep the elbows close to the"
                                        + " torso at all times in order to maximize triceps"
                                        + " involvement.\\n"
                                        + "3) After a second pause, bring the bar back to the starting"
                                        + " position as you breathe out and push the bar using your"
                                        + " triceps muscles. Lock your arms in the contracted position,"
                                        + " hold for a second and then start coming down slowly again."
                                        + " Tip: It should take at least twice as long to go down than"
                                        + " to come up.\\n"
                                        + "4) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "5) When you are done, place the bar back in the rack.",
                                "strength",
                                "Triceps",
                                "Close-Grip_Barbell_Bench_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip Dumbbell Press",
                                "1) Place a dumbbell standing up on a flat bench.\\n"
                                        + "2) Ensuring that the dumbbell stays securely placed at the"
                                        + " top of the bench, lie perpendicular to the bench with only"
                                        + " your shoulders lying on the surface. Hips should be below"
                                        + " the bench and your legs bent with your feet firmly on the"
                                        + " floor.\\n"
                                        + "3) Grasp the dumbbell with both hands and hold it straight"
                                        + " over your chest at arm's length. Both palms should be"
                                        + " pressing against the underside of the sides of the"
                                        + " dumbbell. This will be your starting position.\\n"
                                        + "4) Initiate the movement by lowering the dumbbell to your"
                                        + " chest.\\n"
                                        + "5) Return to the starting position by extending the elbows.",
                                "strength",
                                "Triceps",
                                "Close-Grip_Dumbbell_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip EZ-Bar Press",
                                "1) Lie on a flat bench with an EZ bar loaded to an appropriate"
                                        + " weight.\\n"
                                        + "2) Using a narrow grip lift the bar and hold it straight"
                                        + " over your torso with your elbows in. The arms should be"
                                        + " perpendicular to the floor. This will be your starting"
                                        + " position.\\n"
                                        + "3) Now lower the bar down to your lower chest as you breathe"
                                        + " in. Keep the elbows in as you perform this movement.\\n"
                                        + "4) Using the triceps to push the bar back up, press it back"
                                        + " to the starting position by extending the elbows as you"
                                        + " exhale.\\n"
                                        + "5) Repeat.",
                                "strength",
                                "Triceps",
                                "Close-Grip_EZ-Bar_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Dip Machine",
                                "1) Sit securely in a dip machine, select the weight and firmly"
                                        + " grasp the handles.\\n"
                                        + "2) Now keep your elbows in at your sides in order to place"
                                        + " emphasis on the triceps. The elbows should be bent at a 90"
                                        + " degree angle.\\n"
                                        + "3) As you contract the triceps, extend your arms downwards"
                                        + " as you exhale. Tip: At the bottom of the movement, focus on"
                                        + " keeping a little bend in your arms to keep tension on the"
                                        + " triceps muscle.\\n"
                                        + "4) Now slowly let your arms come back up to the starting"
                                        + " position as you inhale.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Triceps",
                                "Dip_Machine"));
                addExercise(
                        new Exercise(
                                0,
                                "Dips - Triceps Version",
                                "1) To get into the starting position, hold your body at arm's"
                                        + " length with your arms nearly locked above the bars.\\n"
                                        + "2) Now, inhale and slowly lower yourself downward. Your"
                                        + " torso should remain upright and your elbows should stay"
                                        + " close to your body. This helps to better focus on tricep"
                                        + " involvement. Lower yourself until there is a 90 degree"
                                        + " angle formed between the upper arm and forearm.\\n"
                                        + "3) Then, exhale and push your torso back up using your"
                                        + " triceps to bring your body back to the starting"
                                        + " position.\\n"
                                        + "4) Repeat the movement for the prescribed amount of"
                                        + " repetitions.",
                                "strength",
                                "Triceps",
                                "Dips_-_Triceps_Version"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up Close-Grip",
                                "1) Stand facing a Smith machine bar or sturdy elevated platform at"
                                        + " an appropriate height.\\n"
                                        + "2) Place your hands next to one another on the bar.\\n"
                                        + "3) Position your feet back from the bar with arms and body"
                                        + " straight. This will be your starting position.\\n"
                                        + "4) Keeping your body straight, lower your chest to the bar"
                                        + " by bending the arms.\\n"
                                        + "5) Return to the starting position by extending the elbows,"
                                        + " pressing yourself back up.",
                                "strength",
                                "Triceps",
                                "Incline_Push-Up_Close-Grip"));
                addExercise(
                        new Exercise(
                                0,
                                "JM Press",
                                "1) Start the exercise the same way you would a close grip bench"
                                        + " press. You will lie on a flat bench while holding a barbell"
                                        + " at arms length (fully extended) with the elbows in."
                                        + " However, instead of having the arms perpendicular to the"
                                        + " torso, make sure the bar is set in a direct line above the"
                                        + " upper chest. This will be your starting position.\\n"
                                        + "2) Now beginning from a fully extended position lower the"
                                        + " bar down as if performing a lying triceps extension. Inhale"
                                        + " as you perform this movement. When you reach the half way"
                                        + " point, let the bar roll back about one inch by moving the"
                                        + " upper arms towards your legs until they are perpendicular"
                                        + " to the torso. Tip: Keep the bend at the elbows constant as"
                                        + " you bring the upper arms forward.\\n"
                                        + "3) As you exhale, press the bar back up by using the triceps"
                                        + " to perform a close grip bench press.\\n"
                                        + "4) Now go back to the starting position and start over.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Triceps",
                                "JM_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Alternate Hammer Curl",
                                "1) Stand up with your torso upright and a dumbbell in each hand"
                                        + " being held at arms length. The elbows should be close to"
                                        + " the torso.\\n"
                                        + "2) The palms of the hands should be facing your torso. This"
                                        + " will be your starting position.\\n"
                                        + "3) While holding the upper arm stationary, curl the right"
                                        + " weight forward while contracting the biceps as you breathe"
                                        + " out. Continue the movement until your biceps is fully"
                                        + " contracted and the dumbbells are at shoulder level. Hold"
                                        + " the contracted position for a second as you squeeze the"
                                        + " biceps. Tip: Only the forearms should move.\\n"
                                        + "4) Slowly begin to bring the dumbbells back to starting"
                                        + " position as your breathe in.\\n"
                                        + "5) Repeat the movement with the left hand. This equals one"
                                        + " repetition.\\n"
                                        + "6) Continue alternating in this manner for the recommended"
                                        + " amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Alternate_Hammer_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Alternate Incline Dumbbell Curl",
                                "1) Sit down on an incline bench with a dumbbell in each hand being"
                                        + " held at arms length. Tip: Keep the elbows close to the"
                                        + " torso.This will be your starting position.\\n"
                                        + "2) While holding the upper arm stationary, curl the right"
                                        + " weight forward while contracting the biceps as you breathe"
                                        + " out. As you do so, rotate the hand so that the palm is"
                                        + " facing up. Continue the movement until your biceps is fully"
                                        + " contracted and the dumbbells are at shoulder level. Hold"
                                        + " the contracted position for a second as you squeeze the"
                                        + " biceps. Tip: Only the forearms should move.\\n"
                                        + "3) Slowly begin to bring the dumbbell back to starting"
                                        + " position as your breathe in.\\n"
                                        + "4) Repeat the movement with the left hand. This equals one"
                                        + " repetition.\\n"
                                        + "5) Continue alternating in this manner for the recommended"
                                        + " amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Alternate_Incline_Dumbbell_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Curl",
                                "1) Stand up with your torso upright while holding a barbell at a"
                                        + " shoulder-width grip. The palm of your hands should be"
                                        + " facing forward and the elbows should be close to the torso."
                                        + " This will be your starting position.\\n"
                                        + "2) While holding the upper arms stationary, curl the weights"
                                        + " forward while contracting the biceps as you breathe out."
                                        + " Tip: Only the forearms should move.\\n"
                                        + "3) Continue the movement until your biceps are fully"
                                        + " contracted and the bar is at shoulder level. Hold the"
                                        + " contracted position for a second and squeeze the biceps"
                                        + " hard.\\n"
                                        + "4) Slowly begin to bring the bar back to starting position"
                                        + " as your breathe in.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Barbell_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Curls Lying Against An Incline",
                                "1) Lie against an incline bench, with your arms holding a barbell"
                                        + " and hanging down in a horizontal line. This will be your"
                                        + " starting position.\\n"
                                        + "2) While keeping the upper arms stationary, curl the weight"
                                        + " up as high as you can while squeezing the biceps. Breathe"
                                        + " out as you perform this portion of the movement. Tip: Only"
                                        + " the forearms should move. Do not swing the arms.\\n"
                                        + "3) After a second contraction, slowly go back to the"
                                        + " starting position as you inhale. Tip: Make sure that you go"
                                        + " all of the way down.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Barbell_Curls_Lying_Against_An_Incline"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Hammer Curls - Rope Attachment",
                                "1) Attach a rope attachment to a low pulley and stand facing the"
                                        + " machine about 12 inches away from it.\\n"
                                        + "2) Grasp the rope with a neutral (palms-in) grip and stand"
                                        + " straight up keeping the natural arch of the back and your"
                                        + " torso stationary.\\n"
                                        + "3) Put your elbows in by your side and keep them there"
                                        + " stationary during the entire movement. Tip: Only the"
                                        + " forearms should move; not your upper arms. This will be"
                                        + " your starting position.\\n"
                                        + "4) Using your biceps, pull your arms up as you exhale until"
                                        + " your biceps touch your forearms. Tip: Remember to keep the"
                                        + " elbows in and your upper arms stationary.\\n"
                                        + "5) After a 1 second contraction where you squeeze your"
                                        + " biceps, slowly start to bring the weight back to the"
                                        + " original position.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Cable_Hammer_Curls_-_Rope_Attachment"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Preacher Curl",
                                "1) Place a preacher bench about 2 feet in front of a pulley"
                                        + " machine.\\n"
                                        + "2) Attach a straight bar to the low pulley.\\n"
                                        + "3) Sit at the preacher bench with your elbow and upper arms"
                                        + " firmly on top of the bench pad and have someone hand you"
                                        + " the bar from the low pulley.\\n"
                                        + "4) Grab the bar and fully extend your arms on top of the"
                                        + " preacher bench pad. This will be your starting position.\\n"
                                        + "5) Now start pilling the weight up towards your shoulders"
                                        + " and squeeze the biceps hard at the top of the movement."
                                        + " Exhale as you perform this motion. Also, hold for a second"
                                        + " at the top.\\n"
                                        + "6) Now slowly lower the weight to the starting position.\\n"
                                        + "7) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Cable_Preacher_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip EZ Bar Curl",
                                "1) Stand up with your torso upright while holding an E-Z Curl Bar"
                                        + " at the closer inner handle. The palm of your hands should"
                                        + " be facing forward and they should be slightly tilted"
                                        + " inwards due to the shape of the bar. The elbows should be"
                                        + " close to the torso. This will be your starting position.\\n"
                                        + "2) While holding the upper arms stationary, curl the weights"
                                        + " forward while contracting the biceps as you breathe out."
                                        + " Tip: Only the forearms should move.\\n"
                                        + "3) Continue the movement until your biceps are fully"
                                        + " contracted and the bar is at shoulder level. Hold the"
                                        + " contracted position for a second and squeeze the biceps"
                                        + " hard.\\n"
                                        + "4) Slowly begin to bring the bar back to starting position"
                                        + " as your breathe in.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Close-Grip_EZ_Bar_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip EZ-Bar Curl with Band",
                                "1) Attach a band to each end of the bar. Take the bar, placing a"
                                        + " foot on the middle of the band. Stand upright with a"
                                        + " narrow, supinated grip on the EZ bar. The elbows should be"
                                        + " close to the torso. This will be your starting position.\\n"
                                        + "2) While keeping the upper arms in place, flex the elbows to"
                                        + " execute the curl. Exhale as the weight is lifted.\\n"
                                        + "3) Continue the movement until your biceps are fully"
                                        + " contracted and the bar is at shoulder level. Hold the"
                                        + " contracted position for a second and squeeze the biceps"
                                        + " hard.\\n"
                                        + "4) Slowly begin to bring the bar back to starting position"
                                        + " as your breathe in.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Biceps",
                                "Close-Grip_EZ-Bar_Curl_with_Band"));
                addExercise(
                        new Exercise(
                                0,
                                "Band Assisted Pull-Up",
                                "1) Choke the band around the center of the pullup bar. You can use"
                                        + " different bands to provide varying levels of assistance.\\n"
                                        + "2) Pull the end of the band down, and place one bent knee"
                                        + " into the loop, ensuring it won't slip out. Take a medium to"
                                        + " wide grip on the bar. This will be your starting"
                                        + " position.\\n"
                                        + "3) Pull yourself upward by contracting the lats as you flex"
                                        + " the elbow. The elbow should be driven to your side. Pull to"
                                        + " the front, attempting to get your chin over the bar. Avoid"
                                        + " swinging or jerking movements.\\n"
                                        + "4) After a brief pause, return to the starting position.",
                                "strength",
                                "Lats",
                                "Band_Assisted_Pull-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Catch and Overhead Throw",
                                "1) Begin standing while facing a wall or a partner.\\n"
                                        + "2) Using both hands, position the ball behind your head,"
                                        + " stretching as much as possible, and forcefully throw the"
                                        + " ball forward.\\n"
                                        + "3) Ensure that you follow your throw through, being prepared"
                                        + " to receive your rebound from your throw. If you are"
                                        + " throwing against the wall, make sure that you stand close"
                                        + " enough to the wall to receive the rebound, and aim a little"
                                        + " higher than you would with a partner.",
                                "plyometrics",
                                "Lats",
                                "Catch_and_Overhead_Throw"));
                addExercise(
                        new Exercise(
                                0,
                                "Chin-Up",
                                "1) Grab the pull-up bar with the palms facing your torso and a"
                                        + " grip closer than the shoulder width.\\n"
                                        + "2) As you have both arms extended in front of you holding"
                                        + " the bar at the chosen grip width, keep your torso as"
                                        + " straight as possible while creating a curvature on your"
                                        + " lower back and sticking your chest out. This is your"
                                        + " starting position. Tip: Keeping the torso as straight as"
                                        + " possible maximizes biceps stimulation while minimizing back"
                                        + " involvement.\\n"
                                        + "3) As you breathe out, pull your torso up until your head is"
                                        + " around the level of the pull-up bar. Concentrate on using"
                                        + " the biceps muscles in order to perform the movement. Keep"
                                        + " the elbows close to your body. Tip: The upper torso should"
                                        + " remain stationary as it moves through space and only the"
                                        + " arms should move. The forearms should do no other work"
                                        + " other than hold the bar.\\n"
                                        + "4) After a second of squeezing the biceps in the contracted"
                                        + " position, slowly lower your torso back to the starting"
                                        + " position; when your arms are fully extended. Breathe in as"
                                        + " you perform this portion of the movement.\\n"
                                        + "5) Repeat this motion for the prescribed amount of"
                                        + " repetitions.",
                                "strength",
                                "Lats",
                                "Chin-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Close-Grip Front Lat Pulldown",
                                "1) Sit down on a pull-down machine with a wide bar attached to the"
                                        + " top pulley. Make sure that you adjust the knee pad of the"
                                        + " machine to fit your height. These pads will prevent your"
                                        + " body from being raised by the resistance attached to the"
                                        + " bar.\\n"
                                        + "2) Grab the bar with the palms facing forward using the"
                                        + " prescribed grip. Note on grips: For a wide grip, your hands"
                                        + " need to be spaced out at a distance wider than your"
                                        + " shoulder width. For a medium grip, your hands need to be"
                                        + " spaced out at a distance equal to your shoulder width and"
                                        + " for a close grip at a distance smaller than your shoulder"
                                        + " width.\\n"
                                        + "3) As you have both arms extended in front of you - while"
                                        + " holding the bar at the chosen grip width - bring your torso"
                                        + " back around 30 degrees or so while creating a curvature on"
                                        + " your lower back and sticking your chest out. This is your"
                                        + " starting position.\\n"
                                        + "4) As you breathe out, bring the bar down until it touches"
                                        + " your upper chest by drawing the shoulders and the upper"
                                        + " arms down and back. Tip: Concentrate on squeezing the back"
                                        + " muscles once you reach the full contracted position. The"
                                        + " upper torso should remain stationary (only the arms should"
                                        + " move). The forearms should do no other work except for"
                                        + " holding the bar; therefore do not try to pull the bar down"
                                        + " using the forearms.\\n"
                                        + "5) After a second in the contracted position, while"
                                        + " squeezing your shoulder blades together, slowly raise the"
                                        + " bar back to the starting position when your arms are fully"
                                        + " extended and the lats are fully stretched. Inhale during"
                                        + " this portion of the movement.\\n"
                                        + "6) 6. Repeat this motion for the prescribed amount of"
                                        + " repetitions.",
                                "strength",
                                "Lats",
                                "Close-Grip_Front_Lat_Pulldown"));
                addExercise(
                        new Exercise(
                                0,
                                "Kneeling High Pulley Row",
                                "1) Select the appropriate weight using a pulley that is above your"
                                        + " head. Attach a rope to the cable and kneel a couple of feet"
                                        + " away, holding the rope out in front of you with both arms"
                                        + " extended. This will be your starting position.\\n"
                                        + "2) Initiate the movement by flexing the elbows and fully"
                                        + " retracting your shoulders, pulling the rope toward your"
                                        + " upper chest with your elbows out.\\n"
                                        + "3) After pausing briefly, slowly return to the starting"
                                        + " position.",
                                "strength",
                                "Lats",
                                "Kneeling_High_Pulley_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Kneeling Single-Arm High Pulley Row",
                                "1) Attach a single handle to a high pulley and make your weight"
                                        + " selection.\\n"
                                        + "2) Kneel in front of the cable tower, taking the cable with"
                                        + " one hand with your arm extended. This will be your starting"
                                        + " position.\\n"
                                        + "3) Starting with your palm facing forward, pull the weight"
                                        + " down to your torso by flexing the elbow and retract the"
                                        + " shoulder blade. As you do so, rotate the wrist so that at"
                                        + " the completion of the movement, your palm is now facing"
                                        + " you.\\n"
                                        + "4) After a brief pause, return to the starting position.",
                                "strength",
                                "Lats",
                                "Kneeling_Single-Arm_High_Pulley_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Iso Row",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " height so that the handles are at chest level. Grasp the"
                                        + " handles with either a neutral or pronated grip. This will"
                                        + " be your starting position.\\n"
                                        + "2) Pull the handles towards your torso, retracting your"
                                        + " shoulder blades as you flex the elbow.\\n"
                                        + "3) Pause at the bottom of the motion, and then slowly return"
                                        + " the handles to the starting position. For multiple"
                                        + " repetitions, avoid completely returning the weight to the"
                                        + " stops to keep tension on the muscles being worked.",
                                "strength",
                                "Lats",
                                "Leverage_Iso_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "One Arm Lat Pulldown",
                                "1) Select an appropriate weight and adjust the knee pad to help"
                                        + " keep you down. Grasp the handle with a pronated grip. This"
                                        + " will be your starting position.\\n"
                                        + "2) Pull the handle down, squeezing your elbow to your side"
                                        + " as you flex the elbow.\\n"
                                        + "3) Pause at the bottom of the motion, and then slowly return"
                                        + " the handle to the starting position.\\n"
                                        + "4) For multiple repetitions, avoid completely returning the"
                                        + " weight to keep tension on the muscles being worked.",
                                "strength",
                                "Lats",
                                "One_Arm_Lat_Pulldown"));
                addExercise(
                        new Exercise(
                                0,
                                "Bent Over Barbell Row",
                                "1) Holding a barbell with a pronated grip (palms facing down),"
                                        + " bend your knees slightly and bring your torso forward, by"
                                        + " bending at the waist, while keeping the back straight until"
                                        + " it is almost parallel to the floor. Tip: Make sure that you"
                                        + " keep the head up. The barbell should hang directly in front"
                                        + " of you as your arms hang perpendicular to the floor and"
                                        + " your torso. This is your starting position.\\n"
                                        + "2) Now, while keeping the torso stationary, breathe out and"
                                        + " lift the barbell to you. Keep the elbows close to the body"
                                        + " and only use the forearms to hold the weight. At the top"
                                        + " contracted position, squeeze the back muscles and hold for"
                                        + " a brief pause.\\n"
                                        + "3) Then inhale and slowly lower the barbell back to the"
                                        + " starting position.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Middle back",
                                "Bent_Over_Barbell_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Bent Over One-Arm Long Bar Row",
                                "1) Put weight on one of the ends of an Olympic barbell. Make sure"
                                        + " that you either place the other end of the barbell in the"
                                        + " corner of two walls; or put a heavy object on the ground so"
                                        + " the barbell cannot slide backward.\\n"
                                        + "2) Bend forward until your torso is as close to parallel"
                                        + " with the floor as you can and keep your knees slightly"
                                        + " bent.\\n"
                                        + "3) Now grab the bar with one arm just behind the plates on"
                                        + " the side where the weight was placed and put your other"
                                        + " hand on your knee. This will be your starting position.\\n"
                                        + "4) Pull the bar straight up with your elbow in (to maximize"
                                        + " back stimulation) until the plates touch your lower chest."
                                        + " Squeeze the back muscles as you lift the weight up and hold"
                                        + " for a second at the top of the movement. Breathe out as you"
                                        + " lift the weight. Tip: Do not allow for any swinging of the"
                                        + " torso. Only the arm should move.\\n"
                                        + "5) Slowly lower the bar to the starting position getting a"
                                        + " nice stretch on the lats. Tip: Do not let the plates touch"
                                        + " the floor. To ensure the best range of motion, I recommend"
                                        + " using small plates (25-lb ones) as opposed to larger plates"
                                        + " (like 35-45lb ones).\\n"
                                        + "6) Repeat for the recommended amount of repetitions and"
                                        + " switch arms.",
                                "strength",
                                "Middle back",
                                "Bent_Over_One-Arm_Long_Bar_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Bent Over Two-Dumbbell Row",
                                "1) With a dumbbell in each hand (palms facing your torso), bend"
                                        + " your knees slightly and bring your torso forward by bending"
                                        + " at the waist; as you bend make sure to keep your back"
                                        + " straight until it is almost parallel to the floor. Tip:"
                                        + " Make sure that you keep the head up. The weights should"
                                        + " hang directly in front of you as your arms hang"
                                        + " perpendicular to the floor and your torso. This is your"
                                        + " starting position.\\n"
                                        + "2) While keeping the torso stationary, lift the dumbbells to"
                                        + " your side (as you breathe out), keeping the elbows close to"
                                        + " the body (do not exert any force with the forearm other"
                                        + " than holding the weights). On the top contracted position,"
                                        + " squeeze the back muscles and hold for a second.\\n"
                                        + "3) Slowly lower the weight again to the starting position as"
                                        + " you inhale.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Middle back",
                                "Bent_Over_Two-Dumbbell_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Bent Over Two-Dumbbell Row With Palms In",
                                "1) With a dumbbell in each hand (palms facing each other), bend"
                                        + " your knees slightly and bring your torso forward, by"
                                        + " bending at the waist, while keeping the back straight until"
                                        + " it is almost parallel to the floor. Tip: Make sure that you"
                                        + " keep the head up. The weights should hang directly in front"
                                        + " of you as your arms hang perpendicular to the floor and"
                                        + " your torso. This is your starting position.\\n"
                                        + "2) While keeping the torso stationary, lift the dumbbells to"
                                        + " your side as you breathe out, squeezing your shoulder"
                                        + " blades together. On the top contracted position, squeeze"
                                        + " the back muscles and hold for a second.\\n"
                                        + "3) Slowly lower the weight again to the starting position as"
                                        + " you inhale.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Middle back",
                                "Bent_Over_Two-Dumbbell_Row_With_Palms_In"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Incline Row",
                                "1) Using a neutral grip, lean into an incline bench.\\n"
                                        + "2) Take a dumbbell in each hand with a neutral grip,"
                                        + " beginning with the arms straight. This will be your"
                                        + " starting position.\\n"
                                        + "3) Retract the shoulder blades and flex the elbows to row"
                                        + " the dumbbells to your side.\\n"
                                        + "4) Pause at the top of the motion, and then return to the"
                                        + " starting position.",
                                "strength",
                                "Middle back",
                                "Dumbbell_Incline_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Inverted Row",
                                "1) Position a bar in a rack to about waist height. You can also"
                                        + " use a smith machine.\\n"
                                        + "2) Take a wider than shoulder width grip on the bar and"
                                        + " position yourself hanging underneath the bar. Your body"
                                        + " should be straight with your heels on the ground with your"
                                        + " arms fully extended. This will be your starting"
                                        + " position.\\n"
                                        + "3) Begin by flexing the elbow, pulling your chest towards"
                                        + " the bar. Retract your shoulder blades as you perform the"
                                        + " movement.\\n"
                                        + "4) Pause at the top of the motion, and return yourself to"
                                        + " the start position.\\n"
                                        + "5) Repeat for the desired number of repetitions.",
                                "strength",
                                "Middle back",
                                "Inverted_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Inverted Row with Straps",
                                "1) Hang a rope or suspension straps from a rack or other stable"
                                        + " object. Grasp the ends and position yourself in a supine"
                                        + " position hanging from the ropes. Your body should be"
                                        + " straight with your heels on the ground with your arms fully"
                                        + " extended. This will be your starting position.\\n"
                                        + "2) Begin by flexing the elbow, pulling your chest to your"
                                        + " hands. Retract your shoulder blades as you perform the"
                                        + " movement.\\n"
                                        + "3) Pause at the top of the motion, and return yourself to"
                                        + " the start position.\\n"
                                        + "4) Repeat for the desired number of repetitions.",
                                "strength",
                                "Middle back",
                                "Inverted_Row_with_Straps"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage High Row",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " height so that you can just reach the handles above you."
                                        + " Adjust the knee pad to help keep you down. Grasp the"
                                        + " handles with a pronated grip. This will be your starting"
                                        + " position.\\n"
                                        + "2) Pull the handles towards your torso, retracting your"
                                        + " shoulder blades as you flex the elbow.\\n"
                                        + "3) Pause at the bottom of the motion, and then slowly return"
                                        + " the handles to the starting position.\\n"
                                        + "4) For multiple repetitions, avoid completely returning the"
                                        + " weight to the stops to keep tension on the muscles being"
                                        + " worked.",
                                "strength",
                                "Middle back",
                                "Leverage_High_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Balance Board",
                                "1) Place a balance board in front of you.\\n"
                                        + "2) Stand up on it and try to balance yourself.\\n"
                                        + "3) Hold the balance for as long as desired.",
                                "strength",
                                "Calves",
                                "Balance_Board"));
                addExercise(
                        new Exercise(
                                0,
                                "Knee Circles",
                                "1) Stand with your legs together and hands by your waist.\\n"
                                        + "2) Now move your knees in a circular motion as you breathe"
                                        + " normally.\\n"
                                        + "3) Repeat for the recommended amount of repetitions.",
                                "stretching",
                                "Calves",
                                "Knee_Circles"));
                addExercise(
                        new Exercise(
                                0,
                                "Ankle Circles",
                                "1) Use a sturdy object like a squat rack to hold yourself.\\n"
                                        + "2) Lift the right leg in the air (just around 2 inches from"
                                        + " the floor) and perform a circular motion with the big toe."
                                        + " Pretend that you are drawing a big circle with it. Tip: One"
                                        + " circle equals 1 repetition. Breathe normally as you perform"
                                        + " the movement.\\n"
                                        + "3) When you are done with the right foot, then repeat with"
                                        + " the left leg.",
                                "stretching",
                                "Calves",
                                "Ankle_Circles"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Seated Calf Raise",
                                "1) Place a block about 12 inches in front of a flat bench.\\n"
                                        + "2) Sit on the bench and place the ball of your feet on the"
                                        + " block.\\n"
                                        + "3) Have someone place a barbell over your upper thighs about"
                                        + " 3 inches above your knees and hold it there. This will be"
                                        + " your starting position.\\n"
                                        + "4) Raise up on your toes as high as possible as you squeeze"
                                        + " the calves and as you breathe out.\\n"
                                        + "5) After a second contraction, slowly go back to the"
                                        + " starting position. Tip: To get maximum benefit stretch your"
                                        + " calves as far as you can.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Calves",
                                "Barbell_Seated_Calf_Raise"));
                addExercise(
                        new Exercise(
                                0,
                                "Calf Press",
                                "1) Adjust the seat so that your legs are only slightly bent in the"
                                        + " start position. The balls of your feet should be firmly on"
                                        + " the platform.\\n"
                                        + "2) Select an appropriate weight, and grasp the handles. This"
                                        + " will be your starting position.\\n"
                                        + "3) Straighten the legs by extending the knees, just barely"
                                        + " lifting the weight from the stack. Your ankle should be"
                                        + " fully flexed, toes pointing up. Execute the movement by"
                                        + " pressing downward through the balls of your feet as far as"
                                        + " possible.\\n"
                                        + "4) After a brief pause, reverse the motion and repeat.",
                                "strength",
                                "Calves",
                                "Calf_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Calf Press On The Leg Press Machine",
                                "1) Using a leg press machine, sit down on the machine and place"
                                        + " your legs on the platform directly in front of you at a"
                                        + " medium (shoulder width) foot stance.\\n"
                                        + "2) Lower the safety bars holding the weighted platform in"
                                        + " place and press the platform all the way up until your legs"
                                        + " are fully extended in front of you without locking your"
                                        + " knees. (Note: In some leg press units you can leave the"
                                        + " safety bars on for increased safety. If your leg press unit"
                                        + " allows for this, then this is the preferred method of"
                                        + " performing the exercise.) Your torso and the legs should"
                                        + " make perfect 90-degree angle. Now carefully place your toes"
                                        + " and balls of your feet on the lower portion of the platform"
                                        + " with the heels extending off. Toes should be facing"
                                        + " forward, outwards or inwards as described at the beginning"
                                        + " of the chapter. This will be your starting position.\\n"
                                        + "3) Press on the platform by raising your heels as you"
                                        + " breathe out by extending your ankles as high as possible"
                                        + " and flexing your calf. Ensure that the knee is kept"
                                        + " stationary at all times. There should be no bending at any"
                                        + " time. Hold the contracted position by a second before you"
                                        + " start to go back down.\\n"
                                        + "4) Go back slowly to the starting position as you breathe in"
                                        + " by lowering your heels as you bend the ankles until calves"
                                        + " are stretched.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Calves",
                                "Calf_Press_On_The_Leg_Press_Machine"));
                addExercise(
                        new Exercise(
                                0,
                                "Calf Raises - With Bands",
                                "1) Grab an exercise band and stand on it with your toes making"
                                        + " sure that the length of the band between the foot and the"
                                        + " arms is the same for both sides.\\n"
                                        + "2) While holding the handles of the band, raise the arms to"
                                        + " the side of your head as if you were getting ready to"
                                        + " perform a shoulder press. The palms should be facing"
                                        + " forward with the elbows bent and to the sides. This"
                                        + " movement will create tension on the band. This will be your"
                                        + " starting position.\\n"
                                        + "3) Keeping the hands by your shoulder, stand up on your toes"
                                        + " as you exhale and contract the calves hard at the top of"
                                        + " the movement.\\n"
                                        + "4) After a one second contraction, slowly go back down to"
                                        + " the starting position.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Calves",
                                "Calf_Raises_-_With_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Calf Stretch Elbows Against Wall",
                                "1) Stand facing a wall from a couple feet away.\\n"
                                        + "2) Lean against the wall, placing your weight on your"
                                        + " forearms.\\n"
                                        + "3) Attempt to keep your heels on the ground. Hold for 10-20"
                                        + " seconds. You may move further or closer the wall, making it"
                                        + " more or less difficult, respectively.",
                                "stretching",
                                "Calves",
                                "Calf_Stretch_Elbows_Against_Wall"));
                addExercise(
                        new Exercise(
                                0,
                                "Stiff Leg Barbell Good Morning",
                                "1) This exercise is best performed inside a squat rack for safety"
                                        + " purposes. To begin, first set the bar on a rack that best"
                                        + " matches your height. Once the correct height is chosen and"
                                        + " the bar is loaded, step under the bar and place the back of"
                                        + " your shoulders (slightly below the neck) across it.\\n"
                                        + "2) Hold on to the bar using both arms at each side and lift"
                                        + " it off the rack by first pushing with your legs and at the"
                                        + " same time straightening your torso.\\n"
                                        + "3) Step away from the rack and position your legs using a"
                                        + " shoulder width medium stance. Keep your head up at all"
                                        + " times as looking down will get you off balance and also"
                                        + " maintain a straight back. This will be your starting"
                                        + " position.\\n"
                                        + "4) Keeping your legs stationary, move your torso forward by"
                                        + " bending at the hips while inhaling. Lower your torso until"
                                        + " it is parallel with the floor.\\n"
                                        + "5) Begin to raise the bar as you exhale by elevating your"
                                        + " torso back to the starting position.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Lower back",
                                "Stiff_Leg_Barbell_Good_Morning"));
                addExercise(
                        new Exercise(
                                0,
                                "Superman",
                                "1) To begin, lie straight and face down on the floor or exercise"
                                        + " mat. Your arms should be fully extended in front of you."
                                        + " This is the starting position.\\n"
                                        + "2) Simultaneously raise your arms, legs, and chest off of"
                                        + " the floor and hold this contraction for 2 seconds. Tip:"
                                        + " Squeeze your lower back to get the best results from this"
                                        + " exercise. Remember to exhale during this movement. Note:"
                                        + " When holding the contracted position, you should look like"
                                        + " superman when he is flying.\\n"
                                        + "3) Slowly begin to lower your arms, legs and chest back down"
                                        + " to the starting position while inhaling.\\n"
                                        + "4) Repeat for the recommended amount of repetitions"
                                        + " prescribed in your program.",
                                "stretching",
                                "Lower back",
                                "Superman"));
                addExercise(
                        new Exercise(
                                0,
                                "Atlas Stone Trainer",
                                "1) This trainer is effective for developing Atlas Stone strength"
                                        + " for those who don't have access to stones, and are"
                                        + " typically made from bar ends or heavy pipe.\\n"
                                        + "2) Begin by loading the desired weight onto the bar."
                                        + " Straddle the weight, wrapping your arms around the"
                                        + " implement, bending at the hips.\\n"
                                        + "3) Begin by pulling the weight up past the knees, extending"
                                        + " through the hips. As the weight clears the knees, it can be"
                                        + " lapped by resting it on your thighs and sitting back,"
                                        + " hugging it tightly to your chest.\\n"
                                        + "4) Finish the movement by extending through your hips and"
                                        + " knees to raise the weight as high as possible. The weight"
                                        + " can be returned to the lap or to the ground for successive"
                                        + " repetitions.",
                                "strongman",
                                "Lower back",
                                "Atlas_Stone_Trainer"));
                addExercise(
                        new Exercise(
                                0,
                                "Axle Deadlift",
                                "1) Approach the bar so that it is centered over your feet. You"
                                        + " feet should be about hip width apart. Bend at the hip to"
                                        + " grip the bar at shoulder width, allowing your shoulder"
                                        + " blades to protract. Typically, you would use an over/under"
                                        + " grip.\\n"
                                        + "2) With your feet and your grip set, take a big breath and"
                                        + " then lower your hips and flex the knees until your shins"
                                        + " contact the bar. Look forward with your head, keep your"
                                        + " chest up and your back arched, and begin driving through"
                                        + " the heels to move the weight upward.\\n"
                                        + "3) After the bar passes the knees, aggressively pull the bar"
                                        + " back, pulling your shoulder blades together as you drive"
                                        + " your hips forward into the bar.\\n"
                                        + "4) Lower the bar by bending at the hips and guiding it to"
                                        + " the floor.",
                                "strongman",
                                "Lower back",
                                "Axle_Deadlift"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Deadlift",
                                "1) Stand in front of a loaded barbell.\\n"
                                        + "2) While keeping the back as straight as possible, bend your"
                                        + " knees, bend forward and grasp the bar using a medium"
                                        + " (shoulder width) overhand grip. This will be the starting"
                                        + " position of the exercise. Tip: If it is difficult to hold"
                                        + " on to the bar with this grip, alternate your grip or use"
                                        + " wrist straps.\\n"
                                        + "3) While holding the bar, start the lift by pushing with"
                                        + " your legs while simultaneously getting your torso to the"
                                        + " upright position as you breathe out. In the upright"
                                        + " position, stick your chest out and contract the back by"
                                        + " bringing the shoulder blades back. Think of how the"
                                        + " soldiers in the military look when they are in standing in"
                                        + " attention.\\n"
                                        + "4) Go back to the starting position by bending at the knees"
                                        + " while simultaneously leaning the torso forward at the waist"
                                        + " while keeping the back straight. When the weights on the"
                                        + " bar touch the floor you are back at the starting position"
                                        + " and ready to perform another repetition.\\n"
                                        + "5) Perform the amount of repetitions prescribed in the"
                                        + " program.",
                                "strength",
                                "Lower back",
                                "Barbell_Deadlift"));
                addExercise(
                        new Exercise(
                                0,
                                "Deficit Deadlift",
                                "1) Begin by having a platform or weight plates that you can stand"
                                        + " on, usually 1-3 inches in height. Approach the bar so that"
                                        + " it is centered over your feet. You feet should be about hip"
                                        + " width apart. Bend at the hip to grip the bar at shoulder"
                                        + " width, allowing your shoulder blades to protract."
                                        + " Typically, you would use an overhand grip or an over/under"
                                        + " grip on heavier sets.\\n"
                                        + "2) With your feet, and your grip set, take a big breath and"
                                        + " then lower your hips and bend the knees until your shins"
                                        + " contact the bar. Look forward with your head, keep your"
                                        + " chest up and your back arched, and begin driving through"
                                        + " the heels to move the weight upward. After the bar passes"
                                        + " the knees, aggressively pull the bar back, pulling your"
                                        + " shoulder blades together as you drive your hips forward"
                                        + " into the bar.\\n"
                                        + "3) Lower the bar by bending at the hips and guiding it to"
                                        + " the floor.",
                                "powerlifting",
                                "Lower back",
                                "Deficit_Deadlift"));
                addExercise(
                        new Exercise(
                                0,
                                "Hug A Ball",
                                "1) Seat yourself on the floor.\\n"
                                        + "2) Straddle an exercise ball between both legs and lower"
                                        + " your hips down toward the floor.\\n"
                                        + "3) Hug your arms around the ball to support your body."
                                        + " Adjust your legs so that your feet are flat on the floor"
                                        + " and your knees line up over your ankles. Keep a good grip"
                                        + " on the ball so it doesn't roll away from you and send you"
                                        + " back onto your buttocks.",
                                "stretching",
                                "Lower back",
                                "Hug_A_Ball"));
                addExercise(
                        new Exercise(
                                0,
                                "Hyperextensions (Back Extensions)",
                                "1) Lie face down on a hyperextension bench, tucking your ankles"
                                        + " securely under the footpads.\\n"
                                        + "2) Adjust the upper pad if possible so your upper thighs lie"
                                        + " flat across the wide pad, leaving enough room for you to"
                                        + " bend at the waist without any restriction.\\n"
                                        + "3) With your body straight, cross your arms in front of you"
                                        + " (my preference) or behind your head. This will be your"
                                        + " starting position. Tip: You can also hold a weight plate"
                                        + " for extra resistance in front of you under your crossed"
                                        + " arms.\\n"
                                        + "4) Start bending forward slowly at the waist as far as you"
                                        + " can while keeping your back flat. Inhale as you perform"
                                        + " this movement. Keep moving forward until you feel a nice"
                                        + " stretch on the hamstrings and you can no longer keep going"
                                        + " without a rounding of the back. Tip: Never round the back"
                                        + " as you perform this exercise. Also, some people can go"
                                        + " farther than others. The key thing is that you go as far as"
                                        + " your body allows you to without rounding the back.\\n"
                                        + "5) Slowly raise your torso back to the initial position as"
                                        + " you inhale. Tip: Avoid the temptation to arch your back"
                                        + " past a straight line. Also, do not swing the torso at any"
                                        + " time in order to protect the back from injury.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Lower back",
                                "Hyperextensions_Back_Extensions"));
                addExercise(
                        new Exercise(
                                0,
                                "Bottoms-Up Clean From The Hang Position",
                                "1) Initiate the exercise by standing upright with a kettlebell in"
                                        + " one hand.\\n"
                                        + "2) Swing the kettlebell back forcefully and then reverse the"
                                        + " motion forcefully. Crush the kettlebell handle as hard as"
                                        + " possible and raise the kettlebell to your shoulder.",
                                "strength",
                                "Forearms",
                                "Bottoms-Up_Clean_From_The_Hang_Position"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Wrist Curl",
                                "1) Start out by placing a flat bench in front of a low pulley"
                                        + " cable that has a straight bar attachment.\\n"
                                        + "2) Use your arms to grab the cable bar with a narrow to"
                                        + " shoulder width supinated grip (palms up) and bring them up"
                                        + " so that your forearms are resting against the top of your"
                                        + " thighs. Your wrists should be hanging just beyond your"
                                        + " knees.\\n"
                                        + "3) Start out by curling your wrist upwards and exhaling."
                                        + " Keep the contraction for a second.\\n"
                                        + "4) Slowly lower your wrists back down to the starting"
                                        + " position while inhaling.\\n"
                                        + "5) Your forearms should be stationary as your wrist is the"
                                        + " only movement needed to perform this exercise.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Forearms",
                                "Cable_Wrist_Curl"));
                addExercise(
                        new Exercise(
                                0,
                                "Farmer's Walk",
                                "1) There are various implements that can be used for the farmers"
                                        + " walk. These can also be performed with heavy dumbbells or"
                                        + " short bars if these implements aren't available. Begin by"
                                        + " standing between the implements.\\n"
                                        + "2) After gripping the handles, lift them up by driving"
                                        + " through your heels, keeping your back straight and your"
                                        + " head up.\\n"
                                        + "3) Walk taking short, quick steps, and don't forget to"
                                        + " breathe. Move for a given distance, typically 50-100 feet,"
                                        + " as fast as possible.",
                                "strongman",
                                "Forearms",
                                "Farmers_Walk"));
                addExercise(
                        new Exercise(
                                0,
                                "Finger Curls",
                                "1) Hold a barbell with both hands and your palms facing up; hands"
                                        + " spaced about shoulder width.\\n"
                                        + "2) Place your feet flat on the floor, at a distance that is"
                                        + " slightly wider than shoulder width apart. This will be your"
                                        + " starting position.\\n"
                                        + "3) Lower the bar as far as possible by extending the"
                                        + " fingers. Allowing the bar to roll down the hands, catch the"
                                        + " bar with the final joint in the fingers.\\n"
                                        + "4) Now curl bar up as high as possible by closing your hands"
                                        + " while exhaling. Hold the contraction at the top.",
                                "strength",
                                "Forearms",
                                "Finger_Curls"));
                addExercise(
                        new Exercise(
                                0,
                                "Kneeling Forearm Stretch",
                                "1) Start by kneeling on a mat with your palms flat and your"
                                        + " fingers pointing back toward your knees.\\n"
                                        + "2) Slowly lean back keeping your palms flat on the floor"
                                        + " until you feel a stretch in your wrists and forearms. Hold"
                                        + " for 20-30 seconds.",
                                "stretching",
                                "Forearms",
                                "Kneeling_Forearm_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Palms-Down Dumbbell Wrist Curl Over A Bench",
                                "1) Start out by placing two dumbbells on one side of a flat"
                                        + " bench.\\n"
                                        + "2) Kneel down on both of your knees so that your body is"
                                        + " facing the flat bench.\\n"
                                        + "3) Use your arms to grab both of the dumbbells with a"
                                        + " pronated grip (palms facing down) and bring them up so that"
                                        + " your forearms are resting against the flat bench. Your"
                                        + " wrists should be hanging over the edge.\\n"
                                        + "4) Start out by curling your wrist upwards and exhaling.\\n"
                                        + "5) Slowly lower your wrists back down to the starting"
                                        + " position while inhaling.\\n"
                                        + "6) Your forearms should be stationary as your wrist is the"
                                        + " only movement needed to perform this exercise.\\n"
                                        + "7) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Forearms",
                                "Palms-Down_Dumbbell_Wrist_Curl_Over_A_Bench"));
                addExercise(
                        new Exercise(
                                0,
                                "Palms-Down Wrist Curl Over A Bench",
                                "1) Start out by placing a barbell on one side of a flat bench.\\n"
                                        + "2) Kneel down on both of your knees so that your body is"
                                        + " facing the flat bench.\\n"
                                        + "3) Use your arms to grab the barbell with a pronated grip"
                                        + " (palms down) and bring them up so that your forearms are"
                                        + " resting against the flat bench. Your wrists should be"
                                        + " hanging over the edge.\\n"
                                        + "4) Start out by curling your wrist upwards and exhaling.\\n"
                                        + "5) Slowly lower your wrists back down to the starting"
                                        + " position while inhaling.\\n"
                                        + "6) Your forearms should be stationary as your wrist is the"
                                        + " only movement needed to perform this exercise.\\n"
                                        + "7) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Forearms",
                                "Palms-Down_Wrist_Curl_Over_A_Bench"));
                addExercise(
                        new Exercise(
                                0,
                                "Palms-Up Barbell Wrist Curl Over A Bench",
                                "1) Start out by placing a barbell on one side of a flat bench.\\n"
                                        + "2) Kneel down on both of your knees so that your body is"
                                        + " facing the flat bench.\\n"
                                        + "3) Use your arms to grab the barbell with a supinated grip"
                                        + " (palms up) and bring them up so that your forearms are"
                                        + " resting against the flat bench. Your wrists should be"
                                        + " hanging over the edge.\\n"
                                        + "4) Start out by curling your wrist upwards and exhaling.\\n"
                                        + "5) Slowly lower your wrists back down to the starting"
                                        + " position while inhaling.\\n"
                                        + "6) Your forearms should be stationary as your wrist is the"
                                        + " only movement needed to perform this exercise.\\n"
                                        + "7) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Forearms",
                                "Palms-Up_Barbell_Wrist_Curl_Over_A_Bench"));
                addExercise(
                        new Exercise(
                                0,
                                "Flutter Kicks",
                                "1) On a flat bench lie facedown with the hips on the edge of the"
                                        + " bench, the legs straight with toes high off the floor and"
                                        + " with the arms on top of the bench holding on to the front"
                                        + " edge.\\n"
                                        + "2) Squeeze your glutes and hamstrings and straighten the"
                                        + " legs until they are level with the hips. This will be your"
                                        + " starting position.\\n"
                                        + "3) Start the movement by lifting the left leg higher than"
                                        + " the right leg.\\n"
                                        + "4) Then lower the left leg as you lift the right leg.\\n"
                                        + "5) Continue alternating in this manner (as though you are"
                                        + " doing a flutter kick in water) until you have done the"
                                        + " recommended amount of repetitions for each leg. Make sure"
                                        + " that you keep a controlled movement at all times. Tip: You"
                                        + " will breathe normally as you perform this movement.",
                                "strength",
                                "Glutes",
                                "Flutter_Kicks"));
                addExercise(
                        new Exercise(
                                0,
                                "Glute Kickback",
                                "1) Kneel on the floor or an exercise mat and bend at the waist"
                                        + " with your arms extended in front of you (perpendicular to"
                                        + " the torso) in order to get into a kneeling push-up position"
                                        + " but with the arms spaced at shoulder width. Your head"
                                        + " should be looking forward and the bend of the knees should"
                                        + " create a 90-degree angle between the hamstrings and the"
                                        + " calves. This will be your starting position.\\n"
                                        + "2) As you exhale, lift up your right leg until the"
                                        + " hamstrings are in line with the back while maintaining the"
                                        + " 90-degree angle bend. Contract the glutes throughout this"
                                        + " movement and hold the contraction at the top for a second."
                                        + " Tip: At the end of the movement the upper leg should be"
                                        + " parallel to the floor while the calf should be"
                                        + " perpendicular to it.\\n"
                                        + "3) Go back to the initial position as you inhale and now"
                                        + " repeat with the left leg.\\n"
                                        + "4) Continue to alternate legs until all of the recommended"
                                        + " repetitions have been performed.",
                                "strength",
                                "Glutes",
                                "Glute_Kickback"));
                addExercise(
                        new Exercise(
                                0,
                                "Hip Extension with Bands",
                                "1) Secure one end of the band to the lower portion of a post and"
                                        + " attach the other to one ankle.\\n"
                                        + "2) Facing the attachment point of the band, hold on to the"
                                        + " column to stabilize yourself.\\n"
                                        + "3) Keeping your head and your chest up, move the resisted"
                                        + " leg back as far as you can while keeping the knee"
                                        + " straight.\\n"
                                        + "4) Return the leg to the starting position.",
                                "strength",
                                "Glutes",
                                "Hip_Extension_with_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Hip Lift with Band",
                                "1) After choosing a suitable band, lay down in the middle of the"
                                        + " rack, after securing the band on either side of you. If"
                                        + " your rack doesn't have pegs, the band can be secured using"
                                        + " heavy dumbbells or similar objects, just ensure they won't"
                                        + " move.\\n"
                                        + "2) Adjust your position so that the band is directly over"
                                        + " your hips. Bend your knees and place your feet flat on the"
                                        + " floor. Your hands can be on the floor or holding the band"
                                        + " in position.\\n"
                                        + "3) Keeping your shoulders on the ground, drive through your"
                                        + " heels to raise your hips, pushing into the band as high as"
                                        + " you can.\\n"
                                        + "4) Pause at the top of the motion, and return to the"
                                        + " starting position.",
                                "powerlifting",
                                "Glutes",
                                "Hip_Lift_with_Band"));
                addExercise(
                        new Exercise(
                                0,
                                "One Knee To Chest",
                                "1) Start off by lying on the floor.\\n"
                                        + "2) Extend one leg straight and pull the other knee to your"
                                        + " chest. Hold under the knee joint to protect the kneecap.\\n"
                                        + "3) Gently tug that knee toward your nose.\\n"
                                        + "4) Switch sides. This stretches the buttocks and lower back"
                                        + " of the bent leg and the hip flexor of the straight leg.",
                                "stretching",
                                "Glutes",
                                "One_Knee_To_Chest"));
                addExercise(
                        new Exercise(
                                0,
                                "Physioball Hip Bridge",
                                "1) Lay on a ball so that your upper back is on the ball with your"
                                        + " hips unsupported. Both feet should be flat on the floor,"
                                        + " hip width apart or wider. This will be your starting"
                                        + " position.\\n"
                                        + "2) Begin by extending the hips using your glutes and"
                                        + " hamstrings, raising your hips upward as you bridge.\\n"
                                        + "3) Pause at the top of the motion and return to the starting"
                                        + " position.",
                                "strength",
                                "Glutes",
                                "Physioball_Hip_Bridge"));
                addExercise(
                        new Exercise(
                                0,
                                "Pull Through",
                                "1) Begin standing a few feet in front of a low pulley with a rope"
                                        + " or handle attached. Face away from the machine, straddling"
                                        + " the cable, with your feet set wide apart.\\n"
                                        + "2) Begin the movement by reaching through your legs as far"
                                        + " as possible, bending at the hips. Keep your knees slightly"
                                        + " bent. Keeping your arms straight, extend through the hip to"
                                        + " stand straight up. Avoid pulling upward through the"
                                        + " shoulders; all of the motion should originate through the"
                                        + " hips.",
                                "strength",
                                "Glutes",
                                "Pull_Through"));
                addExercise(
                        new Exercise(
                                0,
                                "Step-up with Knee Raise",
                                "1) Stand facing a box or bench of an appropriate height with your"
                                        + " feet together. This will be your starting position.\\n"
                                        + "2) Begin the movement by stepping up, putting your left foot"
                                        + " on the top of the bench. Extend through the hip and knee of"
                                        + " your front leg to stand up on the box. As you stand on the"
                                        + " box with your left leg, flex your right knee and hip,"
                                        + " bringing your knee as high as you can.\\n"
                                        + "3) Reverse this motion to step down off the box, and then"
                                        + " repeat the sequence on the opposite leg.",
                                "strength",
                                "Glutes",
                                "Step-up_with_Knee_Raise"));
                addExercise(
                        new Exercise(
                                0,
                                "Clean Shrug",
                                "1) Begin with a shoulder width, double overhand or hook grip, with"
                                        + " the bar hanging at the mid thigh position. Your back should"
                                        + " be straight and inclined slightly forward.\\n"
                                        + "2) Shrug your shoulders towards your ears. While this"
                                        + " exercise can usually by loaded with heavier weight than a"
                                        + " clean, avoid overloading to the point that the execution"
                                        + " slows down.",
                                "olympic weightlifting",
                                "Traps",
                                "Clean_Shrug"));
                addExercise(
                        new Exercise(
                                0,
                                "Smith Machine Upright Row",
                                "1) To begin, set the bar on the smith machine to a height that is"
                                        + " around the middle of your thighs. Once the correct height"
                                        + " is chosen and the bar is loaded, grasp the bar using a"
                                        + " pronated (palms forward) grip that is shoulder width apart."
                                        + " You may need some wrist wraps if using a significant amount"
                                        + " of weight.\\n"
                                        + "2) Lift the barbell up and fully extend your arms with your"
                                        + " back straight. There should be a slight bend at the elbows."
                                        + " This is the starting position.\\n"
                                        + "3) Use your side shoulders to lift the bar as you exhale."
                                        + " The bar should be close to the body as you move it up."
                                        + " Continue to lift it until it nearly touches your chin. Tip:"
                                        + " Your elbows should drive the motion. As you lift the bar,"
                                        + " your elbows should always be higher than your forearms."
                                        + " Also, keep your torso stationary and pause for a second at"
                                        + " the top of the movement.\\n"
                                        + "4) Lower the bar back down slowly to the starting position."
                                        + " Inhale as you perform this portion of the movement.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Smith_Machine_Upright_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Standing Dumbbell Upright Row",
                                "1) Grasp a dumbbell in each hand with a pronated (palms forward)"
                                        + " grip that is slightly less than shoulder width. The"
                                        + " dumbbells should be resting on top of your thighs. Your"
                                        + " arms should be extended with a slight bend at the elbows"
                                        + " and your back should be straight. This will be your"
                                        + " starting position.\\n"
                                        + "2) Use your side shoulders to lift the dumbbells as you"
                                        + " exhale. The dumbbells should be close to the body as you"
                                        + " move it up and the elbows should drive the motion. Continue"
                                        + " to lift them until they nearly touch your chin. Tip: Your"
                                        + " elbows should drive the motion. As you lift the dumbbells,"
                                        + " your elbows should always be higher than your forearms."
                                        + " Also, keep your torso stationary and pause for a second at"
                                        + " the top of the movement.\\n"
                                        + "3) Lower the dumbbells back down slowly to the starting"
                                        + " position. Inhale as you perform this portion of the"
                                        + " movement.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Standing_Dumbbell_Upright_Row"));
                addExercise(
                        new Exercise(
                                0,
                                "Upright Row - With Bands",
                                "1) To begin, stand on an exercise band so that tension begins at"
                                        + " arm's length. Grasp the handles using a pronated (palms"
                                        + " facing your thighs) grip that is slightly less than"
                                        + " shoulder width. The handles should be resting on top of"
                                        + " your thighs. Your arms should be extended with a slight"
                                        + " bend at the elbows and your back should be straight. This"
                                        + " will be your starting position.\\n"
                                        + "2) Use your side shoulders to lift the handles as you"
                                        + " exhale. The handles should be close to the body as you move"
                                        + " them up. Continue to lift the handles until they nearly"
                                        + " touches your chin. Tip: Your elbows should drive the"
                                        + " motion. As you lift the handles, your elbows should always"
                                        + " be higher than your forearms. Also, keep your torso"
                                        + " stationary and pause for a second at the top of the"
                                        + " movement.\\n"
                                        + "3) Lower the handles back down slowly to the starting"
                                        + " position. Inhale as you perform this portion of the"
                                        + " movement.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Upright_Row_-_With_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Shrug",
                                "1) Stand up straight with your feet at shoulder width as you hold"
                                        + " a barbell with both hands in front of you using a pronated"
                                        + " grip (palms facing the thighs). Tip: Your hands should be a"
                                        + " little wider than shoulder width apart. You can use wrist"
                                        + " wraps for this exercise for a better grip. This will be"
                                        + " your starting position.\\n"
                                        + "2) Raise your shoulders up as far as you can go as you"
                                        + " breathe out and hold the contraction for a second. Tip:"
                                        + " Refrain from trying to lift the barbell by using your"
                                        + " biceps.\\n"
                                        + "3) Slowly return to the starting position as you breathe"
                                        + " in.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Barbell_Shrug"));
                addExercise(
                        new Exercise(
                                0,
                                "Barbell Shrug Behind The Back",
                                "1) Stand up straight with your feet at shoulder width as you hold"
                                        + " a barbell with both hands behind your back using a pronated"
                                        + " grip (palms facing back). Tip: Your hands should be a"
                                        + " little wider than shoulder width apart. You can use wrist"
                                        + " wraps for this exercise for better grip. This will be your"
                                        + " starting position.\\n"
                                        + "2) Raise your shoulders up as far as you can go as you"
                                        + " breathe out and hold the contraction for a second. Tip:"
                                        + " Refrain from trying to lift the barbell by using your"
                                        + " biceps. The arms should remain stretched out at all"
                                        + " times.\\n"
                                        + "3) Slowly return to the starting position as you breathe"
                                        + " in.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Barbell_Shrug_Behind_The_Back"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Shrugs",
                                "1) Grasp a cable bar attachment that is attached to a low pulley"
                                        + " with a shoulder width or slightly wider overhand (palms"
                                        + " facing down) grip.\\n"
                                        + "2) Stand erect close to the pulley with your arms extended"
                                        + " in front of you holding the bar. This will be your starting"
                                        + " position.\\n"
                                        + "3) Lift the bar by elevating the shoulders as high as"
                                        + " possible as you exhale. Hold the contraction at the top for"
                                        + " a second. Tip: The arms should remain extended at all"
                                        + " times. Refrain from using the biceps to help lift the bar."
                                        + " Only the shoulders should be moving up and down.\\n"
                                        + "4) Lower the bar back to the original position.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Cable_Shrugs"));
                addExercise(
                        new Exercise(
                                0,
                                "Calf-Machine Shoulder Shrug",
                                "1) Position yourself on the calf machine so that the shoulder pads"
                                        + " are above your shoulders. Your torso should be straight"
                                        + " with the arms extended normally by your side. This will be"
                                        + " your starting position.\\n"
                                        + "2) Raise your shoulders up towards your ears as you exhale"
                                        + " and hold the contraction for a full second.\\n"
                                        + "3) Slowly return to the starting position as you inhale.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Traps",
                                "Calf-Machine_Shoulder_Shrug"));
                addExercise(
                        new Exercise(
                                0,
                                "Lateral Bound",
                                "1) Assume a half squat position facing 90 degrees from your"
                                        + " direction of travel. This will be your starting"
                                        + " position.\\n"
                                        + "2) Allow your lead leg to do a countermovement inward as you"
                                        + " shift your weight to the outside leg.\\n"
                                        + "3) Immediately push off and extend, attempting to bound to"
                                        + " the side as far as possible.\\n"
                                        + "4) Upon landing, immediately push off in the opposite"
                                        + " direction, returning to your original start position.\\n"
                                        + "5) Continue back and forth for several repetitions.",
                                "plyometrics",
                                "Adductors",
                                "Lateral_Bound"));
                addExercise(
                        new Exercise(
                                0,
                                "Lateral Box Jump",
                                "1) Assume a comfortable standing position, with a short box"
                                        + " positioned next to you. This will be your starting"
                                        + " position.\\n"
                                        + "2) Quickly dip into a quarter squat to initiate the stretch"
                                        + " reflex, and immediately reverse direction to jump up and to"
                                        + " the side.\\n"
                                        + "3) Bring your knees high enough to ensure your feet have"
                                        + " good clearance over the box.\\n"
                                        + "4) Land on the center of the box, using your legs to absorb"
                                        + " the impact.\\n"
                                        + "5) Carefully jump down to the other side of the box, and"
                                        + " continue going back and forth for several repetitions.",
                                "plyometrics",
                                "Adductors",
                                "Lateral_Box_Jump"));
                addExercise(
                        new Exercise(
                                0,
                                "Lateral Cone Hops",
                                "1) Position a number of cones in a row several feet apart.\\n"
                                        + "2) Stand next to the end of the cones, facing 90 degrees to"
                                        + " the direction of travel. This will be your starting"
                                        + " position.\\n"
                                        + "3) Begin the jump by dipping with the knees to initiate a"
                                        + " stretch reflex, and immediately reverse direction to push"
                                        + " off the ground, jumping up and sideways over the cone.\\n"
                                        + "4) Use your legs to absorb impact upon landing, and rebound"
                                        + " into the next jump, continuing down the row of cones.",
                                "plyometrics",
                                "Adductors",
                                "Lateral_Cone_Hops"));
                addExercise(
                        new Exercise(
                                0,
                                "Band Hip Adductions",
                                "1) Anchor a band around a solid post or other object.\\n"
                                        + "2) Stand with your left side to the post, and put your right"
                                        + " foot through the band, getting it around the ankle.\\n"
                                        + "3) Stand up straight and hold onto the post if needed. This"
                                        + " will be your starting position.\\n"
                                        + "4) Keeping the knee straight, raise your right legs out to"
                                        + " the side as far as you can.\\n"
                                        + "5) Return to the starting position and repeat for the"
                                        + " desired rep count.\\n"
                                        + "6) Switch sides.",
                                "strength",
                                "Adductors",
                                "Band_Hip_Adductions"));
                addExercise(
                        new Exercise(
                                0,
                                "Groin and Back Stretch",
                                "1) Sit on the floor with your knees bent and feet together.\\n"
                                        + "2) Interlock your fingers behind your head. This will be"
                                        + " your starting position.\\n"
                                        + "3) Curl downwards, bringing your elbows to the inside of"
                                        + " your thighs. After a brief pause, return to the starting"
                                        + " position with your head up and your back straight. Repeat"
                                        + " for 10-20 repetitions.",
                                "stretching",
                                "Adductors",
                                "Groin_and_Back_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Groiners",
                                "1) Begin in a pushup position on the floor. This will be your"
                                        + " starting position.\\n"
                                        + "2) Using both legs, jump forward landing with your feet next"
                                        + " to your hands. Keep your head up as you do so.\\n"
                                        + "3) Return to the starting position and immediately repeat"
                                        + " the movement, continuing for 10-20 repetitions.",
                                "stretching",
                                "Adductors",
                                "Groiners"));
                addExercise(
                        new Exercise(
                                0,
                                "Side Lying Groin Stretch",
                                "1) Start off by lying on your right side and bend your right knee"
                                        + " in front of you to stabilize the torso.\\n"
                                        + "2) Rest your head on your right hand or shoulder. Lift your"
                                        + " left leg upward and hold it by the back of the knee"
                                        + " (easier) or the foot (harder).\\n"
                                        + "3) Pull your left knee in toward your left shoulder and"
                                        + " simultaneously press your foot or knee down to the floor."
                                        + " To intensify this stretch, straighten your left leg. Switch"
                                        + " sides.",
                                "stretching",
                                "Adductors",
                                "Side_Lying_Groin_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Thigh Adductor",
                                "1) To begin, sit down on the adductor machine and select a weight"
                                        + " you are comfortable with. When your legs are positioned"
                                        + " properly on the leg pads of the machine, grip the handles"
                                        + " on each side. Your entire upper body (from the waist up)"
                                        + " should be stationary. This is the starting position.\\n"
                                        + "2) Slowly press against the machine with your legs to move"
                                        + " them towards each other while exhaling.\\n"
                                        + "3) Feel the contraction for a second and begin to move your"
                                        + " legs back to the starting position while breathing in."
                                        + " Note: Remember to keep your upper body stationary and avoid"
                                        + " fast jerking motions in order to prevent any injuries from"
                                        + " occurring.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Adductors",
                                "Thigh_Adductor"));
                addExercise(
                        new Exercise(
                                0,
                                "Isometric Neck Exercise - Front And Back",
                                "1) With your head and neck in a neutral position (normal position"
                                        + " with head erect facing forward), place both of your hands"
                                        + " on the front side of your head.\\n"
                                        + "2) Now gently push forward as you contract the neck muscles"
                                        + " but resisting any movement of your head. Start with slow"
                                        + " tension and increase slowly. Keep breathing normally as you"
                                        + " execute this contraction.\\n"
                                        + "3) Hold for the recommended number of seconds.\\n"
                                        + "4) Now release the tension slowly.\\n"
                                        + "5) Rest for the recommended amount of time and repeat with"
                                        + " your hands placed on the back side of your head.",
                                "strength",
                                "Neck",
                                "Isometric_Neck_Exercise_-_Front_And_Back"));
                addExercise(
                        new Exercise(
                                0,
                                "Isometric Neck Exercise - Sides",
                                "1) With your head and neck in a neutral position (normal position"
                                        + " with head erect facing forward), place your left hand on"
                                        + " the left side of your head.\\n"
                                        + "2) Now gently push towards the left as you contract the left"
                                        + " neck muscles but resisting any movement of your head. Start"
                                        + " with slow tension and increase slowly. Keep breathing"
                                        + " normally as you execute this contraction.\\n"
                                        + "3) Hold for the recommended number of seconds.\\n"
                                        + "4) Now release the tension slowly.\\n"
                                        + "5) Rest for the recommended amount of time and repeat with"
                                        + " your right hand placed on the right side of your head.",
                                "strength",
                                "Neck",
                                "Isometric_Neck_Exercise_-_Sides"));
                addExercise(
                        new Exercise(
                                0,
                                "Side Neck Stretch",
                                "1) Start with your shoulders relaxed, gently tilt your head"
                                        + " towards your shoulder.\\n"
                                        + "2) Assist stretch with a gentle pull on the side of the"
                                        + " head.",
                                "stretching",
                                "Neck",
                                "Side_Neck_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Chin To Chest Stretch",
                                "1) Get into a seated position on the floor.\\n"
                                        + "2) Place both hands at the rear of your head, fingers"
                                        + " interlocked, thumbs pointing down and elbows pointing"
                                        + " straight ahead. Slowly pull your head down to your chest."
                                        + " Hold for 20-30 seconds.",
                                "stretching",
                                "Neck",
                                "Chin_To_Chest_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Lying Face Down Plate Neck Resistance",
                                "1) Lie face down with your whole body straight on a flat bench"
                                        + " while holding a weight plate behind your head. Tip: You"
                                        + " will need to position yourself so that your shoulders are"
                                        + " slightly above the end of a flat bench in order for the"
                                        + " upper chest, neck and face to be off the bench. This will"
                                        + " be your starting position.\\n"
                                        + "2) While keeping the plate secure on the back of your head"
                                        + " slowly lower your head (as in saying \"yes\") as you"
                                        + " breathe in.\\n"
                                        + "3) Raise your head back up to the starting position in a"
                                        + " semi-circular motion as you breathe out. Hold the"
                                        + " contraction for a second.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Neck",
                                "Lying_Face_Down_Plate_Neck_Resistance"));
                addExercise(
                        new Exercise(
                                0,
                                "Lying Face Up Plate Neck Resistance",
                                "1) Lie face up with your whole body straight on a flat bench while"
                                        + " holding a weight plate on top of your forehead. Tip: You"
                                        + " will need to position yourself so that your shoulders are"
                                        + " slightly above the end of a flat bench in order for the"
                                        + " traps, neck and head to be off the bench. This will be your"
                                        + " starting position.\\n"
                                        + "2) While keeping the plate secure on your forehead slowly"
                                        + " lower your head back in a semi-circular motion as you"
                                        + " breathe in.\\n"
                                        + "3) Raise your head back up to the starting position in a"
                                        + " semi-circular motion as you breathe out. Hold the"
                                        + " contraction for a second.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Neck",
                                "Lying_Face_Up_Plate_Neck_Resistance"));
                addExercise(
                        new Exercise(
                                0,
                                "Seated Head Harness Neck Resistance",
                                "1) Place a neck strap on the floor at the end of a flat bench."
                                        + " Once you have selected the weights, sit at the end of the"
                                        + " flat bench with your feet wider than shoulder width apart"
                                        + " from each other. Your toes should be pointed out.\\n"
                                        + "2) Slowly move your torso forward until it is almost"
                                        + " parallel with the floor. Using both hands, securely"
                                        + " position the neck strap around your head. Tip: Make sure"
                                        + " the weights are still lying on the floor to prevent any"
                                        + " strain on the neck. Now grab the weight with both hands"
                                        + " while elevating your torso back until it is almost"
                                        + " perpendicular to the floor. Note: Your head and torso needs"
                                        + " to be slightly tilted forward to perform this exercise.\\n"
                                        + "3) Now place both hands on top of your knees. This is the"
                                        + " starting position.\\n"
                                        + "4) Slowly lower your neck down until your chin touches the"
                                        + " upper part of your chest while breathing in.\\n"
                                        + "5) While exhaling, bring your neck back to the starting"
                                        + " position.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Neck",
                                "Seated_Head_Harness_Neck_Resistance"));
                addExercise(
                        new Exercise(
                                0,
                                "Neck-SMR",
                                "1) Using a muscle roller or a rolling pin, place the roller behind"
                                        + " your head and against your neck. Make sure that you do not"
                                        + " place the roller directly against the spine, but turned"
                                        + " slightly so that the roller is pressed against the muscles"
                                        + " to either side of the spine. This will be your starting"
                                        + " position.\\n"
                                        + "2) Starting at the top of your neck, slowly roll down the"
                                        + " muscles of your neck, pausing at points of tension for"
                                        + " 10-30 seconds.",
                                "stretching",
                                "Neck",
                                "Neck-SMR"));
                addExercise(
                        new Exercise(
                                0,
                                "Monster Walk",
                                "1) Place a band around both ankles and another around both knees."
                                        + " There should be enough tension that they are tight when"
                                        + " your feet are shoulder width apart.\\n"
                                        + "2) To begin, take short steps forward alternating your left"
                                        + " and right foot.\\n"
                                        + "3) After several steps, do just the opposite and walk"
                                        + " backward to where you started.",
                                "strength",
                                "Abductors",
                                "Monster_Walk"));
                addExercise(
                        new Exercise(
                                0,
                                "Hip Circles (prone)",
                                "1) Position yourself on your hands and knees on the ground."
                                        + " Maintaining good posture, raise one bent knee off of the"
                                        + " ground. This will be your starting position.\\n"
                                        + "2) Keeping the knee in a bent position, rotate the femur in"
                                        + " an arc, attempting to make a big circle with your knee.\\n"
                                        + "3) Perform this slowly for a number of repetitions, and"
                                        + " repeat on the other side.",
                                "stretching",
                                "Abductors",
                                "Hip_Circles_prone"));
                addExercise(
                        new Exercise(
                                0,
                                "Standing Hip Circles",
                                "1) Begin standing on one leg, holding to a vertical support.\\n"
                                        + "2) Raise the unsupported knee to 90 degrees. This will be"
                                        + " your starting position.\\n"
                                        + "3) Open the hip as far as possible, attempting to make a big"
                                        + " circle with your knee.\\n"
                                        + "4) Perform this movement slowly for a number of repetitions,"
                                        + " and repeat on the other side.",
                                "stretching",
                                "Abductors",
                                "Standing_Hip_Circles"));
                addExercise(
                        new Exercise(
                                0,
                                "Thigh Abductor",
                                "1) To begin, sit down on the abductor machine and select a weight"
                                        + " you are comfortable with. When your legs are positioned"
                                        + " properly, grip the handles on each side. Your entire upper"
                                        + " body (from the waist up) should be stationary. This is the"
                                        + " starting position.\\n"
                                        + "2) Slowly press against the machine with your legs to move"
                                        + " them away from each other while exhaling.\\n"
                                        + "3) Feel the contraction for a second and begin to move your"
                                        + " legs back to the starting position while breathing in."
                                        + " Note: Remember to keep your upper body stationary to"
                                        + " prevent any injuries from occurring.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abductors",
                                "Thigh_Abductor"));
                addExercise(
                        new Exercise(
                                0,
                                "Iliotibial Tract-SMR",
                                "1) Lay on your side, with the bottom leg placed onto a foam roller"
                                        + " between the hip and the knee. The other leg can be crossed"
                                        + " in front of you.\\n"
                                        + "2) Place as much of your weight as is tolerable onto your"
                                        + " bottom leg; there is no need to keep your bottom leg in"
                                        + " contact with the ground. Be sure to relax the muscles of"
                                        + " the leg you are stretching.\\n"
                                        + "3) Roll your leg over the foam from you hip to your knee,"
                                        + " pausing for 10-30 seconds at points of tension. Repeat with"
                                        + " the opposite leg.",
                                "stretching",
                                "Abductors",
                                "Iliotibial_Tract-SMR"));
                addExercise(
                        new Exercise(
                                0,
                                "IT Band and Glute Stretch",
                                "1) Loop a belt, rope, or band around one of your feet, and swing"
                                        + " that leg across your body to the opposite side, keeping the"
                                        + " leg extended as you lay on the ground. This will be your"
                                        + " starting position.\\n"
                                        + "2) Keeping your foot off of the floor, pull on the belt,"
                                        + " using the tension to pull the toes up. Hold for 10-20"
                                        + " seconds, and repeat on the other side.",
                                "stretching",
                                "Abductors",
                                "IT_Band_and_Glute_Stretch"));
                addExercise(
                        new Exercise(
                                0,
                                "Windmills",
                                "1) Lie on your back with your arms extended out to the sides and"
                                        + " your legs straight. This will be your starting position.\\n"
                                        + "2) Lift one leg and quickly cross it over your body,"
                                        + " attempting to touch the ground near the opposite hand.\\n"
                                        + "3) Return to the starting position, and repeat with the"
                                        + " opposite leg. Continue to alternate for 10-20 repetitions.",
                                "stretching",
                                "Abductors",
                                "Windmills"));
                addExercise(
                        new Exercise(
                                0,
                                "Lying Crossover",
                                "1) Lie on your back with your legs extended.\\n"
                                        + "2) Cross one leg over your body with the knee bent,"
                                        + " attempting to touch the knee to the ground. Your partner"
                                        + " should kneel beside you, holding your shoulder down with"
                                        + " one hand and controlling the crossed leg with the other."
                                        + " this will be your starting position.\\n"
                                        + "3) Attempt to raise the bent knee off of the ground as your"
                                        + " partner prevents any actual movement.\\n"
                                        + "4) After 10-20 seconds, relax the leg as your partner gently"
                                        + " presses the knee towards the floor. Repeat with the other"
                                        + " side.",
                                "stretching",
                                "Abductors",
                                "Lying_Crossover"));
                addExercise(
                        new Exercise(
                                0,
                                "Bodyweight Walking Lunge",
                                "1) Begin standing with your feet shoulder width apart and your"
                                        + " hands on your hips.\\n"
                                        + "2) Step forward with one leg, flexing the knees to drop your"
                                        + " hips. Descend until your rear knee nearly touches the"
                                        + " ground. Your posture should remain upright, and your front"
                                        + " knee should stay above the front foot.\\n"
                                        + "3) Drive through the heel of your lead foot and extend both"
                                        + " knees to raise yourself back up.\\n"
                                        + "4) Step forward with your rear foot, repeating the lunge on"
                                        + " the opposite leg.",
                                "strength",
                                "Quadriceps",
                                "Bodyweight_Walking_Lunge"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Deadlifts",
                                "1) Move the cables to the bottom of the towers and select an"
                                        + " appropriate weight. Stand directly in between the"
                                        + " uprights.\\n"
                                        + "2) To begin, squat down be flexing your hips and knees until"
                                        + " you can reach the handles.\\n"
                                        + "3) After grasping them, begin your ascent. Driving through"
                                        + " your heels extend your hips and knees keeping your hands"
                                        + " hanging at your side. Keep your head and chest up"
                                        + " throughout the movement.\\n"
                                        + "4) After reaching a full standing position, Return to the"
                                        + " starting position and repeat.",
                                "strength",
                                "Quadriceps",
                                "Cable_Deadlifts"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Internal Rotation",
                                "1) Sit next to a low pulley sideways (with legs stretched in front"
                                        + " of you or crossed) and grasp the single hand cable"
                                        + " attachment with the arm nearest to the cable. Tip: If you"
                                        + " can adjust the pulley's height, you can use a flat bench to"
                                        + " sit on instead.\\n"
                                        + "2) Position the elbow against your side with the elbow bent"
                                        + " at 90\u00b0 and the arm pointing towards the pulley. This"
                                        + " will be your starting position.\\n"
                                        + "3) Pull the single hand cable attachment toward your body by"
                                        + " internally rotating your shoulder until your forearm is"
                                        + " across your abs. You will be creating an imaginary"
                                        + " semi-circle. Tip: The forearm should be perpendicular to"
                                        + " your torso at all times.\\n"
                                        + "4) Slowly go back to the initial position.\\n"
                                        + "5) Repeat for the recommended amount of repetitions and then"
                                        + " repeat the movement with the next arm.",
                                "strength",
                                "Shoulders",
                                "Cable_Internal_Rotation"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Rope Rear-Delt Rows",
                                "1) Sit in the same position on a low pulley row station as you"
                                        + " would if you were doing seated cable rows for the back.\\n"
                                        + "2) Attach a rope to the pulley and grasp it with an overhand"
                                        + " grip. Your arms should be extended and parallel to the"
                                        + " floor with the elbows flared out.\\n"
                                        + "3) Keep your lower back upright and slide your hips back so"
                                        + " that your knees are slightly bent. This will be your"
                                        + " starting position.\\n"
                                        + "4) Pull the cable attachment towards your upper chest, just"
                                        + " below the neck, as you keep your elbows up and out to the"
                                        + " sides. Continue this motion as you exhale until the elbows"
                                        + " travel slightly behind the back. Tip: Keep your upper arms"
                                        + " horizontal, perpendicular to the torso and parallel to the"
                                        + " floor throughout the motion.\\n"
                                        + "5) Go back to the initial position where the arms are"
                                        + " extended and the shoulders are stretched forward. Inhale as"
                                        + " you perform this portion of the movement.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Cable_Rope_Rear-Delt_Rows"));
                addExercise(
                        new Exercise(
                                0,
                                "Cable Shoulder Press",
                                "1) Move the cables to the bottom of the towers and select an"
                                        + " appropriate weight.\\n"
                                        + "2) Stand directly in between the uprights. Grasp the cables"
                                        + " and hold them at shoulder height, palms facing forward."
                                        + " This will be your starting position.\\n"
                                        + "3) Keeping your head and chest up, extend through the elbow"
                                        + " to press the handles directly over head.\\n"
                                        + "4) After pausing at the top, return to the starting position"
                                        + " and repeat.",
                                "strength",
                                "Shoulders",
                                "Cable_Shoulder_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Chair Squat",
                                "1) To begin, first set the bar to a position that best matches"
                                        + " your height. Once the bar is loaded, step under it and"
                                        + " position it across the back of your shoulders.\\n"
                                        + "2) Take the bar with your hands facing forward, unlock it"
                                        + " and lift it off the rack by extending your legs.\\n"
                                        + "3) Move your feet forward about 18 inches in front of the"
                                        + " bar. Position your legs using a shoulder width stance with"
                                        + " the toes slightly pointed out. Look forward at all times"
                                        + " and maintain a neutral or slightly arched spine. This will"
                                        + " be your starting position.\\n"
                                        + "4) Slowly lower the bar by bending the knees as you maintain"
                                        + " a straight posture with the head up. Continue down until"
                                        + " the angle between the upper and lower leg breaks 90"
                                        + " degrees.\\n"
                                        + "5) Begin to raise the bar as you exhale by pushing the floor"
                                        + " with the heels of your feet, extending the knees and"
                                        + " returning to the starting position.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Quadriceps",
                                "Chair_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Chest Push with Run Release",
                                "1) Begin in an athletic stance with the knees bent, hips back, and"
                                        + " back flat. Hold the medicine ball near your legs. This will"
                                        + " be your starting position.\\n"
                                        + "2) While taking your first step draw the medicine ball into"
                                        + " your chest.\\n"
                                        + "3) As you take the second step, explosively push the ball"
                                        + " forward, immediately sprinting for 10 yards after the"
                                        + " release. If you are really fast, you can catch your own"
                                        + " pass!",
                                "plyometrics",
                                "Chest",
                                "Chest_Push_with_Run_Release"));
                addExercise(
                        new Exercise(
                                0,
                                "Cross Over - With Bands",
                                "1) Secure an exercise band around a stationary post.\\n"
                                        + "2) While facing away from the post, grab the handles on both"
                                        + " ends of the band and step forward enough to create tension"
                                        + " on the band.\\n"
                                        + "3) Raise your arms to the sides, parallel to the floor,"
                                        + " perpendicular to your torso (your torso and the arms should"
                                        + " resemble the letter \"T\") and with the palms facing"
                                        + " forward. Have them extended with a slight bend at the"
                                        + " elbows. This will be your starting position.\\n"
                                        + "4) While keeping your arms straight, bring them across your"
                                        + " chest in a semicircular motion to the front as you exhale"
                                        + " and flex your pecs. Hold the contraction for a second.\\n"
                                        + "5) Slowly return to the starting position as you inhale.\\n"
                                        + "6) Perform for the recommended amount of repetitions.",
                                "strength",
                                "Chest",
                                "Cross_Over_-_With_Bands"));
                addExercise(
                        new Exercise(
                                0,
                                "Cross-Body Crunch",
                                "1) Lie flat on your back and bend your knees about 60 degrees.\\n"
                                        + "2) Keep your feet flat on the floor and place your hands"
                                        + " loosely behind your head. This will be your starting"
                                        + " position.\\n"
                                        + "3) Now curl up and bring your right elbow and shoulder"
                                        + " across your body while bring your left knee in toward your"
                                        + " left shoulder at the same time. Reach with your elbow and"
                                        + " try to touch your knee. Exhale as you perform this"
                                        + " movement. Tip: Try to bring your shoulder up towards your"
                                        + " knee rather than just your elbow and remember that the key"
                                        + " is to contract the abs as you perform the movement; not"
                                        + " just to move the elbow.\\n"
                                        + "4) Now go back down to the starting position as you inhale"
                                        + " and repeat with the left elbow and the right knee.\\n"
                                        + "5) Continue alternating in this manner until all prescribed"
                                        + " repetitions are done.",
                                "strength",
                                "Abdominals",
                                "Cross-Body_Crunch"));
                addExercise(
                        new Exercise(
                                0,
                                "Dead Bug",
                                "1) Begin lying on your back with your hands extended above you"
                                        + " toward the ceiling.\\n"
                                        + "2) Bring your feet, knees, and hips up to 90 degrees.\\n"
                                        + "3) Exhale hard to bring your ribcage down and flatten your"
                                        + " back onto the floor, rotating your pelvis up and squeezing"
                                        + " your glutes. Hold this position throughout the movement."
                                        + " This will be your starting position.\\n"
                                        + "4) Initiate the exercise by extending one leg, straightening"
                                        + " the knee and hip to bring the leg just above the ground.\\n"
                                        + "5) Maintain the position of your lumbar and pelvis as you"
                                        + " perform the movement, as your back is going to want to"
                                        + " arch.\\n"
                                        + "6) Stay tight and return the working leg to the starting"
                                        + " position.\\n"
                                        + "7) Repeat on the opposite side, alternating until the set is"
                                        + " complete.",
                                "strength",
                                "Abdominals",
                                "Dead_Bug"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Barbell Bench Press",
                                "1) Secure your legs at the end of the decline bench and slowly lay"
                                        + " down on the bench.\\n"
                                        + "2) Using a medium width grip (a grip that creates a"
                                        + " 90-degree angle in the middle of the movement between the"
                                        + " forearms and the upper arms), lift the bar from the rack"
                                        + " and hold it straight over you with your arms locked. The"
                                        + " arms should be perpendicular to the floor. This will be"
                                        + " your starting position. Tip: In order to protect your"
                                        + " rotator cuff, it is best if you have a spotter help you"
                                        + " lift the barbell off the rack.\\n"
                                        + "3) As you breathe in, come down slowly until you feel the"
                                        + " bar on your lower chest.\\n"
                                        + "4) After a second pause, bring the bar back to the starting"
                                        + " position as you breathe out and push the bar using your"
                                        + " chest muscles. Lock your arms and squeeze your chest in the"
                                        + " contracted position, hold for a second and then start"
                                        + " coming down slowly again. Tip: It should take at least"
                                        + " twice as long to go down than to come up).\\n"
                                        + "5) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "6) When you are done, place the bar back in the rack.",
                                "strength",
                                "Chest",
                                "Decline_Barbell_Bench_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Dumbbell Bench Press",
                                "1) Secure your legs at the end of the decline bench and lie down"
                                        + " with a dumbbell on each hand on top of your thighs. The"
                                        + " palms of your hand will be facing each other.\\n"
                                        + "2) Once you are laying down, move the dumbbells in front of"
                                        + " you at shoulder width.\\n"
                                        + "3) Once at shoulder width, rotate your wrists forward so"
                                        + " that the palms of your hands are facing away from you. This"
                                        + " will be your starting position.\\n"
                                        + "4) Bring down the weights slowly to your side as you breathe"
                                        + " out. Keep full control of the dumbbells at all times. Tip:"
                                        + " Throughout the motion, the forearms should always be"
                                        + " perpendicular to the floor.\\n"
                                        + "5) As you breathe out, push the dumbbells up using your"
                                        + " pectoral muscles. Lock your arms in the contracted"
                                        + " position, squeeze your chest, hold for a second and then"
                                        + " start coming down slowly. Tip: It should take at least"
                                        + " twice as long to go down than to come up..\\n"
                                        + "6) Repeat the movement for the prescribed amount of"
                                        + " repetitions of your training program.",
                                "strength",
                                "Chest",
                                "Decline_Dumbbell_Bench_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Dumbbell Flyes",
                                "1) Secure your legs at the end of the decline bench and lie down"
                                        + " with a dumbbell on each hand on top of your thighs. The"
                                        + " palms of your hand will be facing each other.\\n"
                                        + "2) Once you are laying down, move the dumbbells in front of"
                                        + " you at shoulder width. The palms of the hands should be"
                                        + " facing each other and the arms should be perpendicular to"
                                        + " the floor and fully extended. This will be your starting"
                                        + " position.\\n"
                                        + "3) With a slight bend on your elbows in order to prevent"
                                        + " stress at the biceps tendon, lower your arms out at both"
                                        + " sides in a wide arc until you feel a stretch on your chest."
                                        + " Breathe in as you perform this portion of the movement."
                                        + " Tip: Keep in mind that throughout the movement, the arms"
                                        + " should remain stationary; the movement should only occur at"
                                        + " the shoulder joint.\\n"
                                        + "4) Return your arms back to the starting position as you"
                                        + " squeeze your chest muscles and breathe out. Tip: Make sure"
                                        + " to use the same arc of motion used to lower the weights.\\n"
                                        + "5) Hold for a second at the contracted position and repeat"
                                        + " the movement for the prescribed amount of repetitions.",
                                "strength",
                                "Chest",
                                "Decline_Dumbbell_Flyes"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Oblique Crunch",
                                "1) Secure your legs at the end of the decline bench and slowly lay"
                                        + " down on the bench.\\n"
                                        + "2) Raise your upper body off the bench until your torso is"
                                        + " about 35-45 degrees if measured from the floor.\\n"
                                        + "3) Put one hand beside your head and the other on your"
                                        + " thigh. This will be your starting position.\\n"
                                        + "4) Raise your upper body slowly from the starting position"
                                        + " while turning your torso to the left. Continue crunching up"
                                        + " as you exhale until your right elbow touches your left"
                                        + " knee. Hold this contracted position for a second. Tip:"
                                        + " Focus on keeping your abs tight and keeping the movement"
                                        + " slow and controlled.\\n"
                                        + "5) Lower your body back down slowly to the starting position"
                                        + " as you inhale.\\n"
                                        + "6) After completing one set on the right for the recommended"
                                        + " amount of repetitions, switch to your left side. Tip: Focus"
                                        + " on really twisting your torso and feeling the contraction"
                                        + " when you are in the up position.",
                                "strength",
                                "Abdominals",
                                "Decline_Oblique_Crunch"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Push-Up",
                                "1) Lie on the floor face down and place your hands about 36 inches"
                                        + " apart while holding your torso up at arms length. Move your"
                                        + " feet up to a box or bench. This will be your starting"
                                        + " position.\\n"
                                        + "2) Next, lower yourself downward until your chest almost"
                                        + " touches the floor as you inhale.\\n"
                                        + "3) Now breathe out and press your upper body back up to the"
                                        + " starting position while squeezing your chest.\\n"
                                        + "4) After a brief pause at the top contracted position, you"
                                        + " can begin to lower yourself downward again for as many"
                                        + " repetitions as needed.",
                                "strength",
                                "Chest",
                                "Decline_Push-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Reverse Crunch",
                                "1) Lie on your back on a decline bench and hold on to the top of"
                                        + " the bench with both hands. Don't let your body slip down"
                                        + " from this position.\\n"
                                        + "2) Hold your legs parallel to the floor using your abs to"
                                        + " hold them there while keeping your knees and feet together."
                                        + " Tip: Your legs should be fully extended with a slight bend"
                                        + " on the knee. This will be your starting position.\\n"
                                        + "3) While exhaling, move your legs towards the torso as you"
                                        + " roll your pelvis backwards and you raise your hips off the"
                                        + " bench. At the end of this movement your knees will be"
                                        + " touching your chest.\\n"
                                        + "4) Hold the contraction for a second and move your legs back"
                                        + " to the starting position while inhaling.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Decline_Reverse_Crunch"));
                addExercise(
                        new Exercise(
                                0,
                                "Decline Smith Press",
                                "1) Place a decline bench underneath the Smith machine. Now place"
                                        + " the barbell at a height that you can reach when lying down"
                                        + " and your arms are almost fully extended. Using a pronated"
                                        + " grip that is wider than shoulder width, unlock the bar from"
                                        + " the rack and hold it straight over you with your arms"
                                        + " extended. This will be your starting position.\\n"
                                        + "2) As you inhale, lower the bar under control by allowing"
                                        + " the elbows to flex, lightly contacting the torso.\\n"
                                        + "3) After a brief pause, bring the bar back to the starting"
                                        + " position by extending the elbows, exhaling as you do so.\\n"
                                        + "4) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "5) When the set is complete, lock the bar back in the rack.",
                                "strength",
                                "Chest",
                                "Decline_Smith_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Depth Jump Leap",
                                "1) For this drill you will need two boxes or benches, one 12 to 16"
                                        + " inches high and the other 22 to 26 inches high.\\n"
                                        + "2) Stand on one of the two boxes with arms at the sides;"
                                        + " feet should be together and slightly off the edge as in the"
                                        + " depth jump. Place the other box approximately two or three"
                                        + " feet in front of and facing the performer.\\n"
                                        + "3) Begin by dropping off the initial box, landing and"
                                        + " simultaneously taking off with both feet.\\n"
                                        + "4) Rebound by driving upward and outward as intensely as"
                                        + " possible, using the arms and full extension of the body to"
                                        + " jump onto the higher box. Again, allow the legs to absorb"
                                        + " the impact.",
                                "plyometrics",
                                "Quadriceps",
                                "Depth_Jump_Leap"));
                addExercise(
                        new Exercise(
                                0,
                                "Double Leg Butt Kick",
                                "1) Begin standing with your knees slightly bent.\\n"
                                        + "2) Quickly squat a short distance, flexing the hips and"
                                        + " knees, and immediately extend to jump for maximum vertical"
                                        + " height.\\n"
                                        + "3) As you go up, tuck your heels by flexing the knees,"
                                        + " attempting to touch the buttocks.\\n"
                                        + "4) Finish the motion by landing with the knees only"
                                        + " partially bent, using your legs to absorb the impact.",
                                "plyometrics",
                                "Quadriceps",
                                "Double_Leg_Butt_Kick"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Bench Press",
                                "1) Lie down on a flat bench with a dumbbell in each hand resting"
                                        + " on top of your thighs. The palms of your hands will be"
                                        + " facing each other.\\n"
                                        + "2) Then, using your thighs to help raise the dumbbells up,"
                                        + " lift the dumbbells one at a time so that you can hold them"
                                        + " in front of you at shoulder width.\\n"
                                        + "3) Once at shoulder width, rotate your wrists forward so"
                                        + " that the palms of your hands are facing away from you. The"
                                        + " dumbbells should be just to the sides of your chest, with"
                                        + " your upper arm and forearm creating a 90 degree angle. Be"
                                        + " sure to maintain full control of the dumbbells at all"
                                        + " times. This will be your starting position.\\n"
                                        + "4) Then, as you breathe out, use your chest to push the"
                                        + " dumbbells up. Lock your arms at the top of the lift and"
                                        + " squeeze your chest, hold for a second and then begin coming"
                                        + " down slowly. Tip: Ideally, lowering the weight should take"
                                        + " about twice as long as raising it.\\n"
                                        + "5) Repeat the movement for the prescribed amount of"
                                        + " repetitions of your training program.",
                                "strength",
                                "Chest",
                                "Dumbbell_Bench_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Bench Press with Neutral Grip",
                                "1) Take a dumbbell in each hand and lay back onto a flat bench."
                                        + " Your feet should be flat on the floor and your shoulder"
                                        + " blades retracted.\\n"
                                        + "2) Maintaining a neutral grip, palms facing each other,"
                                        + " begin with your arms extended directly above you,"
                                        + " perpendicular to the floor. This will be your starting"
                                        + " position.\\n"
                                        + "3) Begin the movement by flexing the elbow, lowering the"
                                        + " upper arms to the side. Descend until the dumbbells are to"
                                        + " your torso.\\n"
                                        + "4) Pause, then extend the elbow and return to the starting"
                                        + " position.",
                                "strength",
                                "Chest",
                                "Dumbbell_Bench_Press_with_Neutral_Grip"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Lunges",
                                "1) Stand with your torso upright holding two dumbbells in your"
                                        + " hands by your sides. This will be your starting"
                                        + " position.\\n"
                                        + "2) Step forward with your right leg around 2 feet or so from"
                                        + " the foot being left stationary behind and lower your upper"
                                        + " body down, while keeping the torso upright and maintaining"
                                        + " balance. Inhale as you go down. Note: As in the other"
                                        + " exercises, do not allow your knee to go forward beyond your"
                                        + " toes as you come down, as this will put undue stress on the"
                                        + " knee joint. Make sure that you keep your front shin"
                                        + " perpendicular to the ground.\\n"
                                        + "3) Using mainly the heel of your foot, push up and go back"
                                        + " to the starting position as you exhale.\\n"
                                        + "4) Repeat the movement for the recommended amount of"
                                        + " repetitions and then perform with the left leg.",
                                "strength",
                                "Quadriceps",
                                "Dumbbell_Lunges"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Raise",
                                "1) Grab a dumbbell in each arm and stand up straight with your"
                                        + " arms extended by your sides with a slight bend at the"
                                        + " elbows and your back straight. This will be your starting"
                                        + " position. Tip: The dumbbell should be next to your thighs"
                                        + " with the palm of your hands facing back.\\n"
                                        + "2) Use your side shoulders to lift the dumbbells as you"
                                        + " exhale. The dumbbells should be to the side of the body as"
                                        + " you move them up. Continue to lift it until the dumbbells"
                                        + " are nearly in line with your chin. Tip: Your elbows should"
                                        + " drive the motion. As you lift the dumbbell, your elbow"
                                        + " should always be higher than your forearm. Also, keep your"
                                        + " torso stationary and pause for a second at the top of the"
                                        + " movement.\\n"
                                        + "3) Lower the dumbbells back down slowly to the starting"
                                        + " position. Inhale as you perform this portion of the"
                                        + " movement.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Dumbbell_Raise"));
                addExercise(
                        new Exercise(
                                0,
                                "Dumbbell Squat",
                                "1) Stand up straight while holding a dumbbell on each hand (palms"
                                        + " facing the side of your legs).\\n"
                                        + "2) Position your legs using a shoulder width medium stance"
                                        + " with the toes slightly pointed out. Keep your head up at"
                                        + " all times as looking down will get you off balance and also"
                                        + " maintain a straight back. This will be your starting"
                                        + " position. Note: For the purposes of this discussion we will"
                                        + " use the medium stance described above which targets overall"
                                        + " development; however you can choose any of the three"
                                        + " stances discussed in the foot stances section.\\n"
                                        + "3) Begin to slowly lower your torso by bending the knees as"
                                        + " you maintain a straight posture with the head up. Continue"
                                        + " down until your thighs are parallel to the floor. Tip: If"
                                        + " you performed the exercise correctly, the front of the"
                                        + " knees should make an imaginary straight line with the toes"
                                        + " that is perpendicular to the front. If your knees are past"
                                        + " that imaginary line (if they are past your toes) then you"
                                        + " are placing undue stress on the knee and the exercise has"
                                        + " been performed incorrectly.\\n"
                                        + "4) Begin to raise your torso as you exhale by pushing the"
                                        + " floor with the heel of your foot mainly as you straighten"
                                        + " the legs again and go back to the starting position.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Quadriceps",
                                "Dumbbell_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Elbow to Knee",
                                "1) Lie on the floor, crossing your right leg across your bent left"
                                        + " knee. Clasp your hands behind your head, beginning with"
                                        + " your shoulder blades on the ground. This will be your"
                                        + " starting position.\\n"
                                        + "2) Perform the motion by flexing the spine and rotating your"
                                        + " torso to bring the left elbow to the right knee.\\n"
                                        + "3) Return to the starting position and repeat the movement"
                                        + " for the desired number of repetitions before switching"
                                        + " sides.",
                                "strength",
                                "Abdominals",
                                "Elbow_to_Knee"));
                addExercise(
                        new Exercise(
                                0,
                                "Exercise Ball Pull-In",
                                "1) Place an exercise ball nearby and lay on the floor in front of"
                                        + " it with your hands on the floor shoulder width apart in a"
                                        + " push-up position.\\n"
                                        + "2) Now place your lower shins on top of an exercise ball."
                                        + " Tip: At this point your legs should be fully extended with"
                                        + " the shins on top of the ball and the upper body should be"
                                        + " in a push-up type of position being supported by your two"
                                        + " extended arms in front of you. This will be your starting"
                                        + " position.\\n"
                                        + "3) While keeping your back completely straight and the upper"
                                        + " body stationary, pull your knees in towards your chest as"
                                        + " you exhale, allowing the ball to roll forward under your"
                                        + " ankles. Squeeze your abs and hold that position for a"
                                        + " second.\\n"
                                        + "4) Now slowly straighten your legs, rolling the ball back to"
                                        + " the starting position as you inhale.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Exercise_Ball_Pull-In"));
                addExercise(
                        new Exercise(
                                0,
                                "Extended Range One-Arm Kettlebell Floor Press",
                                "1) Lie on the floor and position a kettlebell for one arm to"
                                        + " press. The kettlebell should be held by the handle. The leg"
                                        + " on the same side that you are pressing should be bent, with"
                                        + " the knee crossing over the midline of the body.\\n"
                                        + "2) Press the kettlebell by extending the elbow and adducting"
                                        + " the arm, pressing it above your body. Return to the"
                                        + " starting position.",
                                "strength",
                                "Chest",
                                "Extended_Range_One-Arm_Kettlebell_Floor_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "External Rotation with Band",
                                "1) Choke the band around a post. The band should be at the same"
                                        + " height as your elbow. Stand with your left side to the band"
                                        + " a couple of feet away.\\n"
                                        + "2) Grasp the end of the band with your right hand, and keep"
                                        + " your elbow pressed firmly to your side. We recommend you"
                                        + " hold a pad or foam roll in place with your elbow to keep it"
                                        + " firmly in position.\\n"
                                        + "3) With your upper arm in position, your elbow should be"
                                        + " flexed to 90 degrees with your hand reaching across the"
                                        + " front of your torso. This will be your starting"
                                        + " position.\\n"
                                        + "4) Execute the movement by rotating your arm in a backhand"
                                        + " motion, keeping your elbow in place.\\n"
                                        + "5) Continue as far as you are able, pause, and then return"
                                        + " to the starting position.",
                                "strength",
                                "Shoulders",
                                "External_Rotation_with_Band"));
                addExercise(
                        new Exercise(
                                0,
                                "Fast Skipping",
                                "1) Start in a relaxed position with one leg slightly forward. This"
                                        + " will be your starting position.\\n"
                                        + "2) Skip by executing a step-hop pattern of right-right-step"
                                        + " to left-left-step, and so on, alternating back and"
                                        + " forth.\\n"
                                        + "3) Perform fast skips by maintaining close contact with the"
                                        + " ground and reduce air time, moving as quickly as possible.",
                                "plyometrics",
                                "Quadriceps",
                                "Fast_Skipping"));
                addExercise(
                        new Exercise(
                                0,
                                "Flat Bench Leg Pull-In",
                                "1) Lie on an exercise mat or a flat bench with your legs off the"
                                        + " end.\\n"
                                        + "2) Place your hands either under your glutes with your palms"
                                        + " down or by the sides holding on to the bench (or with palms"
                                        + " down by the side on an exercise mat). Also extend your legs"
                                        + " straight out. This will be your starting position.\\n"
                                        + "3) Bend your knees and pull your upper thighs into your"
                                        + " midsection as you breathe out. Continue this movement until"
                                        + " your knees are near your chest. Hold the contracted"
                                        + " position for a second.\\n"
                                        + "4) As you breathe in, slowly return to the starting"
                                        + " position.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Flat_Bench_Leg_Pull-In"));
                addExercise(
                        new Exercise(
                                0,
                                "Front Cone Hops (or hurdle hops)",
                                "1) Set up a row of cones or other small barriers, placing them a"
                                        + " few feet apart.\\n"
                                        + "2) Stand in front of the first cone with your feet shoulder"
                                        + " width apart. This will be your starting position.\\n"
                                        + "3) Begin by jumping with both feet over the first cone,"
                                        + " swinging both arms as you jump.\\n"
                                        + "4) Absorb the impact of landing by bending the knees,"
                                        + " rebounding out of the first leap by jumping over the next"
                                        + " cone.\\n"
                                        + "5) Continue until you have jumped over all of the cones.",
                                "plyometrics",
                                "Quadriceps",
                                "Front_Cone_Hops_or_hurdle_hops"));
                addExercise(
                        new Exercise(
                                0,
                                "Front Raise And Pullover",
                                "1) Lie on a flat bench while holding a barbell using a palms down"
                                        + " grip that is about 15 inches apart.\\n"
                                        + "2) Place the bar on your upper thighs, extend your arms and"
                                        + " lock them while keeping a slight bend on the elbows. This"
                                        + " will be your starting position.\\n"
                                        + "3) Now raise the weight using a semicircular motion and"
                                        + " keeping your arms straight as you inhale. Continue the same"
                                        + " movement until the bar is on the other side above your head"
                                        + " . (Tip: the bar will travel approximately 180-degrees). At"
                                        + " this point your arms should be parallel to the floor with"
                                        + " the palms of your hands facing the ceiling.\\n"
                                        + "4) Now return the barbell to the starting position by"
                                        + " reversing the motion as you exhale.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Chest",
                                "Front_Raise_And_Pullover"));
                addExercise(
                        new Exercise(
                                0,
                                "Goblet Squat",
                                "1) Stand holding a light kettlebell by the horns close to your"
                                        + " chest. This will be your starting position.\\n"
                                        + "2) Squat down between your legs until your hamstrings are on"
                                        + " your calves. Keep your chest and head up and your back"
                                        + " straight.\\n"
                                        + "3) At the bottom position, pause and use your elbows to push"
                                        + " your knees out. Return to the starting position, and repeat"
                                        + " for 10-20 repetitions.",
                                "strength",
                                "Quadriceps",
                                "Goblet_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Hack Squat",
                                "1) Place the back of your torso against the back pad of the"
                                        + " machine and hook your shoulders under the shoulder pads"
                                        + " provided.\\n"
                                        + "2) Position your legs in the platform using a shoulder width"
                                        + " medium stance with the toes slightly pointed out. Tip: Keep"
                                        + " your head up at all times and also maintain the back on the"
                                        + " pad at all times.\\n"
                                        + "3) Place your arms on the side handles of the machine and"
                                        + " disengage the safety bars (which on most designs is done by"
                                        + " moving the side handles from a facing front position to a"
                                        + " diagonal position).\\n"
                                        + "4) Now straighten your legs without locking the knees. This"
                                        + " will be your starting position. (Note: For the purposes of"
                                        + " this discussion we will use the medium stance described"
                                        + " above which targets overall development; however you can"
                                        + " choose any of the three stances described in the foot"
                                        + " positioning section).\\n"
                                        + "5) Begin to slowly lower the unit by bending the knees as"
                                        + " you maintain a straight posture with the head up (back on"
                                        + " the pad at all times). Continue down until the angle"
                                        + " between the upper leg and the calves becomes slightly less"
                                        + " than 90-degrees (which is the point in which the upper legs"
                                        + " are below parallel to the floor). Inhale as you perform"
                                        + " this portion of the movement. Tip: If you performed the"
                                        + " exercise correctly, the front of the knees should make an"
                                        + " imaginary straight line with the toes that is perpendicular"
                                        + " to the front. If your knees are past that imaginary line"
                                        + " (if they are past your toes) then you are placing undue"
                                        + " stress on the knee and the exercise has been performed"
                                        + " incorrectly.\\n"
                                        + "6) Begin to raise the unit as you exhale by pushing the"
                                        + " floor with mainly with the heel of your foot as you"
                                        + " straighten the legs again and go back to the starting"
                                        + " position.\\n"
                                        + "7) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Quadriceps",
                                "Hack_Squat"));
                addExercise(
                        new Exercise(
                                0,
                                "Hammer Grip Incline DB Bench Press",
                                "1) Lie back on an incline bench with a dumbbell on each hand on"
                                        + " top of your thighs. The palms of your hand will be facing"
                                        + " each other.\\n"
                                        + "2) By using your thighs to help you get the dumbbells up,"
                                        + " clean the dumbbells one arm at a time so that you can hold"
                                        + " them at shoulder width.\\n"
                                        + "3) Once at shoulder width, keep the palms of your hands with"
                                        + " a neutral grip (palms facing each other). Keep your elbows"
                                        + " flared out with the upper arms in line with the shoulders"
                                        + " (perpendicular to the torso) and the elbows bent creating a"
                                        + " 90-degree angle between the upper arm and the forearm. This"
                                        + " will be your starting position.\\n"
                                        + "4) Now bring down the weights slowly to your side as you"
                                        + " breathe in. Keep full control of the dumbbells at all"
                                        + " times.\\n"
                                        + "5) As you breathe out, push the dumbbells up using your"
                                        + " pectoral muscles. Lock your arms in the contracted"
                                        + " position, hold for a second and then start coming down"
                                        + " slowly. Tip: It should take at least twice as long to go"
                                        + " down than to come up.\\n"
                                        + "6) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "7) When you are done, place the dumbbells back in your"
                                        + " thighs and then on the floor. This is the safest manner to"
                                        + " dispose of the dumbbells.",
                                "strength",
                                "Chest",
                                "Hammer_Grip_Incline_DB_Bench_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Heavy Bag Thrust",
                                "1) Utilize a heavy bag for this exercise. Assume an upright stance"
                                        + " next to the bag, with your feet staggered, fairly wide"
                                        + " apart. Place your hand on the bag at about chest height."
                                        + " This will be your starting position.\\n"
                                        + "2) Begin by twisting at the waist, pushing the bag forward"
                                        + " as hard as possible. Perform this move quickly, pushing the"
                                        + " bag away from your body.\\n"
                                        + "3) Receive the bag as it swings back by reversing these"
                                        + " steps.",
                                "plyometrics",
                                "Chest",
                                "Heavy_Bag_Thrust"));
                addExercise(
                        new Exercise(
                                0,
                                "Hip Flexion with Band",
                                "1) Secure one end of the band to the lower portion of a post and"
                                        + " attach the other to one ankle.\\n"
                                        + "2) Face away from the attachment point of the band.\\n"
                                        + "3) Keeping your head and your chest up, raise your knee up"
                                        + " to 90 degrees and pause.\\n"
                                        + "4) Return the leg to the starting position.",
                                "strength",
                                "Quadriceps",
                                "Hip_Flexion_with_Band"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Cable Chest Press",
                                "1) Adjust the weight to an appropriate amount and be seated,"
                                        + " grasping the handles. Your upper arms should be about 45"
                                        + " degrees to the body, with your head and chest up. The"
                                        + " elbows should be bent to about 90 degrees. This will be"
                                        + " your starting position.\\n"
                                        + "2) Begin by extending through the elbow, pressing the"
                                        + " handles together straight in front of you. Keep your"
                                        + " shoulder blades retracted as you execute the movement.\\n"
                                        + "3) After pausing at full extension, return to the starting"
                                        + " position, keeping tension on the cables.",
                                "strength",
                                "Chest",
                                "Incline_Cable_Chest_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Dumbbell Bench With Palms Facing In",
                                "1) Lie back on an incline bench with a dumbbell on each hand on"
                                        + " top of your thighs. The palms of your hand will be facing"
                                        + " each other.\\n"
                                        + "2) By using your thighs to help you get the dumbbells up,"
                                        + " clean the dumbbells one arm at a time so that you can hold"
                                        + " them at shoulder width.\\n"
                                        + "3) Once at shoulder width, keep the palms of your hands with"
                                        + " a neutral grip (palms facing each other). Keep your elbows"
                                        + " flared out with the upper arms in line with the shoulders"
                                        + " (perpendicular to the torso) and the elbows bent creating a"
                                        + " 90-degree angle between the upper arm and the forearm. This"
                                        + " will be your starting position.\\n"
                                        + "4) Now bring down the weights slowly to your side as you"
                                        + " breathe in. Keep full control of the dumbbells at all"
                                        + " times.\\n"
                                        + "5) As you breathe out, push the dumbbells up using your"
                                        + " pectoral muscles. Lock your arms in the contracted"
                                        + " position, hold for a second and then start coming down"
                                        + " slowly. Tip: It should take at least twice as long to go"
                                        + " down than to come up.\\n"
                                        + "6) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "7) When you are done, place the dumbbells back in your"
                                        + " thighs and then on the floor. This is the safest manner to"
                                        + " dispose of the dumbbells.",
                                "strength",
                                "Chest",
                                "Incline_Dumbbell_Bench_With_Palms_Facing_In"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Dumbbell Flyes",
                                "1) Hold a dumbbell on each hand and lie on an incline bench that"
                                        + " is set to an incline angle of no more than 30 degrees.\\n"
                                        + "2) Extend your arms above you with a slight bend at the"
                                        + " elbows.\\n"
                                        + "3) Now rotate the wrists so that the palms of your hands are"
                                        + " facing you. Tip: The pinky fingers should be next to each"
                                        + " other. This will be your starting position.\\n"
                                        + "4) As you breathe in, start to slowly lower the arms to the"
                                        + " side while keeping the arms extended and while rotating the"
                                        + " wrists until the palms of the hand are facing each other."
                                        + " Tip: At the end of the movement the arms will be by your"
                                        + " side with the palms facing the ceiling.\\n"
                                        + "5) As you exhale start to bring the dumbbells back up to the"
                                        + " starting position by reversing the motion and rotating the"
                                        + " hands so that the pinky fingers are next to each other"
                                        + " again. Tip: Keep in mind that the movement will only happen"
                                        + " at the shoulder joint and at the wrist. There is no motion"
                                        + " that happens at the elbow joint.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Chest",
                                "Incline_Dumbbell_Flyes"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Dumbbell Flyes - With A Twist",
                                "1) Hold a dumbbell in each hand and lie on an incline bench that"
                                        + " is set to an incline angle of no more than 30 degrees.\\n"
                                        + "2) Extend your arms above you with a slight bend at the"
                                        + " elbows.\\n"
                                        + "3) Now rotate the wrists so that the palms of your hands are"
                                        + " facing you. Tip: The pinky fingers should be next to each"
                                        + " other. This will be your starting position.\\n"
                                        + "4) As you breathe in, start to slowly lower the arms to the"
                                        + " side while keeping the arms extended and while rotating the"
                                        + " wrists until the palms of the hand are facing each other."
                                        + " Tip: At the end of the movement the arms will be by your"
                                        + " side with the palms facing the ceiling.\\n"
                                        + "5) As you exhale start to bring the dumbbells back up to the"
                                        + " starting position by reversing the motion and rotating the"
                                        + " hands so that the pinky fingers are next to each other"
                                        + " again. Tip: Keep in mind that the movement will only happen"
                                        + " at the shoulder joint and at the wrist. There is no motion"
                                        + " that happens at the elbow joint.\\n"
                                        + "6) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Chest",
                                "Incline_Dumbbell_Flyes_-_With_A_Twist"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Dumbbell Press",
                                "1) Lie back on an incline bench with a dumbbell in each hand atop"
                                        + " your thighs. The palms of your hands will be facing each"
                                        + " other.\\n"
                                        + "2) Then, using your thighs to help push the dumbbells up,"
                                        + " lift the dumbbells one at a time so that you can hold them"
                                        + " at shoulder width.\\n"
                                        + "3) Once you have the dumbbells raised to shoulder width,"
                                        + " rotate your wrists forward so that the palms of your hands"
                                        + " are facing away from you. This will be your starting"
                                        + " position.\\n"
                                        + "4) Be sure to keep full control of the dumbbells at all"
                                        + " times. Then breathe out and push the dumbbells up with your"
                                        + " chest.\\n"
                                        + "5) Lock your arms at the top, hold for a second, and then"
                                        + " start slowly lowering the weight. Tip Ideally, lowering the"
                                        + " weights should take about twice as long as raising them.\\n"
                                        + "6) Repeat the movement for the prescribed amount of"
                                        + " repetitions.\\n"
                                        + "7) When you are done, place the dumbbells back on your"
                                        + " thighs and then on the floor. This is the safest manner to"
                                        + " release the dumbbells.",
                                "strength",
                                "Chest",
                                "Incline_Dumbbell_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up",
                                "1) Stand facing bench or sturdy elevated platform. Place hands on"
                                        + " edge of bench or platform, slightly wider than shoulder"
                                        + " width.\\n"
                                        + "2) Position forefoot back from bench or platform with arms"
                                        + " and body straight. Arms should be perpendicular to body."
                                        + " Keeping body straight, lower chest to edge of box or"
                                        + " platform by bending arms.\\n"
                                        + "3) Push body up until arms are extended. Repeat.",
                                "strength",
                                "Chest",
                                "Incline_Push-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up Depth Jump",
                                "1) For this drill you will need a box about 12 inches high, and"
                                        + " two thick mats or aerobics steps.\\n"
                                        + "2) Place the steps just outside of your shoulders, and place"
                                        + " your feet on top of the box so that you are in an incline"
                                        + " pushup position, your hands just inside the steps. This"
                                        + " will be your starting position.\\n"
                                        + "3) Begin by bending at the elbows to lower your body,"
                                        + " quickly reversing position to push your body off of the"
                                        + " ground. As you leave the ground, move your hands onto the"
                                        + " steps, bending your elbows to absorb the impact.\\n"
                                        + "4) Repeat the motion to return to the starting position.",
                                "plyometrics",
                                "Chest",
                                "Incline_Push-Up_Depth_Jump"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up Medium",
                                "1) Stand facing a Smith machine bar or sturdy elevated platform at"
                                        + " an appropriate height.\\n"
                                        + "2) Place your hands on the bar, with your hands about"
                                        + " shoulder width apart.\\n"
                                        + "3) Position your feet back from the bar with arms and body"
                                        + " straight. This will be your starting position.\\n"
                                        + "4) Keeping your body straight, lower your chest to the bar"
                                        + " by bending the arms.\\n"
                                        + "5) Return to the starting position by extending the elbows,"
                                        + " pressing yourself back up.",
                                "strength",
                                "Chest",
                                "Incline_Push-Up_Medium"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up Reverse Grip",
                                "1) Stand facing a Smith machine bar or sturdy elevated platform at"
                                        + " an appropriate height.\\n"
                                        + "2) Place your hands on the bar palms up, with your hands"
                                        + " about shoulder width apart.\\n"
                                        + "3) Position your feet back from the bar with arms and body"
                                        + " straight. This will be your starting position.\\n"
                                        + "4) Keeping your body straight, lower your chest to the bar"
                                        + " by bending the arms.\\n"
                                        + "5) Return to the starting position by extending the elbows,"
                                        + " pressing yourself back up.",
                                "strength",
                                "Chest",
                                "Incline_Push-Up_Reverse_Grip"));
                addExercise(
                        new Exercise(
                                0,
                                "Incline Push-Up Wide",
                                "1) Stand facing a Smith machine bar or sturdy elevated platform at"
                                        + " an appropriate height.\\n"
                                        + "2) Place your hands on the bar, with your hands wider than"
                                        + " shoulder width.\\n"
                                        + "3) Position your feet back from the bar with arms and body"
                                        + " straight. Your arms should be perpendicular to the body."
                                        + " This will be your starting position.\\n"
                                        + "4) Keeping your body straight, lower your chest to the bar"
                                        + " by bending the arms.\\n"
                                        + "5) Return to the starting position by extending the elbows,"
                                        + " pressing yourself back up.",
                                "strength",
                                "Chest",
                                "Incline_Push-Up_Wide"));
                addExercise(
                        new Exercise(
                                0,
                                "Isometric Chest Squeezes",
                                "1) While either seating or standing, bend your arms at a 90-degree"
                                        + " angle and place the palms of your hands together in front"
                                        + " of your chest. Tip: Your hands should be open with the"
                                        + " palms together and fingers facing forward (perpendicular to"
                                        + " your torso).\\n"
                                        + "2) Push both hands against each other as you contract your"
                                        + " chest. Start with slow tension and increase slowly. Keep"
                                        + " breathing normally as you execute this contraction.\\n"
                                        + "3) Hold for the recommended number of seconds.\\n"
                                        + "4) Now release the tension slowly.\\n"
                                        + "5) Rest for the recommended amount of time and repeat.",
                                "plyometrics",
                                "Chest",
                                "Isometric_Chest_Squeezes"));
                addExercise(
                        new Exercise(
                                0,
                                "Isometric Wipers",
                                "1) Assume a push-up position, supporting your weight on your hands"
                                        + " and toes while keeping your body straight. Your hands"
                                        + " should be just outside of shoulder width. This will be your"
                                        + " starting position.\\n"
                                        + "2) Begin by shifting your body weight as far to one side as"
                                        + " possible, allowing the elbow on that side to flex as you"
                                        + " lower your body.\\n"
                                        + "3) Reverse the motion by extending the flexed arm, pushing"
                                        + " yourself up and then dropping to the other side.\\n"
                                        + "4) Repeat for the desired number of repetitions.",
                                "strength",
                                "Chest",
                                "Isometric_Wipers"));
                addExercise(
                        new Exercise(
                                0,
                                "Jackknife Sit-Up",
                                "1) Lie flat on the floor (or exercise mat) on your back with your"
                                        + " arms extended straight back behind your head and your legs"
                                        + " extended also. This will be your starting position.\\n"
                                        + "2) As you exhale, bend at the waist while simultaneously"
                                        + " raising your legs and arms to meet in a jackknife position."
                                        + " Tip: The legs should be extended and lifted at"
                                        + " approximately a 35-45 degree angle from the floor and the"
                                        + " arms should be extended and parallel to your legs. The"
                                        + " upper torso should be off the floor.\\n"
                                        + "3) While inhaling, lower your arms and legs back to the"
                                        + " starting position.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Jackknife_Sit-Up"));
                addExercise(
                        new Exercise(
                                0,
                                "Kettlebell Pirate Ships",
                                "1) With a wide stance, hold a kettlebell with both hands. Allow it"
                                        + " to hang at waist level with your arms extended. This will"
                                        + " be your starting position.\\n"
                                        + "2) Initiate the movement by turning to one side, swinging"
                                        + " the kettlebell to head height. Briefly pause at the top of"
                                        + " the motion.\\n"
                                        + "3) Allow the bell to drop as you rotate to the opposite"
                                        + " side, again raising the kettlebell to head height.\\n"
                                        + "4) Repeat for the desired amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Kettlebell_Pirate_Ships"));
                addExercise(
                        new Exercise(
                                0,
                                "Knee Tuck Jump",
                                "1) Begin in a comfortable standing position with your knees"
                                        + " slightly bent. Hold your hands in front of you, palms down"
                                        + " with your fingertips together at chest height. This will be"
                                        + " your starting position.\\n"
                                        + "2) Rapidly dip down into a quarter squat and immediately"
                                        + " explode upward. Drive the knees towards the chest,"
                                        + " attempting to touch them to the palms of the hands.\\n"
                                        + "3) Jump as high as you can, raising your knees up, and then"
                                        + " ensure a good land be re-extending your legs, absorbing"
                                        + " impact through be allowing the knees to rebend.",
                                "plyometrics",
                                "Hamstrings",
                                "Knee_Tuck_Jump"));
                addExercise(
                        new Exercise(
                                0,
                                "Landmine 180's",
                                "1) Position a bar into a landmine or securely anchor it in a"
                                        + " corner. Load the bar to an appropriate weight.\\n"
                                        + "2) Raise the bar from the floor, taking it to shoulder"
                                        + " height with both hands with your arms extended in front of"
                                        + " you. Adopt a wide stance. This will be your starting"
                                        + " position.\\n"
                                        + "3) Perform the movement by rotating the trunk and hips as"
                                        + " you swing the weight all the way down to one side. Keep"
                                        + " your arms extended throughout the exercise.\\n"
                                        + "4) Reverse the motion to swing the weight all the way to the"
                                        + " opposite side.\\n"
                                        + "5) Continue alternating the movement until the set is"
                                        + " complete.",
                                "strength",
                                "Abdominals",
                                "Landmine_180s"));
                addExercise(
                        new Exercise(
                                0,
                                "Leg Press",
                                "1) Using a leg press machine, sit down on the machine and place"
                                        + " your legs on the platform directly in front of you at a"
                                        + " medium (shoulder width) foot stance. (Note: For the"
                                        + " purposes of this discussion we will use the medium stance"
                                        + " described above which targets overall development; however"
                                        + " you can choose any of the three stances described in the"
                                        + " foot positioning section).\\n"
                                        + "2) Lower the safety bars holding the weighted platform in"
                                        + " place and press the platform all the way up until your legs"
                                        + " are fully extended in front of you. Tip: Make sure that you"
                                        + " do not lock your knees. Your torso and the legs should make"
                                        + " a perfect 90-degree angle. This will be your starting"
                                        + " position.\\n"
                                        + "3) As you inhale, slowly lower the platform until your upper"
                                        + " and lower legs make a 90-degree angle.\\n"
                                        + "4) Pushing mainly with the heels of your feet and using the"
                                        + " quadriceps go back to the starting position as you"
                                        + " exhale.\\n"
                                        + "5) Repeat for the recommended amount of repetitions and"
                                        + " ensure to lock the safety pins properly once you are done."
                                        + " You do not want that platform falling on you fully loaded.",
                                "strength",
                                "Quadriceps",
                                "Leg_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Leg Pull-In",
                                "1) Lie on an exercise mat with your legs extended and your hands"
                                        + " either palms facing down next to you or under your glutes."
                                        + " Tip: My preference is with the hands next to me. This will"
                                        + " be your starting position.\\n"
                                        + "2) Bend your knees and pull your upper thighs into your"
                                        + " midsection as you breathe out. Continue the motion until"
                                        + " your knees are around chest level. Contract your abs as you"
                                        + " execute this movement and hold for a second at the top."
                                        + " Tip: As you perform the motion, the lower legs (calves)"
                                        + " should always remain parallel to the floor.\\n"
                                        + "3) Return to the starting position as you inhale.\\n"
                                        + "4) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Abdominals",
                                "Leg_Pull-In"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Chest Press",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " for your height. The handles should be near the bottom or"
                                        + " middle of the pectorals at the beginning of the motion.\\n"
                                        + "2) Your chest and head should be up and your shoulder blades"
                                        + " retracted. This will be your starting position.\\n"
                                        + "3) Press the handles forward by extending through the"
                                        + " elbow.\\n"
                                        + "4) After a brief pause at the top, return the weight just"
                                        + " above the start position, keeping tension on the muscles by"
                                        + " not returning the weight to the stops until the set is"
                                        + " complete.",
                                "strength",
                                "Chest",
                                "Leverage_Chest_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Deadlift",
                                "1) Load the pins to an appropriate weight. Position yourself"
                                        + " directly between the handles. Grasp the bottom handles with"
                                        + " a comfortable grip, and then lower your hips as you take a"
                                        + " breath. Look forward with your head and keep your chest up."
                                        + " This will be your starting position.\\n"
                                        + "2) Return the weight to the starting position.",
                                "strength",
                                "Quadriceps",
                                "Leverage_Deadlift"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Decline Chest Press",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " for your height. The handles should be near the bottom of"
                                        + " the pectorals at the beginning of the motion. Your chest"
                                        + " and head should be up and your shoulder blades retracted."
                                        + " This will be your starting position.\\n"
                                        + "2) Press the handles forward by extending through the"
                                        + " elbow.\\n"
                                        + "3) After a brief pause at the top, return the weight just"
                                        + " above the start position, keeping tension on the muscles by"
                                        + " not returning the weight to the stops until the set is"
                                        + " complete.",
                                "strength",
                                "Chest",
                                "Leverage_Decline_Chest_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Incline Chest Press",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " for your height. The handles should be near the top of the"
                                        + " pectorals at the beginning of the motion. Your chest and"
                                        + " head should be up and your shoulder blades retracted. This"
                                        + " will be your starting position.\\n"
                                        + "2) Press the handles forward by extending through the"
                                        + " elbow.\\n"
                                        + "3) After a brief pause at the top, return the weight just"
                                        + " above the start position, keeping tension on the muscles by"
                                        + " not returning the weight to the stops until the set is"
                                        + " complete.",
                                "strength",
                                "Chest",
                                "Leverage_Incline_Chest_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Leverage Shoulder Press",
                                "1) Load an appropriate weight onto the pins and adjust the seat"
                                        + " for your height. The handles should be near the top of the"
                                        + " shoulders at the beginning of the motion. Your chest and"
                                        + " head should be up and handles held with a pronated grip."
                                        + " This will be your starting position.\\n"
                                        + "2) Press the handles upward by extending through the"
                                        + " elbow.\\n"
                                        + "3) After a brief pause at the top, return the weight to just"
                                        + " above the start position, keeping tension on the muscles by"
                                        + " not returning the weight to the stops until the set is"
                                        + " complete.",
                                "strength",
                                "Shoulders",
                                "Leverage_Shoulder_Press"));
                addExercise(
                        new Exercise(
                                0,
                                "Linear 3-Part Start Technique",
                                "1) This drill helps you accelerate as quickly as possible into a"
                                        + " sprint from a dead stop. It helps to use a line to start"
                                        + " from. Begin with two feet on the line. Place your left foot"
                                        + " with the toe next to your right ankle. Place your right"
                                        + " foot 4-6 inches behind the left.\\n"
                                        + "2) Place your right hand onto the line, and thing bring your"
                                        + " nose close to your left knee.\\n"
                                        + "3) Squat down as you lean foward, your head being lower than"
                                        + " your hips and your weight loaded onto the left leg. This"
                                        + " will be your starting position.\\n"
                                        + "4) Take your left hand up so that it is parallel to the"
                                        + " ground, pointing behind you, and explode out when ready.",
                                "plyometrics",
                                "Hamstrings",
                                "Linear_3-Part_Start_Technique"));
                addExercise(
                        new Exercise(
                                0,
                                "Linear Acceleration Wall Drill",
                                "1) Lean at around 45 degrees against a wall. Your feet should be"
                                        + " together, glutes contracted.\\n"
                                        + "2) Begin by lifting your right knee quickly, pausing, and"
                                        + " then driving it straight down into the ground.\\n"
                                        + "3) Switch legs, raising the opposite knee, and then"
                                        + " attacking the ground straight down.\\n"
                                        + "4) Repeat once more with your right leg, and as soon as the"
                                        + " right foot strikes the ground hammer them out rapidly,"
                                        + " alternating left and right as fast as you can.",
                                "plyometrics",
                                "Hamstrings",
                                "Linear_Acceleration_Wall_Drill"));
                addExercise(
                        new Exercise(
                                0,
                                "Low Pulley Row To Neck",
                                "1) Sit on a low pulley row machine with a rope attachment.\\n"
                                        + "2) Grab the ends of the rope using a palms-down grip and sit"
                                        + " with your back straight and your knees slightly bent. Tip:"
                                        + " Keep your back almost completely vertical and your arms"
                                        + " fully extended in front of you. This will be your starting"
                                        + " position.\\n"
                                        + "3) While keeping your torso stationary, lift your elbows and"
                                        + " start bending them as you pull the rope towards your neck"
                                        + " while exhaling. Throughout the movement your upper arms"
                                        + " should remain parallel to the floor. Tip: Continue this"
                                        + " motion until your hands are almost next to your ears (the"
                                        + " forearms will not be parallel to the floor at the end of"
                                        + " the movement as they will be angled a bit upwards) and your"
                                        + " elbows are out away from your sides.\\n"
                                        + "4) After holding for a second or so at the contracted"
                                        + " position, come back slowly to the starting position as you"
                                        + " inhale. Tip: Again, during no part of the movement should"
                                        + " the torso move.\\n"
                                        + "5) Repeat for the recommended amount of repetitions.",
                                "strength",
                                "Shoulders",
                                "Low_Pulley_Row_To_Neck"));
                addExercise(
                        new Exercise(
                                0,
                                "Machine Bench Press",
                                "1) Sit down on the Chest Press Machine and select the weight.\\n"
                                        + "2) Step on the lever provided by the machine since it will"
                                        + " help you to bring the handles forward so that you can grab"
                                        + " the handles and fully extend the arms.\\n"
                                        + "3) Grab the handles with a palms-down grip and lift your"
                                        + " elbows so that your upper arms are parallel to the floor to"
                                        + " the sides of your torso. Tip: Your forearms will be"
                                        + " pointing forward since you are grabbing the handles. Once"
                                        + " you bring the handles forward and extend the arms you will"
                                        + " be at the starting position.\\n"
                                        + "4) Now bring the handles back towards you as you breathe"
                                        + " in.\\n"
                                        + "5) Push the handles away from you as you flex your pecs and"
                                        + " you breathe out. Hold the contraction for a second before"
                                        + " going back to the starting position.\\n"
                                        + "6) Repeat for the recommended amount of reps.\\n"
                                        + "7) When finished step on the lever again and slowly get the"
                                        + " handles back to their original place.",
                                "strength",
                                "Chest",
                                "Machine_Bench_Press"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed exercises", e);
        }
    }

    public void addExercise(Exercise exercise) {
        String sql = "INSERT INTO exercises (name, instruction, category, primary_muscle,"
                + " exercise_image_url) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, exercise.getName());
            statement.setString(2, exercise.getInstruction());
            statement.setString(3, exercise.getCategory());
            statement.setString(4, exercise.getPrimaryMuscle());
            statement.setString(5, exercise.getExerciseImageId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exercise.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add exercise", e);
        }
    }

    public Exercise getExerciseById(int id) {
        String sql = "SELECT id, name, instruction, category, primary_muscle, exercise_image_url FROM"
                + " exercises WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRowToExercise(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get exercise by id", e);
        }
    }

    public ObservableList<Exercise> getAllExercises() {
        String sql = "SELECT id, name, instruction, category, primary_muscle, exercise_image_url FROM"
                + " exercises ORDER BY name";
        ObservableList<Exercise> exercises = FXCollections.observableArrayList();
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                exercises.add(
                        new Exercise(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("instruction"),
                                rs.getString("category"),
                                rs.getString("primary_muscle"),
                                rs.getString("exercise_image_url")));
            }
            return exercises;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get exercises", e);
        }
    }

    /**
     * Returns the DB id of an exercise whose name matches (case-insensitive), or 0
     * if not found.
     */
    public int findExerciseIdByName(String name) {
        String sql = "SELECT id FROM exercises WHERE lower(name)=lower(?) LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Error finding exercise by name: " + e.getMessage());
        }
        return 0;
    }

    private Exercise mapRowToExercise(ResultSet rs) throws SQLException {
        return new Exercise(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("instruction"),
                rs.getString("category"),
                rs.getString("primary_muscle"),
                rs.getString("exercise_image_url"));
    }
}
