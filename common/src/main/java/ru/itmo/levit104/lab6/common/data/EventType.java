package ru.itmo.levit104.lab6.common.data;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EventType {
    E_SPORTS,
    FOOTBALL,
    BASKETBALL,
    EXPOSITION;

    public static String valuesList() {
        return Arrays.stream(values())
                .map(EventType::toString)
                .collect(Collectors.joining(", "));
    }
}
