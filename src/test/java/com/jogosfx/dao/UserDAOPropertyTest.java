package com.jogosfx.dao;

import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

/**
 * Property-based tests for UserDAO null-password rejection.
 *
 * Feature: password-encryption-dao, Property 3: Null password rejected
 *
 * Validates: Requirements 1.3, 2.3
 */
class UserDAOPropertyTest {

    // -------------------------------------------------------------------------
    // Property 3a: insertUser always rejects a null password
    // -------------------------------------------------------------------------

    @Property
    // Feature: password-encryption-dao, Property 3: Null password rejected
    void insertUser_nullPassword_alwaysThrowsIllegalArgumentException(
            @ForAll("anyUser") User user) {

        try (MockedStatic<ConnectionDB> ignored = mockStatic(ConnectionDB.class)) {
            // ConnectionDB is stubbed so no real DB call can happen; the null guard must fire first.
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> UserDAO.insertUser(user, null));
        }
    }

    // -------------------------------------------------------------------------
    // Property 3b: updateUser always rejects a null password
    // -------------------------------------------------------------------------

    @Property
    // Feature: password-encryption-dao, Property 3: Null password rejected
    void updateUser_nullPassword_alwaysThrowsIllegalArgumentException(
            @ForAll("anyUser") User user) {

        try (MockedStatic<ConnectionDB> ignored = mockStatic(ConnectionDB.class)) {
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> UserDAO.updateUser(user, null));
        }
    }

    // -------------------------------------------------------------------------
    // Arbitrary: generate any User (using concrete subclass Client)
    // -------------------------------------------------------------------------

    @Provide
    Arbitrary<User> anyUser() {
        Arbitrary<Integer> ids     = Arbitraries.integers().between(0, Integer.MAX_VALUE);
        Arbitrary<String>  emails  = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50)
                                        .map(s -> s + "@test.com");
        Arbitrary<String>  names   = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        Arbitrary<Double>  balance = Arbitraries.doubles().between(0.0, 1_000_000.0);

        return Combinators.combine(ids, emails, names, balance)
                .as((id, email, name, bal) -> new Client(id, email, name, bal));
    }
}
