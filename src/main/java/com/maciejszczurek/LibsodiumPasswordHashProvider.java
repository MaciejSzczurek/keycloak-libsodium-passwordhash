package com.maciejszczurek;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import com.goterl.lazysodium.utils.Base64MessageEncoder;
import com.goterl.lazysodium.utils.LibraryLoader;
import com.sun.jna.NativeLong;
import org.jetbrains.annotations.NotNull;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;

public class LibsodiumPasswordHashProvider implements PasswordHashProvider {

  private final LazySodiumJava lazySodiumJava;
  private final String providerId;
  private final long opsLimit;
  private final NativeLong memLimit;

  public LibsodiumPasswordHashProvider(
    final String providerId,
    final LibraryLoader.Mode loadingMode,
    final long opsLimit,
    final NativeLong memLimit
  ) {
    this.providerId = providerId;
    lazySodiumJava =
      new LazySodiumJava(
        new SodiumJava(loadingMode),
        new Base64MessageEncoder()
      );
    this.opsLimit = opsLimit;
    this.memLimit = memLimit;
  }

  public LibsodiumPasswordHashProvider(
    final String providerId,
    final String libraryPath,
    final long opsLimit,
    final NativeLong memLimit
  ) {
    this.providerId = providerId;
    lazySodiumJava =
      new LazySodiumJava(
        new SodiumJava(libraryPath),
        new Base64MessageEncoder()
      );
    this.opsLimit = opsLimit;
    this.memLimit = memLimit;
  }

  @Override
  public boolean policyCheck(
    @NotNull final PasswordPolicy policy,
    final PasswordCredentialModel credential
  ) {
    return providerId.equals(policy.getHashAlgorithm());
  }

  @Override
  public PasswordCredentialModel encodedCredential(
    final String rawPassword,
    final int iterations
  ) {
    try {
      return PasswordCredentialModel.createFromValues(
        providerId,
        null,
        -1,
        lazySodiumJava.cryptoPwHashStr(rawPassword, opsLimit, memLimit)
      );
    } catch (SodiumException e) {
      throw new LibsodiumPasswordException("Cannot hash password.", e);
    }
  }

  @Override
  public boolean verify(
    final String rawPassword,
    @NotNull final PasswordCredentialModel credential
  ) {
    return lazySodiumJava.cryptoPwHashStrVerify(
      credential.getPasswordSecretData().getValue(),
      rawPassword
    );
  }

  @Override
  public void close() {
    // nothing to close
  }
}
