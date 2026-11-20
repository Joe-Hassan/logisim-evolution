package com.cburch.logisim.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ValueTest {

  static final Value F = Value.FALSE;
  static final Value T = Value.TRUE;
  static final Value U = Value.UNKNOWN;
  static final Value E = Value.ERROR;

  static Stream<Arguments> andTable() {
    return Stream.of(
        Arguments.of(F, F, F),
        Arguments.of(F, T, F),
        Arguments.of(F, U, F),
        Arguments.of(F, E, F),
        Arguments.of(T, F, F),
        Arguments.of(T, T, T),
        Arguments.of(T, U, U),
        Arguments.of(T, E, E),
        Arguments.of(U, F, F),
        Arguments.of(U, T, U),
        Arguments.of(U, U, U),
        Arguments.of(U, E, E),
        Arguments.of(E, F, F),
        Arguments.of(E, T, E),
        Arguments.of(E, U, E),
        Arguments.of(E, E, E));
  }

  static Stream<Arguments> orTable() {
    return Stream.of(
        Arguments.of(F, F, F),
        Arguments.of(F, T, T),
        Arguments.of(F, U, U),
        Arguments.of(F, E, E),
        Arguments.of(T, F, T),
        Arguments.of(T, T, T),
        Arguments.of(T, U, T),
        Arguments.of(T, E, T),
        Arguments.of(U, F, U),
        Arguments.of(U, T, T),
        Arguments.of(U, U, U),
        Arguments.of(U, E, E),
        Arguments.of(E, F, E),
        Arguments.of(E, T, T),
        Arguments.of(E, U, E),
        Arguments.of(E, E, E));
  }

  static Stream<Arguments> xorTable() {
    return Stream.of(
        Arguments.of(F, F, F),
        Arguments.of(F, T, T),
        Arguments.of(F, U, U),
        Arguments.of(F, E, E),
        Arguments.of(T, F, T),
        Arguments.of(T, T, F),
        Arguments.of(T, U, U),
        Arguments.of(T, E, E),
        Arguments.of(U, F, U),
        Arguments.of(U, T, U),
        Arguments.of(U, U, U),
        Arguments.of(U, E, E),
        Arguments.of(E, F, E),
        Arguments.of(E, T, E),
        Arguments.of(E, U, E),
        Arguments.of(E, E, E));
  }

  static Stream<Arguments> notTable() {
    return Stream.of(
        Arguments.of(F, T),
        Arguments.of(T, F),
        Arguments.of(U, U),
        Arguments.of(E, E));
  }

  @ParameterizedTest
  @MethodSource("andTable")
  void and(Value a, Value b, Value expected) {
    assertEquals(expected, a.and(b));
  }

  @ParameterizedTest
  @MethodSource("orTable")
  void or(Value a, Value b, Value expected) {
    assertEquals(expected, a.or(b));
  }

  @ParameterizedTest
  @MethodSource("xorTable")
  void xor(Value a, Value b, Value expected) {
    assertEquals(expected, a.xor(b));
  }

  @ParameterizedTest
  @MethodSource("notTable")
  void not(Value a, Value expected) {
    assertEquals(expected, a.not());
  }
}
