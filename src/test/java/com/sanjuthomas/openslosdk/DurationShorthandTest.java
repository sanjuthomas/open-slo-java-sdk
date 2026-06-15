package com.sanjuthomas.openslosdk;

import com.sanjuthomas.openslo.v2alpha.DurationShorthand;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationShorthandTest {

    @Test
    void parseAndConvertAllUnits() {
        DurationShorthand fiveMinutes = DurationShorthand.parse("5m");
        assertEquals(5, fiveMinutes.getValue());
        assertEquals(DurationShorthand.Unit.MINUTE, fiveMinutes.getUnit());
        assertEquals(Duration.ofMinutes(5), fiveMinutes.toDuration());
        assertEquals(Duration.ofHours(3), DurationShorthand.parse("3h").toDuration());
        assertEquals(Duration.ofDays(2), DurationShorthand.parse("2d").toDuration());
        assertEquals(Duration.ofDays(14), DurationShorthand.parse("2w").toDuration());
    }

    @Test
    void parseEmptyAndInvalidValues() {
        DurationShorthand empty = DurationShorthand.parse("");
        assertEquals("", empty.toString());
        assertThrows(IllegalArgumentException.class, () -> DurationShorthand.parse("10x"));
        assertThrows(IllegalArgumentException.class, () -> DurationShorthand.Unit.fromSymbol("x"));
    }

    @Test
    void fromStringCreatorMatchesParse() {
        assertEquals(
                DurationShorthand.parse("15m").toString(),
                DurationShorthand.fromString("15m").toString());
    }
}
