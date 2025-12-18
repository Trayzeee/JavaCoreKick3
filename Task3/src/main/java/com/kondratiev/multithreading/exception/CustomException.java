package com.kondratiev.multithreading.exception;

public class CustomException extends Exception {

  public CustomException (String message, Throwable cause) {
      super(message, cause);
  }

  public CustomException (String message) {
    super(message);
  }

  public CustomException (Throwable cause) {
    super(cause);
  }

  public CustomException () {
  }
}
