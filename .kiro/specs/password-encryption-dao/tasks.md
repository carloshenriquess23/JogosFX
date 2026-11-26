# Implementation Plan: Password Encryption in DAO Layer

## Overview

Wire the existing `PasswordUtils` BCrypt utility into `UserDAO` and `AdmDAO` so that passwords and admin credentials are always hashed before reaching the database. Add `jqwik` as a test-scoped dependency, then write JUnit 5 + Mockito unit tests and jqwik property-based tests covering all six correctness properties from the design.

## Tasks

- [x] 1. Add jqwik test dependency to pom.xml
  - Add `net.jqwik:jqwik:1.9.3` with `<scope>test</scope>` to `pom.xml`.
  - _Requirements: (testing infrastructure for Properties 1–6)_

- [x] 2. Harden `UserDAO` with null-guard and password hashing
  - [x] 2.1 Add null guard and `PasswordUtils.hash()` call in `insertUser`
    - Before executing the INSERT, check `if (password == null) throw new IllegalArgumentException("Password must not be null")`.
    - Call `PasswordUtils.hash(password)` and bind the result to the `PASSWORD` column instead of the raw value.
    - _Requirements: 1.1, 1.2, 1.3_
  - [x] 2.2 Add null guard and `PasswordUtils.hash()` call in `updateUser`
    - Apply the identical null guard and hashing pattern as `insertUser`.
    - Bind the BCrypt hash to the `PASSWORD` column in the UPDATE statement.
    - _Requirements: 2.1, 2.2, 2.3_
  - [x] 2.3 Write property tests for `UserDAO` null-password rejection
    - **Property 3: Null password is always rejected before hashing**
    - `@Property` — for any `User` object, `UserDAO.insertUser(user, null)` always throws `IllegalArgumentException`.
    - `@Property` — for any `User` object, `UserDAO.updateUser(user, null)` always throws `IllegalArgumentException`.
    - Tag each test: `Feature: password-encryption-dao, Property 3: Null password rejected`
    - **Validates: Requirements 1.3, 2.3**

- [x] 3. Harden `AdmDAO.insertAdmin` with null-guard and credential hashing
  - [x] 3.1 Add null guard and `PasswordUtils.hash()` call in `insertAdmin`
    - Check `if (adm.getCredential() == null) throw new IllegalArgumentException("Credential must not be null")`.
    - Call `PasswordUtils.hash(adm.getCredential())` and bind the hash to the `CREDENTIAL` column.
    - _Requirements: 3.1, 3.2, 3.3_
  - [x] 3.2 Write property test for `AdmDAO` null-credential rejection
    - **Property 4: Null credential is always rejected before hashing**
    - `@Property` — for any `Adm` whose `getCredential()` returns `null`, `AdmDAO.insertAdmin(adm)` always throws `IllegalArgumentException`.
    - Tag: `Feature: password-encryption-dao, Property 4: Null credential rejected`
    - **Validates: Requirements 3.3**

- [x] 4. Checkpoint — Ensure null-guard tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Refactor `AdmDAO.findByCredential` to application-layer filtering
  - [x] 5.1 Remove `WHERE CREDENTIAL=?` from SQL and add in-loop `PasswordUtils.verify()` filter
    - Change the SQL to `SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN INNER JOIN TB_USER ON USER_ID_USER=ID_USER` (no WHERE on CREDENTIAL).
    - In the result-set loop, call `PasswordUtils.verify(credential, row.getCredential())` and add the `Adm` to the result list only when it returns `true`.
    - _Requirements: 4.4, 4.5, 5.2_
  - [x] 5.2 Write property test for `findByCredential` correctness
    - **Property 6: findByCredential returns only matching records**
    - `@Property` — given a generated list of admin records with known BCrypt hashes, `findByCredential(plain)` returns exactly those whose hash matches.
    - Tag: `Feature: password-encryption-dao, Property 6: findByCredential returns only matching records`
    - **Validates: Requirements 4.4, 4.5**

- [x] 6. Add `findByUserId` and `verifyCredential` to `AdmDAO`
  - [x] 6.1 Implement `findByUserId(int userId)` returning `Optional<Adm>`
    - SQL: `SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN INNER JOIN TB_USER ON USER_ID_USER=ID_USER WHERE USER_ID_USER=?`.
    - Return `Optional.of(adm)` for the first row, or `Optional.empty()` if the result set is empty.
    - _Requirements: 4.1_
  - [x] 6.2 Implement `verifyCredential(int userId, String plainCredential)` returning `boolean`
    - Call `findByUserId(userId)`; if `Optional.empty()`, return `false`.
    - Otherwise return `PasswordUtils.verify(plainCredential, adm.getCredential())`.
    - _Requirements: 4.2, 4.3_
  - [x] 6.3 Write property test for `verifyCredential` with absent admin
    - **Property 5: verifyCredential returns false for absent admin**
    - `@Property` — for any user ID with no corresponding record in TB_ADMIN, `verifyCredential(userId, anyString)` returns `false` without throwing.
    - Tag: `Feature: password-encryption-dao, Property 5: verifyCredential false for absent admin`
    - **Validates: Requirements 4.3**

- [x] 7. Write unit tests (JUnit 5 + Mockito) for all DAO changes
  - [x] 7.1 Write unit tests for `UserDAO` hashing behaviour
    - `insertUser` with valid password → `PreparedStatement.setString(2, hash)` receives a BCrypt-prefixed value (starts with `$2a$`), not the plain text.
    - `insertUser` with `null` → `IllegalArgumentException` is thrown; no connection is opened.
    - `updateUser` mirrors both cases above.
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 5.1_
  - [x] 7.2 Write unit tests for `AdmDAO` hashing and credential methods
    - `insertAdmin` with valid credential → `setString(1, hash)` receives a BCrypt hash.
    - `insertAdmin` with `null` credential → `IllegalArgumentException`.
    - `verifyCredential` with a matching record → returns `true`.
    - `verifyCredential` with a wrong credential → returns `false`.
    - `verifyCredential` with a non-existent userId → returns `false`.
    - `findByCredential` with two records (one matching, one not) → returns only the matching one.
    - _Requirements: 3.1, 3.2, 3.3, 4.1, 4.2, 4.3, 4.4, 4.5, 5.2_

- [x] 8. Write property tests for `PasswordUtils` core properties
  - [x] 8.1 Write property test for hash-then-verify round-trip
    - **Property 1: Hash then verify round-trip**
    - `@Property` — for any non-null, non-empty string, `PasswordUtils.verify(plain, PasswordUtils.hash(plain))` returns `true`.
    - Tag: `Feature: password-encryption-dao, Property 1: Hash then verify round-trip`
    - **Validates: Requirements 1.1, 1.2, 2.1, 2.2, 3.1, 3.2**
  - [x] 8.2 Write property test for hash irreversibility
    - **Property 2: Hash irreversibility — wrong input always fails verify**
    - `@Property` — for any two distinct non-null strings `a` and `b` where `a ≠ b`, `PasswordUtils.verify(b, PasswordUtils.hash(a))` returns `false`.
    - Tag: `Feature: password-encryption-dao, Property 2: Hash irreversibility`
    - **Validates: Requirements 1.1, 2.1, 3.1**

- [x] 9. Final checkpoint — Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP.
- Each task references specific requirements for traceability.
- Mockito is not yet in `pom.xml`; add `org.mockito:mockito-core` (test scope) alongside `jqwik` in task 1 if unit tests (task 7) will be executed.
- Existing rows in `TB_USER` / `TB_ADMIN` inserted before this feature hold plain-text values; a data migration script is outside this feature's scope.
- Property tests for Properties 1 and 2 (task 8) operate on the pure `PasswordUtils` functions and require no DB mocking.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1"] },
    { "id": 1, "tasks": ["2.1", "2.2"] },
    { "id": 2, "tasks": ["2.3", "3.1"] },
    { "id": 3, "tasks": ["3.2"] },
    { "id": 4, "tasks": ["4"] },
    { "id": 5, "tasks": ["5.1", "6.1"] },
    { "id": 6, "tasks": ["5.2", "6.2"] },
    { "id": 7, "tasks": ["6.3"] },
    { "id": 8, "tasks": ["7.1", "7.2"] },
    { "id": 9, "tasks": ["8.1", "8.2"] },
    { "id": 10, "tasks": ["9"] }
  ]
}
```
