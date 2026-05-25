package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.user.Measurement;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private MeasurementDAO measurementDAO;

    @BeforeEach
    void setUp() throws Exception {
        // Create fake logged‑in user
        User fakeUser = new User();
        fakeUser.setId(1);
        CurrentUser.set(fakeUser);

        // Create in‑memory DB
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);

        // Create DAO (auto‑creates table)
        measurementDAO = new MeasurementDAO(conn);
    }

    private Measurement makeMeasurement(double value, String type, LocalDate date) {
        return new Measurement(
                1,          // userId (matches CurrentUser)
                value,
                type,
                date
        );
    }

    @Test
    void addMeasurement_insertsRow() {
        Measurement m = makeMeasurement(70.5, "Weight", LocalDate.of(2024, 5, 20));
        assertDoesNotThrow(() -> measurementDAO.addMeasurement(m));
    }

    @Test
    void getMeasurements_returnsInsertedRows() {
        Measurement m1 = makeMeasurement(70.0, "Weight", LocalDate.of(2024, 1, 1));
        Measurement m2 = makeMeasurement(71.0, "Weight", LocalDate.of(2024, 2, 1));

        measurementDAO.addMeasurement(m1);
        measurementDAO.addMeasurement(m2);

        List<Measurement> list = measurementDAO.getMeasurements("Weight");

        assertEquals(2, list.size());
        assertEquals(70.0, list.get(0).getValue());
        assertEquals(71.0, list.get(1).getValue());
    }

    @Test
    void getMeasurements_returnsEmpty_whenNoneExist() {
        List<Measurement> list = measurementDAO.getMeasurements("Weight");
        assertTrue(list.isEmpty());
    }

    @Test
    void getMeasurements_ordersByDateAscending() {
        Measurement m1 = makeMeasurement(65.0, "Weight", LocalDate.of(2024, 3, 1));
        Measurement m2 = makeMeasurement(60.0, "Weight", LocalDate.of(2024, 1, 1));
        Measurement m3 = makeMeasurement(62.0, "Weight", LocalDate.of(2024, 2, 1));

        measurementDAO.addMeasurement(m1);
        measurementDAO.addMeasurement(m2);
        measurementDAO.addMeasurement(m3);

        List<Measurement> list = measurementDAO.getMeasurements("Weight");

        assertEquals(60.0, list.get(0).getValue());
        assertEquals(62.0, list.get(1).getValue());
        assertEquals(65.0, list.get(2).getValue());
    }

    @Test
    void deleteMeasurement_removesRow() {
        Measurement m = makeMeasurement(80.0, "Weight", LocalDate.of(2024, 5, 10));
        measurementDAO.addMeasurement(m);

        // Fetch inserted row to get its ID
        int id = measurementDAO.getMeasurements("Weight").get(0).getId();

        assertTrue(measurementDAO.deleteMeasurement(id));

        List<Measurement> list = measurementDAO.getMeasurements("Weight");
        assertTrue(list.isEmpty());
    }

    @Test
    void deleteMeasurement_returnsFalse_whenIdMissing() {
        assertFalse(measurementDAO.deleteMeasurement(99999));
    }

    @Test
    void getMeasurements_filtersByType() {
        measurementDAO.addMeasurement(makeMeasurement(70.0, "Weight", LocalDate.of(2024, 4, 1)));
        measurementDAO.addMeasurement(makeMeasurement(15.0, "BodyFat", LocalDate.of(2024, 4, 2)));
        assertEquals(1, measurementDAO.getMeasurements("Weight").size());
        assertEquals(1, measurementDAO.getMeasurements("BodyFat").size());
    }

    @Test
    void addMeasurement_preservesDateAndValue() {
        LocalDate date = LocalDate.of(2024, 6, 15);
        measurementDAO.addMeasurement(makeMeasurement(82.3, "Weight", date));
        Measurement stored = measurementDAO.getMeasurements("Weight").get(0);
        assertEquals(82.3, stored.getValue());
        assertEquals(date, stored.getDate());
    }
}
