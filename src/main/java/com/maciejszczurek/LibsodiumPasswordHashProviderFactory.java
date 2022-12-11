package com.maciejszczurek;

import com.goterl.lazysodium.interfaces.PwHash;
import com.goterl.lazysodium.utils.LibraryLoader;
import com.maciejszczurek.lazysodium.VersionedSodiumJava;
import com.sun.jna.NativeLong;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.keycloak.Config;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class LibsodiumPasswordHashProviderFactory
  implements PasswordHashProviderFactory, ServerInfoAwareProviderFactory {

  private static final String DEFAULT_LIBRARY_PATH =
    "/usr/local/lib/libsodium.so";
  private static final String OPS_LIMIT = "ops-limit";
  private static final String MEM_LIMIT = "mem-limit";
  private Config.Scope config;

  @Override
  public List<ProviderConfigProperty> getConfigMetadata() {
    return ProviderConfigurationBuilder
      .create()
      .property()
      .name("loading-mode")
      .type("string")
      .helpText("Loading mode for Libsodium library.")
      .defaultValue(LibraryLoader.Mode.SYSTEM_ONLY.name())
      .add()
      .property()
      .name("library-path")
      .type("string")
      .helpText("Path to libsodium library.")
      .defaultValue(DEFAULT_LIBRARY_PATH)
      .add()
      .property()
      .name(OPS_LIMIT)
      .type("long")
      .helpText("Maximum amount of computations to perform.")
      .defaultValue(PwHash.OPSLIMIT_INTERACTIVE)
      .add()
      .property()
      .name(MEM_LIMIT)
      .type("long")
      .helpText(
        "Maximum amount of RAM in bytes that the hash function will use."
      )
      .defaultValue(PwHash.MEMLIMIT_INTERACTIVE.longValue())
      .add()
      .build();
  }

  @Override
  public PasswordHashProvider create(final KeycloakSession session) {
    return isSystemOnly()
      ? new LibsodiumPasswordHashProvider(
        getId(),
        getLibraryPath(),
        config.getLong(OPS_LIMIT, PwHash.OPSLIMIT_INTERACTIVE),
        new NativeLong(
          config.getLong(MEM_LIMIT, PwHash.MEMLIMIT_INTERACTIVE.longValue())
        )
      )
      : new LibsodiumPasswordHashProvider(
        getId(),
        getLoadingMode(),
        config.getLong(OPS_LIMIT, PwHash.OPSLIMIT_INTERACTIVE),
        new NativeLong(
          config.getLong(MEM_LIMIT, PwHash.MEMLIMIT_INTERACTIVE.longValue())
        )
      );
  }

  private String getLibraryPath() {
    return config.get("library-path", DEFAULT_LIBRARY_PATH);
  }

  @Override
  public void init(@NotNull final Config.Scope config) {
    this.config = config;
  }

  private boolean isSystemOnly() {
    return getLoadingMode().equals(LibraryLoader.Mode.SYSTEM_ONLY);
  }

  @NotNull
  private LibraryLoader.Mode getLoadingMode() {
    return LibraryLoader.Mode.valueOf(
      config.get("loading-mode", LibraryLoader.Mode.SYSTEM_ONLY.name())
    );
  }

  @Override
  public void postInit(final KeycloakSessionFactory factory) {
    // nothing to post init
  }

  @Override
  public void close() {
    // nothing to close
  }

  @Override
  public String getId() {
    return "sodium";
  }

  @Override
  public Map<String, String> getOperationalInfo() {
    return Map.of(
      "libsodium-version",
      (
        isSystemOnly()
          ? new VersionedSodiumJava(getLibraryPath())
          : new VersionedSodiumJava(getLoadingMode())
      ).sodium_version_string()
    );
  }
}
