package com.lockedin.lockedin.model.entity.user;

import java.time.LocalDate;

/**
 * Entity representing a body measurement entry for a user.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class Measurement {
    private int id;
    private int userId;
    private double value;
    private String type;
    private LocalDate date;

    public Measurement(int id, int userId, double value, String type, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.value = value;
        this.type = type;
        this.date = date;
    }

    public Measurement(int userId, double value, String type, LocalDate date) {
        this(-1, userId, value, type, date);
    }

    /** @return the database id */
    public int getId() { return id; }

    /** @return the owning user id */
    public int getUserId() { return userId; }

    /** @return the measured value */
    public double getValue() { return value; }

    /** @return the measurement type (for example weight or body fat) */
    public String getType() { return type; }

    /** @return the date the measurement was recorded */
    public LocalDate getDate() { return date; }

    /** @param value the measured value */
    public void setValue(double value) { this.value = value; }

    /** @param type the measurement type */
    public void setType(String type) { this.type = type; }

    /** @param date the date the measurement was recorded */
    public void setDate(LocalDate date) { this.date = date; }
}