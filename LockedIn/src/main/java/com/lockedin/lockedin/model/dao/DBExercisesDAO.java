package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.Exercise;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBExercisesDAO {
    private final Connection connection;

    public DBExercisesDAO() {
        this.connection = SqliteConnection.getInstance();
        createExercisesTable();
        seedExercises();
    }

    private void createExercisesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instruction TEXT, " +
                "category TEXT, " +
                "primary_muscle TEXT" +
                ")";

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
                addExercise(new Exercise(0, "Barbell Back Squat", "Place bar on upper back, squat to parallel, stand tall", "Strength", "Quads"));
                addExercise(new Exercise(0, "Front Squat", "Rest bar on front delts, keep torso upright, squat and stand", "Strength", "Quads"));
                addExercise(new Exercise(0, "Goblet Squat", "Hold dumbbell at chest, squat down with controlled depth", "Strength", "Quads"));
                addExercise(new Exercise(0, "Bulgarian Split Squat", "Rear foot elevated, lower into lunge, drive through front heel", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Walking Lunge", "Step forward into lunge and alternate legs while moving", "Strength", "Quads"));
                addExercise(new Exercise(0, "Reverse Lunge", "Step backward into lunge, keep front knee stable", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Leg Press", "Press platform away using full foot contact", "Strength", "Quads"));
                addExercise(new Exercise(0, "Hack Squat", "Use hack squat machine, lower under control and press up", "Strength", "Quads"));
                addExercise(new Exercise(0, "Romanian Deadlift", "Hinge at hips with slight knee bend, feel hamstring stretch", "Strength", "Hamstrings"));
                addExercise(new Exercise(0, "Conventional Deadlift", "Pull bar from floor with flat back and locked core", "Strength", "Back"));
                addExercise(new Exercise(0, "Sumo Deadlift", "Wide stance deadlift, keep knees tracking over toes", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Trap Bar Deadlift", "Lift handles with neutral grip, drive through legs", "Strength", "Quads"));
                addExercise(new Exercise(0, "Hip Thrust", "Upper back on bench, thrust hips upward and squeeze glutes", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Glute Bridge", "Drive hips up from floor position while bracing core", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Step-Up", "Step onto bench and fully extend hip and knee", "Strength", "Quads"));
                addExercise(new Exercise(0, "Leg Extension", "Extend knees against machine pad with control", "Strength", "Quads"));
                addExercise(new Exercise(0, "Seated Leg Curl", "Curl heels back against pad to work hamstrings", "Strength", "Hamstrings"));
                addExercise(new Exercise(0, "Lying Leg Curl", "Flex knees against resistance while keeping hips down", "Strength", "Hamstrings"));
                addExercise(new Exercise(0, "Nordic Hamstring Curl", "Lower body slowly from kneeling while resisting fall", "Strength", "Hamstrings"));
                addExercise(new Exercise(0, "Standing Calf Raise", "Raise heels high, pause, then lower slowly", "Strength", "Calves"));
                addExercise(new Exercise(0, "Seated Calf Raise", "Raise heels from seated machine position with full range", "Strength", "Calves"));
                addExercise(new Exercise(0, "Single-Leg Calf Raise", "Lift heel on one leg and control descent", "Strength", "Calves"));
                addExercise(new Exercise(0, "Barbell Bench Press", "Lower bar to mid chest and press to lockout", "Strength", "Chest"));
                addExercise(new Exercise(0, "Incline Bench Press", "Press weight on incline bench to target upper chest", "Strength", "Chest"));
                addExercise(new Exercise(0, "Decline Bench Press", "Press on decline bench with controlled bar path", "Strength", "Chest"));
                addExercise(new Exercise(0, "Dumbbell Bench Press", "Press dumbbells from chest with neutral wrist alignment", "Strength", "Chest"));
                addExercise(new Exercise(0, "Incline Dumbbell Press", "Press dumbbells on incline while maintaining scapular control", "Strength", "Chest"));
                addExercise(new Exercise(0, "Dumbbell Fly", "Arc dumbbells outward and inward with slight elbow bend", "Strength", "Chest"));
                addExercise(new Exercise(0, "Cable Fly", "Bring cable handles together in front of chest", "Strength", "Chest"));
                addExercise(new Exercise(0, "Push-Up", "Lower chest toward floor and press up in plank", "Strength", "Chest"));
                addExercise(new Exercise(0, "Dips", "Lower body between bars and press up to start", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Chest Press Machine", "Press machine handles forward using controlled tempo", "Strength", "Chest"));
                addExercise(new Exercise(0, "Pec Deck Fly", "Bring pads together in hugging motion", "Strength", "Chest"));
                addExercise(new Exercise(0, "Barbell Overhead Press", "Press bar overhead while keeping ribs down", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Seated Dumbbell Shoulder Press", "Press dumbbells overhead from seated position", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Arnold Press", "Rotate palms during press to hit all deltoid heads", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Lateral Raise", "Raise dumbbells to shoulder height with soft elbows", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Front Raise", "Lift weight in front to shoulder level", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Rear Delt Fly", "Open arms outward while hinging to target rear delts", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Face Pull", "Pull rope toward face with elbows high", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Upright Row", "Pull bar vertically to upper chest", "Strength", "Shoulders"));
                addExercise(new Exercise(0, "Barbell Row", "Row bar to lower ribs with flat back", "Strength", "Back"));
                addExercise(new Exercise(0, "Dumbbell Row", "Row dumbbell toward hip while bracing core", "Strength", "Back"));
                addExercise(new Exercise(0, "T-Bar Row", "Pull T-bar handle toward torso with controlled squeeze", "Strength", "Back"));
                addExercise(new Exercise(0, "Seated Cable Row", "Pull cable handle to torso while keeping chest up", "Strength", "Back"));
                addExercise(new Exercise(0, "Lat Pulldown", "Pull bar to upper chest with elbows driving down", "Strength", "Lats"));
                addExercise(new Exercise(0, "Pull-Up", "Pull body until chin passes bar", "Strength", "Lats"));
                addExercise(new Exercise(0, "Chin-Up", "Underhand grip pull-up focusing on arms and lats", "Strength", "Lats"));
                addExercise(new Exercise(0, "Straight-Arm Pulldown", "Pull bar down with straight arms using lats", "Strength", "Lats"));
                addExercise(new Exercise(0, "Chest-Supported Row", "Row from chest pad to reduce lower back stress", "Strength", "Back"));
                addExercise(new Exercise(0, "Shrug", "Elevate shoulders upward and pause at top", "Strength", "Traps"));
                addExercise(new Exercise(0, "Back Extension", "Extend spine from hip hinge position", "Strength", "Lower Back"));
                addExercise(new Exercise(0, "Good Morning", "Hinge forward with bar on back and return upright", "Strength", "Hamstrings"));
                addExercise(new Exercise(0, "Hyperextension", "Raise torso from back extension bench with control", "Strength", "Lower Back"));
                addExercise(new Exercise(0, "Barbell Biceps Curl", "Curl bar toward shoulders without swinging", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Dumbbell Biceps Curl", "Curl dumbbells with elbows close to body", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Hammer Curl", "Neutral grip curl targeting brachialis and forearms", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Preacher Curl", "Curl on preacher bench to isolate biceps", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Concentration Curl", "Single-arm seated curl with strict form", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Cable Curl", "Curl cable attachment with constant tension", "Strength", "Biceps"));
                addExercise(new Exercise(0, "EZ-Bar Curl", "Curl EZ-bar with comfortable wrist angle", "Strength", "Biceps"));
                addExercise(new Exercise(0, "Triceps Pushdown", "Press cable bar down until elbows extend", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Overhead Triceps Extension", "Extend weight overhead while elbows stay tucked", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Skull Crusher", "Lower bar toward forehead and extend elbows", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Close-Grip Bench Press", "Press bar with narrow grip to bias triceps", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Triceps Dips", "Dip body upright to emphasize triceps extension", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Kickback", "Extend elbow backward with dumbbell or cable", "Strength", "Triceps"));
                addExercise(new Exercise(0, "Wrist Curl", "Curl wrists upward while forearms are supported", "Strength", "Forearms"));
                addExercise(new Exercise(0, "Reverse Wrist Curl", "Extend wrists upward against resistance", "Strength", "Forearms"));
                addExercise(new Exercise(0, "Farmer's Carry", "Walk while carrying heavy weights with tall posture", "Strength", "Forearms"));
                addExercise(new Exercise(0, "Plank", "Hold rigid forearm plank while bracing core", "Core", "Abs"));
                addExercise(new Exercise(0, "Side Plank", "Hold side plank with hips lifted and aligned", "Core", "Obliques"));
                addExercise(new Exercise(0, "Dead Bug", "Alternate arm and leg extension while keeping back flat", "Core", "Abs"));
                addExercise(new Exercise(0, "Bird Dog", "Extend opposite arm and leg while stabilizing trunk", "Core", "Lower Back"));
                addExercise(new Exercise(0, "Hanging Leg Raise", "Raise straight legs while hanging from bar", "Core", "Abs"));
                addExercise(new Exercise(0, "Lying Leg Raise", "Lift legs from floor while keeping lower back pressed down", "Core", "Abs"));
                addExercise(new Exercise(0, "Reverse Crunch", "Curl pelvis toward chest from supine position", "Core", "Abs"));
                addExercise(new Exercise(0, "Cable Crunch", "Crunch torso downward using cable resistance", "Core", "Abs"));
                addExercise(new Exercise(0, "Russian Twist", "Rotate torso side to side with braced core", "Core", "Obliques"));
                addExercise(new Exercise(0, "Pallof Press", "Press cable forward and resist torso rotation", "Core", "Obliques"));
                addExercise(new Exercise(0, "Mountain Climber", "Drive knees alternately toward chest in plank", "Conditioning", "Abs"));
                addExercise(new Exercise(0, "Bicycle Crunch", "Alternate elbow to opposite knee in twisting crunch", "Core", "Obliques"));
                addExercise(new Exercise(0, "Ab Wheel Rollout", "Roll wheel forward and return while maintaining neutral spine", "Core", "Abs"));
                addExercise(new Exercise(0, "Woodchopper", "Rotate cable diagonally across body with control", "Core", "Obliques"));
                addExercise(new Exercise(0, "Copenhagen Plank", "Support top leg on bench and hold side plank", "Core", "Adductors"));
                addExercise(new Exercise(0, "Clamshell", "Open top knee while lying on side with hips stacked", "Mobility", "Glutes"));
                addExercise(new Exercise(0, "Fire Hydrant", "Lift knee outward from quadruped position", "Mobility", "Glutes"));
                addExercise(new Exercise(0, "Cable Hip Abduction", "Move leg away from body against cable resistance", "Strength", "Glutes"));
                addExercise(new Exercise(0, "Cable Hip Adduction", "Move leg toward midline against cable resistance", "Strength", "Adductors"));
                addExercise(new Exercise(0, "Kettlebell Swing", "Hinge and explosively swing kettlebell to chest height", "Conditioning", "Glutes"));
                addExercise(new Exercise(0, "Thruster", "Perform front squat then press weight overhead", "Conditioning", "Shoulders"));
                addExercise(new Exercise(0, "Burpee", "Drop to floor, jump feet in, and explode upward", "Conditioning", "Full Body"));
                addExercise(new Exercise(0, "Box Jump", "Jump onto box and land softly with control", "Plyometric", "Quads"));
                addExercise(new Exercise(0, "Jump Squat", "Explode upward from squat and land softly", "Plyometric", "Quads"));
                addExercise(new Exercise(0, "Battle Ropes", "Create alternating rope waves with stable torso", "Conditioning", "Shoulders"));
                addExercise(new Exercise(0, "Sled Push", "Drive sled forward with short powerful steps", "Conditioning", "Quads"));
                addExercise(new Exercise(0, "Sled Pull", "Pull sled backward or forward under constant tension", "Conditioning", "Hamstrings"));
                addExercise(new Exercise(0, "Rowing Machine Sprint", "Sprint on rower with strong leg drive", "Conditioning", "Back"));
                addExercise(new Exercise(0, "Assault Bike Sprint", "Pedal and push handles at max intensity", "Conditioning", "Full Body"));
                addExercise(new Exercise(0, "Stair Climber", "Climb at steady pace with upright posture", "Conditioning", "Glutes"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed exercises", e);
        }
    }

    public void addExercise(Exercise exercise) {
        String sql = "INSERT INTO exercises (name, instruction, category, primary_muscle) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, exercise.getName());
            statement.setString(2, exercise.getInstruction());
            statement.setString(3, exercise.getCategory());
            statement.setString(4, exercise.getPrimaryMuscle());
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
        String sql = "SELECT id, name, instruction, category, primary_muscle FROM exercises WHERE id = ?";

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

    public ObservableList<String> getAllExerciseNames() {
        String sql = "SELECT name FROM exercises ORDER BY name";
        ObservableList<String> exerciseNames = FXCollections.observableArrayList();

        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                exerciseNames.add(rs.getString("name"));
            }
            return exerciseNames;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get exercise names", e);
        }
    }

    private Exercise mapRowToExercise(ResultSet rs) throws SQLException {
        return new Exercise(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("instruction"),
                rs.getString("category"),
                rs.getString("primary_muscle"));
    }
}
