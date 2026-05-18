package bme.oop;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

import bme.oop.Displays.Display;

class DisplayTest {

    @Test
    void calculateDigitsFormatsSingleDigitNumber() {
        assertArrayEquals(new int[] {0, 0, 7}, Display.calculateDigits(7));
    }

    @Test
    void calculateDigitsFormatsTwoDigitNumber() {
        assertArrayEquals(new int[] {0, 4, 2}, Display.calculateDigits(42));
    }

    @Test
    void calculateDigitsFormatsThreeDigitNumber() {
        assertArrayEquals(new int[] {1, 2, 3}, Display.calculateDigits(123));
    }

    @Test
    void calculateDigitsClampsNegativeNumberToZero() {
        assertArrayEquals(new int[] {0, 0, 0}, Display.calculateDigits(-5));
    }

    @Test
    void calculateDigitsClampsTooLargeNumberTo999() {
        assertArrayEquals(new int[] {9, 9, 9}, Display.calculateDigits(1200));
    }
}