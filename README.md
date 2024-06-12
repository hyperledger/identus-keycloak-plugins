# Identus Keycloak Plugins

![CI](https://github.com/hyperledger/identus-keycloak-plugins/actions/workflows/unit-tests.yml/badge.svg)

## Overview

This repository provides a Keycloak Plugin that extends Keycloak's functionality to handle Self-Sovereign Identity (SSI) tasks, including OpenID for verifiable credential issuance.
The plugin is designed to integrate with the Identus cloud agent, enabling a comprehensive SSI ecosystem.

## Available Plugins

### `identus-keycloak-oid4vci`

Extends Keycloak for integration with Identus cloud agent in [OID4VCI](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html) flow.

#### Features

- Authorization code flow
  - _AuthorizationRequest_ supported parameters
    - [`issuer_state`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-5.1.3-2.3)
  - _TokenResponse_ supported parameters
    - [`c_nonce`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-6.2-4.1)
    - [`c_nonce_expires_in`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-6.2-4.2)
- Pre-authorized-code flow
  - Not yet supported

## Getting started

### Using published docker image

TODO

### Using published JAR

TODO
