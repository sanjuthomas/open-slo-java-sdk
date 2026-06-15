package com.sanjuthomas.openslo.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sanjuthomas.openslo.validation.ValidationException;
import com.sanjuthomas.openslo.validation.ValidatorError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationShorthand {
    private static final Pattern PATTERN = Pattern.compile("^([0-9]+)([mhdwMQY])$");

    public enum Unit {
        MINUTE("m"),
        HOUR("h"),
        DAY("d"),
        WEEK("w"),
        MONTH("M"),
        QUARTER("Q"),
        YEAR("Y");

        private final String symbol;

        Unit(String symbol) {
            this.symbol = symbol;
        }

        @JsonCreator
        public static Unit fromSymbol(String symbol) {
            for (Unit unit : values()) {
                if (unit.symbol.equals(symbol)) {
                    return unit;
                }
            }
            throw new IllegalArgumentException("invalid duration unit: " + symbol);
        }

        @JsonValue
        public String getSymbol() {
            return symbol;
        }
    }

    private int value;
    private Unit unit;

    public DurationShorthand() {}

    public DurationShorthand(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public static DurationShorthand parse(String text) {
        if (text == null || text.isEmpty()) {
            return new DurationShorthand(0, null);
        }
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "invalid duration shorthand: " + text + ", expected [0-9]+[mhdwMQY]");
        }
        return new DurationShorthand(Integer.parseInt(matcher.group(1)), Unit.fromSymbol(matcher.group(2)));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @JsonValue
    @Override
    public String toString() {
        if (value == 0) {
            return "";
        }
        return value + unit.getSymbol();
    }

    @JsonCreator
    public static DurationShorthand fromString(String text) {
        return parse(text);
    }

    public Duration toDuration() {
        return switch (unit) {
            case MINUTE -> Duration.ofMinutes(value);
            case HOUR -> Duration.ofHours(value);
            case DAY -> Duration.ofDays(value);
            case WEEK -> Duration.ofDays(value * 7L);
            case MONTH -> Duration.ofDays(value * 30L);
            case QUARTER -> Duration.ofDays(value * 90L);
            case YEAR -> Duration.ofDays(value * 365L);
            default -> throw new IllegalStateException("invalid unit");
        };
    }

    public void validate(String path, List<ValidatorError> errors) {
        if (unit == null) {
            errors.add(new com.sanjuthomas.openslo.validation.ValidatorError("'" + path + ".unit' is required")
                    .withPropertyPath(path + ".unit"));
        } else {
            com.sanjuthomas.openslo.validation.Rules.oneOf(
                    path + ".unit", unit, List.of(Unit.values()), errors);
        }
        com.sanjuthomas.openslo.validation.Rules.gte(path + ".value", value, 0, errors);
    }
}
