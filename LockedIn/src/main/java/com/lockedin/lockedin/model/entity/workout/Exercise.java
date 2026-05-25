package com.lockedin.lockedin.model.entity.workout;

/**
 * Represents an exercise definition with metadata and demonstration image URLs.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class Exercise {
    private int id;
    private String name;
    private String instruction;
    private String category;
    private String primaryMuscle;
    private String exerciseImageId;

        /**
     * Constructs a Exercise using default application dependencies.
     * @param id id
     * @param name name
     * @param instruction instruction
     * @param category category
     * @param primaryMuscle The primary muscle.
     * @param exerciseImageId The exercise image id.
     */
    public Exercise(
            int id,
            String name,
            String instruction,
            String category,
            String primaryMuscle,
            String exerciseImageId) {
        this.id = id;
        this.name = name;
        this.instruction = instruction;
        this.category = category;
        this.primaryMuscle = primaryMuscle;
        this.exerciseImageId = exerciseImageId;
    }

            /**
     * Returns the id.
     * @return id
     */
    public int getId() {
        return id;
    }

        /**
     * Sets the id.
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

            /**
     * Returns the name.
     * @return name
     */
    public String getName() {
        return name;
    }

        /**
     * Sets the name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

            /**
     * Returns the instruction.
     * @return instruction
     */
    public String getInstruction() {
        return instruction;
    }

        /**
     * Sets the instruction.
     * @param instruction instruction
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

            /**
     * Returns the category.
     * @return category
     */
    public String getCategory() {
        return category;
    }

        /**
     * Sets the category.
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

            /**
     * Returns the primary muscle.
     * @return primary muscle
     */
    public String getPrimaryMuscle() {
        return primaryMuscle;
    }

    /**
     * Sets the primary muscle.
     * @param primaryMuscle The primary muscle.
     */
    public void setPrimaryMuscle(String primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
    }

            /**
     * Returns the exercise image id.
     * @return exercise image id
     */
    public String getExerciseImageId() {
        return exerciseImageId;
    }

    /**
     * Sets the exercise image id.
     * @param exerciseImageId The exercise image id.
     */
    public void setExerciseImageId(String exerciseImageId) {
        this.exerciseImageId = exerciseImageId;
    }

            /**
     * Returns the exercise image url.
     * @param option option
     * @return exercise image url
     */
    public String getExerciseImageUrl(int option) {
        final String baseImageUrl =
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";
        return baseImageUrl + exerciseImageId + "/" + option + ".jpg";
    }
    /**
     * Returns a string representation of this object.
     * @return The resulting text.
     */

    @Override
    public String toString() {
        return name;
    }
}
