package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

class UserImageDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private UserImageDAO imageDAO;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        imageDAO = new UserImageDAO(conn);
    }

    @Test
    void saveOrReplaceImage_persistsBytes() {
        byte[] data = {1, 2, 3, 4};
        imageDAO.saveOrReplaceImage(1, data);
        assertArrayEquals(data, imageDAO.getImageByUserId(1).orElseThrow());
    }

    @Test
    void getImageByUserId_returnsEmpty_whenMissing() {
        assertTrue(imageDAO.getImageByUserId(99).isEmpty());
    }

    @Test
    void saveOrReplaceImage_overwritesExisting() {
        byte[] first = {10};
        byte[] second = {20, 30};
        imageDAO.saveOrReplaceImage(2, first);
        imageDAO.saveOrReplaceImage(2, second);
        assertArrayEquals(second, imageDAO.getImageByUserId(2).orElseThrow());
    }

    @Test
    void deleteImage_removesStoredImage() {
        imageDAO.saveOrReplaceImage(3, new byte[] {5});
        imageDAO.deleteImage(3);
        assertTrue(imageDAO.getImageByUserId(3).isEmpty());
    }

    @Test
    void getImageByUserId_isolatesUsers() {
        imageDAO.saveOrReplaceImage(4, new byte[] {1});
        imageDAO.saveOrReplaceImage(5, new byte[] {2});
        assertArrayEquals(new byte[] {1}, imageDAO.getImageByUserId(4).orElseThrow());
        assertArrayEquals(new byte[] {2}, imageDAO.getImageByUserId(5).orElseThrow());
    }

    @Test
    void saveOrReplaceImage_acceptsEmptyArray() {
        byte[] empty = {};
        imageDAO.saveOrReplaceImage(6, empty);
        assertArrayEquals(empty, imageDAO.getImageByUserId(6).orElseThrow());
    }

    @Test
    void deleteImage_doesNotAffectOtherUsers() {
        imageDAO.saveOrReplaceImage(7, new byte[] {7});
        imageDAO.saveOrReplaceImage(8, new byte[] {8});
        imageDAO.deleteImage(7);
        assertTrue(imageDAO.getImageByUserId(7).isEmpty());
        assertTrue(imageDAO.getImageByUserId(8).isPresent());
    }

    @Test
    void saveOrReplaceImage_largePayload_roundTrips() {
        byte[] large = new byte[2048];
        for (int i = 0; i < large.length; i++) {
            large[i] = (byte) (i % 128);
        }
        imageDAO.saveOrReplaceImage(9, large);
        assertArrayEquals(large, imageDAO.getImageByUserId(9).orElseThrow());
    }
}
