package com.cburch.logisim.analyze.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest {

  // Rows are abc = 000, 001, 010, ... 111 (a is the most significant bit).
  static Stream<Arguments> validExpressions() {
    return Stream.of(
        Arguments.of("~a b", "00110000"),
        Arguments.of("a b ^ c", "01010110"),
        Arguments.of("a ^ b + c", "01111101"),
        Arguments.of("a + b c", "00011111"),
        Arguments.of("a * b + c", "01010111"),
        Arguments.of("a + b · c", "00011111"),
        Arguments.of("!a & b | c", "01110101"),
        Arguments.of("NOT a AND b OR c", "01110101"),
        Arguments.of("(a + b)' c", "01000000"),
        Arguments.of("a XOR b AND c", "00011110"));
  }

  @ParameterizedTest
  @MethodSource("validExpressions")
  void validExpressionParses(String text, String expected) throws ParserException {
    final var model = new AnalyzerModel();
    model.setVariables(
        List.of(new Var("a", 1), new Var("b", 1), new Var("c", 1)), List.of(new Var("y", 1)));

    final var expr = Parser.parse(text, model);

    final var actual = new StringBuilder();
    for (var row = 0; row < 8; row++) {
      final var assignments = new Assignments();
      assignments.put("a", (row & 4) != 0);
      assignments.put("b", (row & 2) != 0);
      assignments.put("c", (row & 1) != 0);
      actual.append(expr.evaluate(assignments) ? '1' : '0');
    }
    assertEquals(expected, actual.toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"(a + b", "+ a", "a + d", "'a"})
  void invalidExpressionThrows(String text) {
    final var model = new AnalyzerModel();
    model.setVariables(
        List.of(new Var("a", 1), new Var("b", 1), new Var("c", 1)), List.of(new Var("y", 1)));

    assertThrows(ParserException.class, () -> Parser.parse(text, model));
  }

  @Test
  void majorityTruthTable() throws ParserException {
    final var model = new AnalyzerModel();
    model.setVariables(
        List.of(new Var("a", 1), new Var("b", 1), new Var("c", 1)), List.of(new Var("y", 1)));
    model.getOutputExpressions().setExpression("y", Parser.parse("a b + a c + b c", model));

    final var column = model.getTruthTable().getOutputColumn(0);
    final var actual = new StringBuilder();
    for (final var entry : column) {
      actual.append(entry == Entry.ONE ? '1' : '0');
    }
    assertEquals("00010111", actual.toString());
  }

  @Test
  void fullAdderSumTruthTable() throws ParserException {
    final var model = new AnalyzerModel();
    model.setVariables(
        List.of(new Var("a", 1), new Var("b", 1), new Var("c", 1)), List.of(new Var("y", 1)));
    model.getOutputExpressions().setExpression("y", Parser.parse("a ^ b ^ c", model));

    final var column = model.getTruthTable().getOutputColumn(0);
    final var actual = new StringBuilder();
    for (final var entry : column) {
      actual.append(entry == Entry.ONE ? '1' : '0');
    }
    assertEquals("01101001", actual.toString());
  }

  @Test
  void multiplexerTruthTable() throws ParserException {
    final var model = new AnalyzerModel();
    model.setVariables(
        List.of(new Var("a", 1), new Var("b", 1), new Var("c", 1)), List.of(new Var("y", 1)));
    model.getOutputExpressions().setExpression("y", Parser.parse("c' a + c b", model));

    final var column = model.getTruthTable().getOutputColumn(0);
    final var actual = new StringBuilder();
    for (final var entry : column) {
      actual.append(entry == Entry.ONE ? '1' : '0');
    }
    assertEquals("00011011", actual.toString());
  }
}
