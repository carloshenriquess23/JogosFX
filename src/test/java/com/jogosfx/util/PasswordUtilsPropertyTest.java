package com.jogosfx.util;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for the core PasswordUtils BCrypt utility.
 *
 * Feature: password-encryption-dao
 */
class PasswordUtilsPropertyTest {

    // -------------------------------------------------------------------------
    // Property 1: Hash then verify round-trip
    // Feature: password-encryption-dao, Property 1: Hash then verify round-trip
    // Validates: Requirements 1.1, 1.2, 2.1, 2.2, 3.1, 3.2
    // -------------------------------------------------------------------------

    @Property(tries = 20)
    void hashThenVerify_roundTrip_alwaysTrue(
            @ForAll("nonEmptyString") String plain) {

        // Feature: password-encryption-dao, Property 1: Hash then verify round-trip
        String hash = PasswordUtils.hash(plain);
        assertTrue(PasswordUtils.verify(plain, hash),
                "verify(plain, hash(plain)) must return true for any non-null, non-empty input");
    }

    // -------------------------------------------------------------------------
    // Property 2: Hash irreversibility — wrong input always fails verify
    // Feature: password-encryption-dao, Property 2: Hash irreversibility
    // Validates: Requirements 1.1, 2.1, 3.1
    // -------------------------------------------------------------------------

    @Property(tries = 20)
    void hashIrreversibility_wrongInputAlwaysFails(
            @ForAll("nonEmptyString") String a,
            @ForAll("nonEmptyString") String b) {

        // Feature: password-encryption-dao, Property 2: Hash irreversibility
        Assume.that(!a.equals(b));

        String hashOfA = PasswordUtils.hash(a);
        assertFalse(PasswordUtils.verify(b, hashOfA),
                "verify(b, hash(a)) must return false whenever a != b");
    }

    // -------------------------------------------------------------------------
    // Arbitrary: generate non-null, non-empty strings
    // -------------------------------------------------------------------------

    @Provide
    Arbitrary<String> nonEmptyString() {
        return Arbitraries.strings()
                .withCharRange(' ', '~')   // printable ASCII
                .ofMinLength(1)
                .ofMaxLength(72);           // BCrypt input limit
    }
}
