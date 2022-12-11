# Libsodium Password Hash Provider for Keycloak

Libsodium Password Hash Provider for Keycloak is a library that allows you to hash the passwords of Keycloak application
users using the Libsodium library and the best proposed algorithm for hashing passwords.

## Installation

As for any provider to Keycloak, you need to put the JAR file in the `providers` directory, and then run the `build`
command.

```bash
bin/kc.[sh|bat] build
```

To use the provider, add the parameter `--spi-password-hashing-provider=sodium` in the build parameters, or add `sodium`
as the password hashing algorithm in the authentication policy settings for the realm.

## Usage

By default, the library does not require changing additional settings. If you want to change the configuration options
when starting Keycloak, add additional parameters to the `start` command or set environment variables, such
as `KC_SPI_PASSWORD_HASHING_SODIUM_OPS_LIMIT`.

E.g.

```bash
bin/kc.[sh|bat] start --spi-password-hashing-sodium-ops-limit=3
```

### Configuration parameters

| Parameter      | Description                                                                                                       | Default                       |
| -------------- | ----------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| `loading-mode` | Loading mode for Libsodium library (`PREFER_SYSTEM`, `PREFER_BUNDLED`, `BUNDLED_ONLY`, `SYSTEM_ONLY`).            | `SYSTEM_ONLY`                 |
| `library-path` | Path to libsodium library. When `SYSTEM_ONLY` library mode loading is selected, this setting is mandatory.        | `/usr/local/lib/libsodium.so` |
| `ops-limit`    | Maximum amount of computations to perform. (Default is `OPSLIMIT_INTERACTIVE`)                                    | `2`                           |
| `mem-limit`    | Maximum amount of RAM in bytes that the hash function will use. (Default is `MEMLIMIT_INTERACTIVE` that is 67 MB) | `67108864`                    |

### Additional reading

- https://www.keycloak.org/server/configuration-provider
- https://www.keycloak.org/server/configuration
- https://libsodium.gitbook.io/doc/password_hashing/default_phf
- https://github.com/terl/lazysodium-java/blob/e6c193b7559433398c976fdb9977bc4d0c9fc6be/src/main/java/com/goterl/lazysodium/utils/LibraryLoader.java#L38

## Important

Password hash provider is a private service and along with an upgrade of the major version of Keycloak, could lead to
incompatibility. Before running and installing the library in a new version of Keycloak, check that the current version
of the library is compatible. I usually do my best to get the update as soon as possible.

## Contributing

Pull requests are always welcome.

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
