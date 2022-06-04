package ru.itmo.levit104.lab6.common.exceptions;

public class NotUniqueValueException extends Exception {
    public NotUniqueValueException() {
    }
    public NotUniqueValueException(String message) {
        super(message);
    }
}
