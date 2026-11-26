# Design Document

## Overview

This feature wires the existing `PasswordUtils` BCrypt utility into `UserDAO` and `AdmDAO` so that passwords and admin credentials are always hashed before reaching the database. The change is confined to the DAO layer: the `PasswordUtils` API (`hash` / `verify`) is already in place and no schema changes are required. The two impacted DAOs are `UserDAO` (TB_USER.PASSWORD) and `AdmDAO` (TB_ADMIN.CREDENTIAL).

The key design decision for `AdmDAO` is that BCrypt produces a unique salt per hash, so SQL equality on `CREDENTIAL` becomes useless once values are hashed. The `findByCredential` method must shift from a `WHERE CREDENTIAL=?` SQL filter to an application-layer loop that calls `PasswordUtils.verify()` on each row. A new `findByUserId` convenience method enables looking up a single admin record without touching the credential column, and a `verifyCredential` method encapsulates the full authenticate-by-userId flow.

## Architecture

The change is entirely within the existing DAO layer. No new packages, no service layer, and no model changes are introduced.

```
Caller
  │
  ├─► UserDAO.insertUser(user, plainPassword)
  │       │
  │       └─► PasswordUtils.hash(plainPassword) → hashedPw
  │               │
  │               └─► INSERT TB_USER … PASSWORD = hashedPw
  │
  ├─► UserDAO.updateUser(user, plainPassword)
  │       │  (same hashing path as insertUser)
  │
  ├─► AdmDAO.insertAdmin(adm)
  │       │
  │       ├─► adm.getCredential() → plainCredential
  │       └─► PasswordUtils.hash(plainCredential) → hashedCred
  │               │
  │               └─► INSERT TB_ADMIN … CREDENTIAL = hashedCred
  │
  ├─► AdmDAO.findByUserId(userId)
  │       └─► SELECT … WHERE USER_ID_USER=userId → Adm (with stored hash)
  │
  ├─► AdmDAO.verifyCredential(userId, plainCredential)
  │       │
  │       ├─► findByUserId(userId) → Adm (or absent)
  │       └─► PasswordUtils.verify(plainCredential, storedHash) → boolean
  │
  └─► AdmDAO.findByCredential(plainCredential)
          │
          ├─► SELECT all Adm records (no WHERE on CREDENTIAL)
          └─► for each row: PasswordUtils.verify(plain, stored) → keep if true
```

## Components and Interfaces

### UserDAO changes

| Method | Change |
|--------|--------|
| `insertUser(User user, String password)` | Guard `password != null` (throw `IllegalArgumentException` otherwise); call `PasswordUtils.hash(password)` and bind the result to the `PASSWORD` parameter. |
| `updateUser(User user, String password)` | Same null-guard and hashing pattern as `insertUser`. |
| `findById`, `findByEmail`, `findByName`, `deleteUser` | No change. |

### AdmDAO changes

| Method | Change |
|--------|--------|
| `insertAdmin(Adm adm)` | Guard `adm.getCredential() != null` (throw `IllegalArgumentException` otherwise); call `PasswordUtils.hash(adm.getCredential())` and bind the result to the `CREDENTIAL` parameter. |
| `findByCredential(String credential)` *(existing)* | Remove the `WHERE CREDENTIAL=?` SQL filter; fetch all admin rows; apply `PasswordUtils.verify(credential, row.getCredential())` in the loop; return only matching `Adm` objects. |
| `findByUserId(int userId)` *(new)* | `SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN INNER JOIN TB_USER ON USER_ID_USER=ID_USER WHERE USER_ID_USER=?`; return the first `Adm` (with the stored hash) wrapped in an `Optional<Adm>`, or `Optional.empty()` if absent. |
| `verifyCredential(int userId, String plainCredential)` *(new)* | Calls `findByUserId(userId)`; if absent returns `false`; otherwise returns `PasswordUtils.verify(plainCredential, adm.getCredential())`. |

### PasswordUtils (unchanged)

```java
// com.jogosfx.util.PasswordUtils
public static String hash(String plainPassword)          // BCrypt, work factor 12
public static boolean verify(String plain, String hashed) // BCrypt.checkpw
```

## Data Models

No model class changes are required. The `Adm` constructor already accepts a `credential` string parameter; after this feature, that field holds the BCrypt hash when an `Adm` is reconstructed from a database query.

**TB_USER** (column `PASSWORD VARCHAR`) — no schema change; previously held plain text, will hold BCrypt hash (60 characters, within typical VARCHAR length).

**TB_ADMIN** (column `CREDENTIAL VARCHAR`) — same situation as TB_USER.PASSWORD.

> **Migration note**: Existing rows in `TB_USER` and `TB_ADMIN` that were inserted before this feature will still hold plain-text values. Any authentication flow that relies on `PasswordUtils.verify()` will fail for those rows until they are re-hashed. A data migration script (outside this feature's scope) or a one-time re-hash pass should be planned before deploying to an environment with existing users.

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system — essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Hash then verify round-trip

*For any* non-null plain-text password or credential string, calling `PasswordUtils.hash(plain)` to produce a stored hash and then calling `PasswordUtils.verify(plain, storedHash)` SHALL return `true`.

**Validates: Requirements 1.1, 1.2, 2.1, 2.2, 3.1, 3.2**

### Property 2: Hash irreversibility — wrong input always fails verify

*For any* two distinct non-null strings `a` and `b` where `a ≠ b`, `PasswordUtils.verify(b, PasswordUtils.hash(a))` SHALL return `false`.

**Validates: Requirements 1.1, 2.1, 3.1**

### Property 3: Null password is always rejected before hashing

*For any* call to `UserDAO.insertUser` or `UserDAO.updateUser` with a `null` password argument, the method SHALL throw `IllegalArgumentException` without touching the database.

**Validates: Requirements 1.3, 2.3**

### Property 4: Null credential is always rejected before hashing

*For any* call to `AdmDAO.insertAdmin` with an `Adm` whose `getCredential()` returns `null`, the method SHALL throw `IllegalArgumentException` without touching the database.

**Validates: Requirements 3.3**

### Property 5: verifyCredential returns false for absent admin

*For any* user ID that has no corresponding record in TB_ADMIN, `AdmDAO.verifyCredential(userId, anyCredential)` SHALL return `false` without throwing an exception.

**Validates: Requirements 4.3**

### Property 6: findByCredential returns only matching records

*For any* plain-text credential and any set of admin records stored in the database, `AdmDAO.findByCredential(credential)` SHALL return exactly those `Adm` objects whose stored BCrypt hash matches `credential` via `PasswordUtils.verify()`, and SHALL NOT return any non-matching records.

**Validates: Requirements 4.4, 4.5**

## Error Handling

| Scenario | Handling |
|----------|----------|
| `null` password passed to `UserDAO.insertUser` or `updateUser` | Throw `IllegalArgumentException("Password must not be null")` before any DB call. |
| `null` credential on `adm.getCredential()` in `AdmDAO.insertAdmin` | Throw `IllegalArgumentException("Credential must not be null")` before any DB call. |
| `SQLException` from JDBC | Propagated as-is (unchanged from current behaviour). `try-with-resources` ensures connections are closed. |
| `AdmDAO.verifyCredential` for non-existent userId | `findByUserId` returns `Optional.empty()`; `verifyCredential` returns `false`. |
| `AdmDAO.findByCredential` with no matching rows | Returns an empty `ArrayList` (no exception). |

**Security note**: Exception messages and log output MUST NOT include the plain-text password or credential value. All `IllegalArgumentException` messages describe the validation rule only (e.g. "Password must not be null"), never the actual value.

## Testing Strategy

### Unit tests (JUnit 5, already in pom.xml)

Unit tests use **Mockito** to stub `ConnectionDB.generateConnection()` and the JDBC `PreparedStatement` / `ResultSet`, keeping tests fast and database-free.

Specific examples to cover:

- `UserDAO.insertUser` with a valid password → verify `PreparedStatement.setString(2, hash)` is called with a BCrypt-prefixed value (starts with `$2a$`), NOT with the original plain text.
- `UserDAO.insertUser` with `null` → `IllegalArgumentException` is thrown; no connection is opened.
- `UserDAO.updateUser` mirrors the same two cases.
- `AdmDAO.insertAdmin` with a valid credential → `setString(1, hash)` receives a BCrypt hash.
- `AdmDAO.insertAdmin` with `null` credential → `IllegalArgumentException`.
- `AdmDAO.verifyCredential` with a matching record → returns `true`.
- `AdmDAO.verifyCredential` with a wrong credential → returns `false`.
- `AdmDAO.verifyCredential` with a non-existent userId → returns `false`.
- `AdmDAO.findByCredential` with two records (one matching, one not) → returns only the matching one.

### Property-based tests (jqwik)

The BCrypt utility itself (`PasswordUtils`) has clean, pure-function semantics that make it well-suited for property-based testing. Add `jqwik` as a test-scoped dependency:

```xml
<dependency>
    <groupId>net.jqwik</groupId>
    <artifactId>jqwik</artifactId>
    <version>1.9.3</version>
    <scope>test</scope>
</dependency>
```

Each property test runs a minimum of 100 iterations. Tag each test with a comment referencing the design property.

| Property | Test description |
|----------|-----------------|
| **Property 1** | `@Property` — for any non-null, non-empty string, `verify(plain, hash(plain))` returns `true`. Tag: `Feature: password-encryption-dao, Property 1: Hash then verify round-trip` |
| **Property 2** | `@Property` — for any two distinct strings, `verify(b, hash(a))` returns `false`. Tag: `Feature: password-encryption-dao, Property 2: Hash irreversibility` |
| **Property 3** | `@Property` — `UserDAO.insertUser(user, null)` always throws `IllegalArgumentException` regardless of the `User` object's other fields. Tag: `Feature: password-encryption-dao, Property 3: Null password rejected` |
| **Property 4** | `@Property` — `AdmDAO.insertAdmin(admWithNullCredential)` always throws `IllegalArgumentException`. Tag: `Feature: password-encryption-dao, Property 4: Null credential rejected` |
| **Property 5** | `@Property` — `verifyCredential(nonExistentId, anyString)` always returns `false`. Tag: `Feature: password-encryption-dao, Property 5: verifyCredential false for absent admin` |
| **Property 6** | `@Property` — given a generated list of admin records with known hashes, `findByCredential(plain)` returns exactly the records whose hash matches. Tag: `Feature: password-encryption-dao, Property 6: findByCredential returns only matching records` |
