package com.maciejszczurek.lazysodium;

import com.goterl.lazysodium.Sodium;
import com.goterl.lazysodium.utils.LibraryLoader;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class VersionedSodiumJava extends Sodium {

  public VersionedSodiumJava(final LibraryLoader.Mode loadingMode) {
    new LibraryLoader(getClassesToRegistry())
      .loadLibrary(loadingMode, "sodium");
    onRegistered();
  }

  public VersionedSodiumJava(final String absolutePath) {
    new LibraryLoader(getClassesToRegistry()).loadAbsolutePath(absolutePath);
    onRegistered();
  }

  @Contract(value = " -> new", pure = true)
  @SuppressWarnings("rawtypes")
  private @NotNull @Unmodifiable List<Class> getClassesToRegistry() {
    return List.of(VersionedSodiumJava.class, Sodium.class);
  }

  @SuppressWarnings("java:S100")
  public native String sodium_version_string();
}
