# Requirements Document

## Introduction

This feature integrates BCrypt password hashing into the data access layer of the JogosFX application. Currently, `UserDAO` stores user passwords and `AdmDAO` stores admin credentials in plain text. The `PasswordUtils` utility class (BCrypt, work factor 12) already exists but is unused by the DAOs. This feature ensures that all passwords and credentials at rest are hashed before being persisted, and that credential verification for admins shifts from a SQL equality check to an application-layer comparison using `PasswordUtils.verify()`.

## Glossary

- **UserDAO**: Data access object responsible for CRUD operations on the `TB_USER` table.
- **AdmDAO**: Data access object responsible for CRUD operations on the `TB_ADMIN` table.
- **PasswordUtils**: Utility class wrapping BCrypt with a work factor of 12, exposing `hash(String)` and `verify(String, String)` methods.
- **Plain_Password**: A password string as supplied by the caller, before any transformation.
- **Hashed_Password**: The BCrypt hash of a plain password, safe to store in the database.
- **Credential**: The admin-specific secret string stored in `TB_ADMIN.CREDENTIAL`, used to authenticate an admin beyond their user account.
- **TB_USER**: The database table holding user accounts (email, name, password, balance).
- **TB_ADMIN**: The database table holding admin-specific data (credential, foreign key to `TB_USER`).

## Requirements

### Requirement 1: Hash password on user insert

**User Story:** As a developer, I want `UserDAO.insertUser` to store a hashed password, so that plain-text passwords are never persisted in `TB_USER`.

#### Acceptance Criteria

1. WHEN `UserDAO.insertUser(User user, String password)` is called with a non-null `password`, THE `UserDAO` SHALL pass `password` through `PasswordUtils.hash(password)` before executing the INSERT statement.
2. WHEN `UserDAO.insertUser` executes the INSERT statement, THE `UserDAO` SHALL bind the `Hashed_Password` to the `PASSWORD` column of `TB_USER`.
3. IF `password` is null, THEN THE `UserDAO` SHALL throw an `IllegalArgumentException` before attempting the database operation.

---

### Requirement 2: Hash password on user update

**User Story:** As a developer, I want `UserDAO.updateUser` to store a hashed password, so that password changes also persist only hashed values in `TB_USER`.

#### Acceptance Criteria

1. WHEN `UserDAO.updateUser(User user, String password)` is called with a non-null `password`, THE `UserDAO` SHALL pass `password` through `PasswordUtils.hash(password)` before executing the UPDATE statement.
2. WHEN `UserDAO.updateUser` executes the UPDATE statement, THE `UserDAO` SHALL bind the `Hashed_Password` to the `PASSWORD` column of `TB_USER`.
3. IF `password` is null, THEN THE `UserDAO` SHALL throw an `IllegalArgumentException` before attempting the database operation.

---

### Requirement 3: Hash credential on admin insert

**User Story:** As a developer, I want `AdmDAO.insertAdmin` to store a hashed credential, so that admin credentials are never persisted in plain text in `TB_ADMIN`.

#### Acceptance Criteria

1. WHEN `AdmDAO.insertAdmin(Adm adm)` is called, THE `AdmDAO` SHALL retrieve the credential from `adm.getCredential()` and pass it through `PasswordUtils.hash()` before executing the INSERT statement.
2. WHEN `AdmDAO.insertAdmin` executes the INSERT statement, THE `AdmDAO` SHALL bind the `Hashed_Password` to the `CREDENTIAL` column of `TB_ADMIN`.
3. IF `adm.getCredential()` returns null, THEN THE `AdmDAO` SHALL throw an `IllegalArgumentException` before attempting the database operation.

---

### Requirement 4: Application-layer credential verification for admin lookup

**User Story:** As a developer, I want `AdmDAO` to verify admin credentials in the application layer, so that BCrypt hashing remains compatible with credential-based lookups (since BCrypt produces a unique hash per call, SQL equality checks against stored hashes cannot work).

#### Acceptance Criteria

1. THE `AdmDAO` SHALL provide a method `findByUserId(int userId)` that retrieves the admin record by `USER_ID_USER`, returning an `Adm` object that includes the stored `Hashed_Password` from `CREDENTIAL`.
2. WHEN a caller wants to authenticate an admin by credential, THE `AdmDAO` SHALL provide a method `verifyCredential(int userId, String plainCredential)` that fetches the admin record via `findByUserId` and returns `true` if `PasswordUtils.verify(plainCredential, storedHash)` returns `true`, or `false` otherwise.
3. WHEN no admin record exists for the given `userId`, THE `AdmDAO` SHALL return `false` from `verifyCredential` without throwing an exception.
4. THE `AdmDAO` SHALL retain the existing `findByCredential(String credential)` method signature, but update its implementation to fetch all admin records and filter using `PasswordUtils.verify()` in the application layer, returning only the matching `Adm` objects.
5. WHILE iterating over admin records in `findByCredential`, THE `AdmDAO` SHALL compare each stored `CREDENTIAL` hash against the supplied `credential` using `PasswordUtils.verify()` and include only matching records in the result list.

---

### Requirement 5: No plain-text password observable after DAO call

**User Story:** As a security reviewer, I want to confirm that the DAOs never log, return, or propagate the plain-text password, so that passwords cannot leak through the DAO layer.

#### Acceptance Criteria

1. THE `UserDAO` SHALL NOT include the `Plain_Password` parameter value in any log output, exception message, or return value.
2. THE `AdmDAO` SHALL NOT include the plain-text credential value in any log output, exception message, or return value.
