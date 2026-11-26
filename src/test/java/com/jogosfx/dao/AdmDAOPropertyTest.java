package com.jogosfx.dao;

import com.jogosfx.model.user.Adm;
import com.jogosfx.util.PasswordUtils;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for AdmDAO.
 *
 * Feature: password-encryption-dao, Property 4: Null credential rejected
 * Feature: password-encryption-dao, Property 5: verifyCredential false for absent admin
 * Feature: password-encryption-dao, Property 6: findByCredential returns only matching records
 *
 * Validates: Requirements 3.3, 4.3, 4.4, 4.5
 */
class AdmDAOPropertyTest {

    // -------------------------------------------------------------------------
    // Property 4: insertAdmin always rejects a null credential
    // -------------------------------------------------------------------------

    @Property
    // Feature: password-encryption-dao, Property 4: Null credential rejected
    void insertAdmin_nullCredential_alwaysThrowsIllegalArgumentException(
            @ForAll("anyAdmWithNullCredential") Adm adm) {

        try (MockedStatic<ConnectionDB> ignored = mockStatic(ConnectionDB.class)) {
            // ConnectionDB is stubbed so no real DB call can happen; the null guard must fire first.
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> AdmDAO.insertAdmin(adm));
        }
    }

    // -------------------------------------------------------------------------
    // Arbitrary: generate any Adm with null credential
    // -------------------------------------------------------------------------

    @Provide
    Arbitrary<Adm> anyAdmWithNullCredential() {
        Arbitrary<Integer> ids    = Arbitraries.integers().between(0, Integer.MAX_VALUE);
        Arbitrary<String>  emails = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50)
                                       .map(s -> s + "@test.com");
        Arbitrary<String>  names  = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);

        return Combinators.combine(ids, emails, names)
                .as((id, email, name) -> new Adm(id, email, name, null));
    }

    // -------------------------------------------------------------------------
    // Property 5: verifyCredential returns false for absent admin
    // -------------------------------------------------------------------------

    // Feature: password-encryption-dao, Property 5: verifyCredential false for absent admin
    // Validates: Requirements 4.3
    @Property
    void verifyCredential_absentAdmin_alwaysReturnsFalse(
            @ForAll("anyPositiveUserId") int userId,
            @ForAll("anyPlainCredential") String plainCredential) throws SQLException {

        Connection mockConnection       = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet         = mock(ResultSet.class);

        // Empty result set — no admin row found for this userId
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            // findByUserId opens its own connection; this stub intercepts that call
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            boolean result = AdmDAO.verifyCredential(userId, plainCredential);

            Assertions.assertFalse(result,
                    "verifyCredential must return false when no admin record exists for userId=" + userId);
        }
    }

    @Provide
    Arbitrary<Integer> anyPositiveUserId() {
        return Arbitraries.integers().between(1, Integer.MAX_VALUE);
    }

    @Provide
    Arbitrary<String> anyPlainCredential() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
    }

    // -------------------------------------------------------------------------
    // Property 6: findByCredential returns only matching records
    // -------------------------------------------------------------------------

    // Feature: password-encryption-dao, Property 6: findByCredential returns only matching records
    @Property(tries = 10)
    void findByCredential_returnsExactlyMatchingRecords(
            @ForAll("admListWithSomePrehashed") AdmListScenario scenario) throws SQLException {

        Connection mockConnection         = mock(Connection.class);
        PreparedStatement mockStatement   = mock(PreparedStatement.class);
        ResultSet mockResultSet           = mock(ResultSet.class);

        // Wire up the JDBC mock chain
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Build a ResultSet that iterates over the generated Adm records
        List<Adm> rows = scenario.allRecords;
        int[] cursor = {-1};
        when(mockResultSet.next()).thenAnswer(inv -> {
            cursor[0]++;
            return cursor[0] < rows.size();
        });
        when(mockResultSet.getInt("ID_USER")).thenAnswer(inv -> rows.get(cursor[0]).getUserId());
        when(mockResultSet.getString("EMAIL")).thenAnswer(inv -> rows.get(cursor[0]).getEmail());
        when(mockResultSet.getString("NAME")).thenAnswer(inv -> rows.get(cursor[0]).getName());
        when(mockResultSet.getString("CREDENTIAL")).thenAnswer(inv -> rows.get(cursor[0]).getCredential());

        try (MockedStatic<ConnectionDB> mockedDB = mockStatic(ConnectionDB.class)) {
            mockedDB.when(ConnectionDB::generateConnection).thenReturn(mockConnection);

            @SuppressWarnings("unchecked")
            ArrayList<Adm> result = AdmDAO.findByCredential(scenario.plainCredential);

            // Every returned Adm must have a hash that matches the plain credential
            for (Adm returned : result) {
                Assertions.assertTrue(
                        PasswordUtils.verify(scenario.plainCredential, returned.getCredential()),
                        "Returned Adm has a hash that does NOT match the queried credential");
            }

            // Every Adm in the input whose hash matches must appear in the result
            for (Adm row : rows) {
                boolean shouldMatch = PasswordUtils.verify(scenario.plainCredential, row.getCredential());
                boolean isPresent   = result.stream().anyMatch(r -> r.getUserId() == row.getUserId());
                Assertions.assertEquals(
                        shouldMatch,
                        isPresent,
                        "Adm with userId=" + row.getUserId() +
                        " shouldMatch=" + shouldMatch + " but isPresent=" + isPresent);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Scenario holder
    // -------------------------------------------------------------------------

    static class AdmListScenario {
        final String    plainCredential;
        final List<Adm> allRecords;

        AdmListScenario(String plainCredential, List<Adm> allRecords) {
            this.plainCredential = plainCredential;
            this.allRecords      = allRecords;
        }

        @Override
        public String toString() {
            return "credential='" + plainCredential + "', records=" + allRecords.size();
        }
    }

    // -------------------------------------------------------------------------
    // Arbitrary: generate a plain credential + a list of Adm rows where
    // each row is independently assigned either a hash of the target credential
    // or a hash of a different credential.
    // -------------------------------------------------------------------------

    @Provide
    Arbitrary<AdmListScenario> admListWithSomePrehashed() {
        // Target plain credential (4-20 alpha chars)
        Arbitrary<String> credentialArb = Arbitraries.strings().alpha()
                .ofMinLength(4).ofMaxLength(20);

        // A different credential that is guaranteed to differ (we append a suffix to be safe)
        Arbitrary<String> otherCredentialArb = Arbitraries.strings().alpha()
                .ofMinLength(4).ofMaxLength(16)
                .map(s -> s + "1");   // guarantees non-empty; digits not in alpha set

        // Row count: keep small so BCrypt stays fast (BCrypt is expensive)
        Arbitrary<Integer> listSizeArb = Arbitraries.integers().between(1, 3);

        return Combinators.combine(credentialArb, otherCredentialArb, listSizeArb)
                .flatAs((plainCred, otherCred, size) -> {
                    // Build a single-row arbitrary that generates (Adm, shouldMatch) based on a boolean flag
                    List<Arbitrary<Adm>> rowArbs = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        final int rowId = i + 1;
                        rowArbs.add(buildRowArbitrary(rowId, plainCred, otherCred));
                    }

                    // Collect all row arbitraries; since they are independent, combine them sequentially
                    // using reduce over flatMap so the list length is dynamic.
                    return buildListArbitrary(rowArbs)
                            .map(rows -> new AdmListScenario(plainCred, rows));
                });
    }

    /**
     * Builds one row arbitrary: randomly picks whether the stored hash matches
     * {@code plainCred} or {@code otherCred}.
     */
    private Arbitrary<Adm> buildRowArbitrary(int id, String plainCred, String otherCred) {
        Arbitrary<String> nameArb      = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
        Arbitrary<String> emailLocalArb = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(15);
        Arbitrary<Boolean> matchArb    = Arbitraries.of(true, false);

        return Combinators.combine(nameArb, emailLocalArb, matchArb)
                .as((name, emailLocal, shouldMatch) -> {
                    String hash = shouldMatch
                            ? PasswordUtils.hash(plainCred)
                            : PasswordUtils.hash(otherCred);
                    return new Adm(id, emailLocal + "@test.com", name, hash);
                });
    }

    /**
     * Combines a list of row arbitraries into an {@code Arbitrary<List<Adm>>}
     * by folding with flatMap.
     */
    private Arbitrary<List<Adm>> buildListArbitrary(List<Arbitrary<Adm>> rowArbs) {
        Arbitrary<List<Adm>> acc = Arbitraries.just(new ArrayList<>());
        for (Arbitrary<Adm> rowArb : rowArbs) {
            acc = acc.flatMap(list -> rowArb.map(adm -> {
                List<Adm> next = new ArrayList<>(list);
                next.add(adm);
                return next;
            }));
        }
        return acc;
    }
}
