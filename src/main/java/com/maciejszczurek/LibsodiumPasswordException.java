package com.maciejszczurek;

public class LibsodiumPasswordException extends RuntimeException {

  public LibsodiumPasswordException(
    final String message,
    final Throwable exception
  ) {
    super(message, exception);
  }
}
