package com.MagicTheGathering.Exceptions;

public class UnauthorizedAccessException extends RuntimeException {
  public UnauthorizedAccessException() {
    super("You are not authorized to access");
  }}
