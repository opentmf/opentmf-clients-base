# Changelog

All notable changes to this project are documented in this file.

## [1.1.6] - 2026-03-11

### Added
- CHANGELOG.md with version history; README.md now references it for release notes.

### Changed
- Adds `@Validated` to `TmfClientConfigurations` so that `@NotBlank` on `baseUrl` (and nested `TmfClientConfig`) is enforced at startup.
- Updates Spring Boot to 3.5.11 and various dependency/plugin versions (json-path 3.0.0, Maven Surefire/Failsafe, Testcontainers, Sonar, release and publishing plugins).

## [1.1.5]
- Starts handling "Content-Range: items * / total"
- Updates Spring Boot version to 3.5.8

## [1.1.4]
- Fixes request context headers not being applied to the WebClient for list and delete methods
- Updates Spring Boot version to 3.5.5

## [1.1.3]
- Improves default error handling logic
- Updates Spring Boot version to 3.5.4

## [1.1.2]
- Started handling zero length responses

## [1.1.1]
- Initial open-source version, replacing pia with opentmf

## [1.1.0] (Backward Incompatible)
- Renames the `RetrievalContext` to `TmfRequestContext`
- Moves query parameters from the `TmfClient` interface to `TmfRequestContext`.
- It is now possible to specify return class types for the list methods in the `TmfClient` interface.
- Enabled query parameters for `post`, `patch`, and `delete` methods too.
- Added many `delete` variants to allow `TmfRequestContext` and return class type.

## [1.0.5]
- Changes the types of the GenericClient from String to Object to cover broader use cases
- Updates Spring Boot version to 3.4.3

## [1.0.4]
- Updates to pia-web-clients 1.0.8 for fewer dependencies for the reactive WebClient
- Updates Spring Boot version to 3.4.1

## [1.0.3]
- Updates pia-webclients to version 1.0.6
- Updates documentation

## [1.0.2]
- Updates pia-webclients to version 1.0.5

## [1.0.1]
- Updates pia-webclients to version 1.0.4

## [1.0.0]
- Initial version
