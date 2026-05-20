package com.lockedin.lockedin.model.entity.user;

import java.time.LocalDate;
/**
 * Model for a user's body measurement entry.
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

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getValue() { return value; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }

    public void setValue(double value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public void setDate(LocalDate date) { this.date = date; }
}