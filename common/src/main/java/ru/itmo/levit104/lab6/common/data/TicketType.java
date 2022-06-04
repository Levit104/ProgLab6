package ru.itmo.levit104.lab6.common.data;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TicketType {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;

    public static String valuesList() {
        return Arrays.stream(values())
                .map(TicketType::toString)
                .collect(Collectors.joining(", "));
    }
}
