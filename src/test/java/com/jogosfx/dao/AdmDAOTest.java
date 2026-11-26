package com.jogosfx.dao;

import com.jogosfx.model.user.Adm;
import com.jogosfx.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AdmDAO} hashing and credential methods.
 *
 * Requirements: 3.1, 3.2, 3.3, 4.1, 4.2, 4.3, 4.4, 4.5, 5.2
 */
class AdmDAOTest {

    // -------------------------------------------------------------------------
    // Case 1: insertAdmin with a valid credential → setString(1, hash) receives a BCrypt hash
    // Requirements: 3.1, 3.2
    // -------------------------------------------------------------------------

    @Test
    void insertAdmin_validCredential_bindsBCryptHashToStatement() throws Exception {
        String plainCredential = "adminSecret123";
        Adm adm = new Adm(42, "admin@test.com", "Admin User", plainCredential);

        Connection mockConnection     = mock(Connection.class);
        PreparedStatement mockStmt    = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            AdmDAO.insertAdmin(adm);

            // setString is called twice: (1, hash) and (2, userId)
            // Capture all setString(int, String) calls to find the credential binding at index 1
            verify(mockStmt, atLeastOnce()).setString(eq(1), stringCaptor.capture());

            String capturedValue = stringCaptor.getValue();
            assertTrue(capturedValue.startsWith("$2a$"),
                    "setString(1, ...) must receive a BCrypt hash starting with '$2a$', got: " + capturedValue);
            assertNotEquals(plainCredential, capturedValue,
                    "Plain credential must not be stored; a hash is expected.");
        }
    }

    // -------------------------------------------------------------------------
    // Case 2: insertAdmin with null credential → IllegalArgumentException
    // Requirements: 3.3
    // -------------------------------------------------------------------------

    @Test
    void insertAdmin_nullCredential_throwsIllegalArgumentException() {
        Adm adm = new Adm(1, "admin@test.com", "Admin User", null);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            // DB is stubbed so any accidental connection attempt would be detected
            assertThrows(IllegalArgumentException.class,
                    () -> AdmDAO.insertAdmin(adm),
                    "insertAdmin must throw IllegalArgumentException when credential is null");

            // The null guard must fire before any DB interaction
            mockedDB.verifyNoInteractions();
        }
    }

    // -------------------------------------------------------------------------
    // Case 3: verifyCredential with a matching record → returns true
    // Requirements: 4.2, 4.3
    // -------------------------------------------------------------------------

    @Test
    void verifyCredential_matchingRecord_returnsTrue() throws Exception {
        String plainCredential  = "correctPassword";
        String storedHash       = PasswordUtils.hash(plainCredential);
        int    userId           = 10;

        Connection        mockConnection = mock(Connection.class);
        PreparedStatement mockStmt       = mock(PreparedStatement.class);
        ResultSet         mockRs         = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("ID_USER")).thenReturn(userId);
        when(mockRs.getString("EMAIL")).thenReturn("admin@test.com");
        when(mockRs.getString("NAME")).thenReturn("Admin User");
        when(mockRs.getString("CREDENTIAL")).thenReturn(storedHash);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            boolean result = AdmDAO.verifyCredential(userId, plainCredential);

            assertTrue(result, "verifyCredential must return true when the credential matches the stored hash");
        }
    }

    // -------------------------------------------------------------------------
    // Case 4: verifyCredential with a wrong credential → returns false
    // Requirements: 4.2, 4.3
    // -------------------------------------------------------------------------

    @Test
    void verifyCredential_wrongCredential_returnsFalse() throws Exception {
        String correctPlain = "correctPassword";
        String storedHash   = PasswordUtils.hash(correctPlain);
        int    userId       = 10;

        Connection        mockConnection = mock(Connection.class);
        PreparedStatement mockStmt       = mock(PreparedStatement.class);
        ResultSet         mockRs         = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("ID_USER")).thenReturn(userId);
        when(mockRs.getString("EMAIL")).thenReturn("admin@test.com");
        when(mockRs.getString("NAME")).thenReturn("Admin User");
        when(mockRs.getString("CREDENTIAL")).thenReturn(storedHash);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            boolean result = AdmDAO.verifyCredential(userId, "wrongPassword");

            assertFalse(result, "verifyCredential must return false when the credential does not match");
        }
    }

    // -------------------------------------------------------------------------
    // Case 5: verifyCredential with a non-existent userId (empty ResultSet) → returns false
    // Requirements: 4.1, 4.3
    // -------------------------------------------------------------------------

    @Test
    void verifyCredential_nonExistentUserId_returnsFalse() throws Exception {
        Connection        mockConnection = mock(Connection.class);
        PreparedStatement mockStmt       = mock(PreparedStatement.class);
        ResultSet         mockRs         = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // empty result set

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            boolean result = AdmDAO.verifyCredential(999, "anyPassword");

            assertFalse(result, "verifyCredential must return false when no admin record exists for the given userId");
        }
    }

    // -------------------------------------------------------------------------
    // Case 6: findByCredential with two records (one matching, one not) → returns only matching
    // Requirements: 4.4, 4.5, 5.2
    // -------------------------------------------------------------------------

    @Test
    void findByCredential_twoRecords_returnsOnlyMatchingOne() throws Exception {
        String targetPlain  = "targetCredential";
        String matchHash    = PasswordUtils.hash(targetPlain);
        String otherHash    = PasswordUtils.hash("otherCredential");

        // Record 1 matches; Record 2 does not
        int    matchingId   = 1;
        int    nonMatchingId = 2;

        Connection        mockConnection = mock(Connection.class);
        PreparedStatement mockStmt       = mock(PreparedStatement.class);
        ResultSet         mockRs         = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Simulate two rows in the ResultSet
        when(mockRs.next()).thenReturn(true, true, false);
        when(mockRs.getInt("ID_USER")).thenReturn(matchingId, nonMatchingId);
        when(mockRs.getString("EMAIL")).thenReturn("match@test.com", "other@test.com");
        when(mockRs.getString("NAME")).thenReturn("Match Admin", "Other Admin");
        when(mockRs.getString("CREDENTIAL")).thenReturn(matchHash, otherHash);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            @SuppressWarnings("unchecked")
            ArrayList<Adm> result = AdmDAO.findByCredential(targetPlain);

            assertEquals(1, result.size(),
                    "findByCredential must return exactly one record when only one hash matches");
            assertEquals(matchingId, result.get(0).getUserId(),
                    "The returned record must be the one whose hash matches the plain credential");
            assertTrue(PasswordUtils.verify(targetPlain, result.get(0).getCredential()),
                    "The returned record's stored hash must verify against the queried credential");
        }
    }
}
