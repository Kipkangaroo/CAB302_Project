package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserProgressDAO {
  private static final String USER_DB = "users.db";
  private final Connection connection;

  public UserProgressDAO() {
    this.connection = SqliteConnection.getInstance(USER_DB);
    createUserProgressTable();
    addSessionTargetColumnIfMissing();
  }

  public UserProgressDAO(Connection connection) {
    this.connection = connection;
    createUserProgressTable();
    addSessionTargetColumnIfMissing();
  }

  private void createUserProgressTable() {
    String sql = "CREATE TABLE IF NOT EXISTS user_progress ("
        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "user_id INTEGER NOT NULL, "
        + "fitness_goal TEXT NOT NULL, "
        + "weight REAL NOT NULL, "
        + "target_calories REAL NOT NULL, "
        + "effective_from TEXT NOT NULL, "
        + "target_sessions INTEGER DEFAULT 3"
        + ")";

    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create user_progress table", e);
    }
  }

  private void addSessionTargetColumnIfMissing() {
    try (Statement statement = connection.createStatement()) {
      statement.execute(
          "ALTER TABLE user_progress ADD COLUMN target_sessions INTEGER DEFAULT 3");
    } catch (SQLException e) {
      // Column already exists — ignore
    }
  }

  public boolean addUserProgress(UserProgress userProgress) {
    Optional<UserProgress> existingProgress = getUserProgressByDate(userProgress.getUserId(),
        userProgress.getEffectiveFrom());
    if (existingProgress.isPresent()) {
      UserProgress existing = existingProgress.get();
      String updateSql = "UPDATE user_progress SET fitness_goal = ?, weight = ?, target_calories = ? WHERE id = ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
        FitnessGoal fitnessGoal = userProgress.getFitnessGoal();
        preparedStatement.setString(1, fitnessGoal == null ? null : fitnessGoal.getDisplayName());
        preparedStatement.setDouble(2, userProgress.getWeight());
        preparedStatement.setDouble(3, userProgress.getTargetCalories());
        preparedStatement.setInt(4, existing.getId());
        return preparedStatement.executeUpdate() > 0;
      } catch (SQLException e) {
        throw new RuntimeException("Failed to update user progress", e);
      }
    }

    String sql = "INSERT INTO user_progress(user_id, fitness_goal, weight, target_calories, effective_from)"
        + " VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setInt(1, userProgress.getUserId());
      FitnessGoal fitnessGoal = userProgress.getFitnessGoal();
      preparedStatement.setString(2, fitnessGoal == null ? null : fitnessGoal.getDisplayName());
      preparedStatement.setDouble(3, userProgress.getWeight());
      preparedStatement.setDouble(4, userProgress.getTargetCalories());
      preparedStatement.setString(5, userProgress.getEffectiveFrom().toString());
      int rowsInserted = preparedStatement.executeUpdate();
      if (rowsInserted == 0) {
        return false;
      }
      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          userProgress.setId(generatedKeys.getInt(1));
        }
      }
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to add user progress", e);
    }
  }

  public Optional<UserProgress> getUserProgressByDate(int userId, LocalDate date) {
    String sql = "SELECT * FROM user_progress WHERE user_id = ? AND effective_from = ? "
        + "ORDER BY id DESC LIMIT 1";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, userId);
      preparedStatement.setString(2, date.toString());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(mapUserProgress(resultSet));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch user progress by date", e);
    }
  }

  public List<UserProgress> getUserProgressHistory(int userId) {
    String sql = "SELECT * FROM user_progress WHERE user_id = ? ORDER BY effective_from DESC, id DESC";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        List<UserProgress> progress = new ArrayList<>();
        while (resultSet.next()) {
          progress.add(mapUserProgress(resultSet));
        }
        return progress;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to fetch user progress history", e);
    }
  }

  public Optional<UserProgress> getLatestUserProgress(int userId, LocalDate date) {
    return getUserProgressHistory(userId).stream()
        .filter(progress -> !progress.getEffectiveFrom().isAfter(date))
        .max(
            Comparator.comparing(UserProgress::getEffectiveFrom)
                .thenComparing(UserProgress::getId));
  }

  public void deleteAllForUser(int userId) {
    String sql = "DELETE FROM user_progress WHERE user_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, userId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete user progress for user " + userId, e);
    }
  }

  /**
   * Returns the effective weight for each day in the range, using the latest
   * recorded progress on or before that date (forward-filled between entries).
   */
  public Map<LocalDate, Double> getDailyWeightForRange(int userId, LocalDate start, LocalDate end) {
    List<UserProgress> history = new ArrayList<>(getUserProgressHistory(userId));
    history.sort(
        Comparator.comparing(UserProgress::getEffectiveFrom).thenComparing(UserProgress::getId));

    double currentWeight =
        new UserDAO().getUserById(userId).map(User::getWeight).orElse(0.0);

    int idx = 0;
    while (idx < history.size() && history.get(idx).getEffectiveFrom().isBefore(start)) {
      currentWeight = history.get(idx).getWeight();
      idx++;
    }

    Map<LocalDate, Double> weights = new LinkedHashMap<>();
    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
      while (idx < history.size() && !history.get(idx).getEffectiveFrom().isAfter(date)) {
        currentWeight = history.get(idx).getWeight();
        idx++;
      }
      weights.put(date, currentWeight);
    }
    return weights;
  }

  public double getDailyTargetCalories(int userId, LocalDate date) {
    Optional<UserProgress> progress = getLatestUserProgress(userId, date);
    if (progress.isPresent()) {
      return progress.get().getTargetCalories();
    }
    return new UserDAO().getUserById(userId).map(User::getTargetCalories).orElse(0.0);
  }

  public int getSessionTarget(int userId) {
    String sql = "SELECT target_sessions FROM user_progress "
        + "WHERE user_id = ? ORDER BY effective_from DESC, id DESC LIMIT 1";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, userId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int val = resultSet.getInt("target_sessions");
          return resultSet.wasNull() ? 3 : val;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to get session target", e);
    }
    return 3;
  }

  public void updateSessionTarget(int userId, int target) {
    String sql = "UPDATE user_progress SET target_sessions = ? WHERE user_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, target);
      preparedStatement.setInt(2, userId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update session target", e);
    }
  }

  private UserProgress mapUserProgress(ResultSet resultSet) throws SQLException {
    return new UserProgress(
        resultSet.getInt("id"),
        resultSet.getInt("user_id"),
        FitnessGoal.fromDisplayName(resultSet.getString("fitness_goal")),
        resultSet.getDouble("weight"),
        resultSet.getDouble("target_calories"),
        LocalDate.parse(resultSet.getString("effective_from")));
  }
}
