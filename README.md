# Identus Keycloak Plugins

![CI](https://github.com/hyperledger/identus-keycloak-plugins/actions/workflows/unit-tests.yml/badge.svg)

## Overview

This repository provides a Keycloak Plugin that extends Keycloak's functionality to handle Self-Sovereign Identity (SSI) tasks, including OpenID for verifiable credential issuance.
The plugin is designed to integrate with the Identus cloud agent, enabling a comprehensive SSI ecosystem.

## Getting started

### Using published docker image

__Default Keycloak plugin image__

The plugin is available as a pre-bundled Docker image.
This image includes Keycloak and the plugin enabled for basic use cases.
For more complex setup, the JAR file published in the Maven repository should be used to build a custom Keycloak image.
The docker-compose configuration below allows spinning up a basic Keycloak instance with the plugin enabled as part of the idendus cloud agent stack.

```yaml
services:
  keycloak-oid4vci-issuer:
    image: ghcr.io/hyperledger/identus-keycloak-plugins:0.1.0
    ports:
      - "9980:8080"
    command:
      - start-dev
      - --features=preview
      - --health-enabled=true
      - --hostname-url=http://localhost:9980
      - --hostname-admin-url=http://localhost:9980
    environment:
      IDENTUS_URL: <AGENT_URL> # point to cloud agent instance
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    restart: always

  # Identus cloud agent and other services below
```

__Customized Keycloak plugin image__

In many cases, Keycloak customization is necessary for branding or to meet custom
authentication and authorization requirements. The default plugin image can be used
in conjunction with docker multi-stage builds to extract the Keycloak plugin JARs and
create a customized Keycloak image.

The image contains plugin JARs in `/opt/keycloak/providers` directory

For example

```
FROM ghcr.io/hyperledger/identus-keycloak-plugins:0.1.0 AS dist

FROM quay.io/keycloak/keycloak:23.0.7
COPY --from=dist /opt/keycloak/providers/<PLUGIN_FILE>.jar /opt/keycloak/providers/<PLUGIN_FILE>.jar

# customized and copy assets / provideres
COPY ./

RUN /opt/keycloak/bin/kc.sh build
ENTRYPOINT [ "/opt/keycloak/bin/kc.sh" ]
```


### Using published JAR

TODO


## Available Plugins

### `identus-keycloak-oid4vci`

Extends Keycloak for integration with Identus cloud agent in [OID4VCI](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html) flow.
The Keycloak instance to use this is the Issuer Authorization Server where the plugin takes care of
the OpenID extension in the issuance flow.
The plugin supports the Authorization Endpoint and Token Endpoint according to the specification.
Additionally, the plugin communicates with the cloud agent during holder authorization to coordinate the issuance session.

#### Features

- [Authorization code flow](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-authorization-code-flow)
  - _AuthorizationRequest_ supported parameters
    - [`issuer_state`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-5.1.3-2.3)
  - _TokenResponse_ supported parameters
    - [`c_nonce`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-6.2-4.1)
    - [`c_nonce_expires_in`](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-6.2-4.2)
- [Pre-authorized-code flow](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-pre-authorized-code-flow)
  - Not yet supported

#### Environment Variables

|Name|Description|
|-|-|
|`IDENTUS_URL`|URL of the Identus cloud agent to coordinate the issuance session.|
