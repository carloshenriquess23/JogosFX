package com.jogosfx.dao;

import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDAO password-hashing behaviour.
 *
 * Validates: Requirements 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 5.1
 */
class UserDAOTest {

    private static final User SAMPLE_USER = new Client(1, "alice@example.com", "Alice", 0.0);
    private static final String PLAIN_PASSWORD = "mySecret123";

    // -------------------------------------------------------------------------
    // Helper: build a fully-wired JDBC mock chain (Connection → Statement → ResultSet)
    // -------------------------------------------------------------------------

    private static Connection buildMockConnection() throws Exception {
        Connection mockConnection       = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet         = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        return mockConnection;
    }

    // =========================================================================
    // insertUser — valid password
    // =========================================================================

    /**
     * insertUser with a valid password must bind a BCrypt hash (starts with "$2a$")
     * to the PASSWORD column (setString index 2), never the plain-text value.
     *
     * Validates: Requirements 1.1, 1.2, 5.1
     */
    @Test
    void insertUser_validPassword_bindsHashNotPlainText() throws Exception {
        Connection mockConnection = buildMockConnection();
        PreparedStatement mockStatement = mockConnection.prepareStatement("any");

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            UserDAO.insertUser(SAMPLE_USER, PLAIN_PASSWORD);

            // Capture what was bound at parameter index 2 (the PASSWORD column)
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(mockStatement, atLeastOnce()).setString(eq(2), captor.capture());

            String boundValue = captor.getValue();
            assertTrue(boundValue.startsWith("$2a$"),
                    "PASSWORD must be a BCrypt hash (starts with $2a$), got: " + boundValue);
            assertNotEquals(PLAIN_PASSWORD, boundValue,
                    "Plain-text password must not be stored directly");
        }
    }

    // =========================================================================
    // insertUser — null password
    // =========================================================================

    /**
     * insertUser with null must throw IllegalArgumentException before any
     * connection is opened, so the database is never reached.
     *
     * Validates: Requirements 1.3
     */
    @Test
    void insertUser_nullPassword_throwsIllegalArgumentExceptionBeforeOpeningConnection() {
        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            assertThrows(IllegalArgumentException.class,
                    () -> UserDAO.insertUser(SAMPLE_USER, null));

            // The null guard must fire before ConnectionDB.generateConnection() is ever called
            mockedDB.verify(ConnectionDB::generateConnection, never());
        }
    }

    // =========================================================================
    // updateUser — valid password
    // =========================================================================

    /**
     * updateUser with a valid password must bind a BCrypt hash (starts with "$2a$")
     * to the PASSWORD column (setString index 2), never the plain-text value.
     *
     * Validates: Requirements 2.1, 2.2, 5.1
     */
    @Test
    void updateUser_validPassword_bindsHashNotPlainText() throws Exception {
        Connection mockConnection = buildMockConnection();
        PreparedStatement mockStatement = mockConnection.prepareStatement("any");

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            UserDAO.updateUser(SAMPLE_USER, PLAIN_PASSWORD);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(mockStatement, atLeastOnce()).setString(eq(2), captor.capture());

            String boundValue = captor.getValue();
            assertTrue(boundValue.startsWith("$2a$"),
                    "PASSWORD must be a BCrypt hash (starts with $2a$), got: " + boundValue);
            assertNotEquals(PLAIN_PASSWORD, boundValue,
                    "Plain-text password must not be stored directly");
        }
    }

    // =========================================================================
    // updateUser — null password
    // =========================================================================

    /**
     * updateUser with null must throw IllegalArgumentException before any
     * connection is opened.
     *
     * Validates: Requirements 2.3
     */
    @Test
    void updateUser_nullPassword_throwsIllegalArgumentExceptionBeforeOpeningConnection() {
        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            assertThrows(IllegalArgumentException.class,
                    () -> UserDAO.updateUser(SAMPLE_USER, null));

            mockedDB.verify(ConnectionDB::generateConnection, never());
        }
    }
}
