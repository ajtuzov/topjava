package ru.javawebinar.topjava.util;

import org.springframework.lang.Nullable;

import static java.lang.String.format;

public class Util {
    private Util() {
    }

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0) && (end == null || value.compareTo(end) < 0);
    }

    public static String addRightPadding(String string, int length) {
        return format("%-" + length + "s", string);
    }
}