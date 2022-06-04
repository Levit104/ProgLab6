package ru.itmo.levit104.lab6.client;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.levit104.lab6.common.data.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Getter
@Setter
public class ConsoleManager {
    private Scanner scanner;
    private boolean inScript;
    private String commandName, commandArgument;
    public ConsoleManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public Ticket createTicket(String keyString) {
        Ticket ticket = null;
        try {
            Integer key = Integer.parseInt(keyString);
            ticket = new Ticket(key, askName(),
                    new Coordinates(askCoordinateX(), askCoordinateY()),
                    askPrice(), askTicketType(),
                    new Event(askEventName(), askEventTime(), askEventType())
            );
        } catch (NumberFormatException ignore) {
            //Обработка на сервере
        }
        return ticket;
    }

    public void startInput() {
        String[] commandInput;
        do {
            System.out.print("Введите команду: ");
            commandInput = scanner.nextLine().split("\\s+", 2);
            commandName = commandInput[0];
            commandArgument = (commandInput.length > 1) ? commandInput[1] : "";
        } while (commandInput[0].isEmpty());
    }

    private String askName() {
        while (true) {
            if (!inScript) {
                System.out.print("Введите название билета: ");
            }
            String ticketName = scanner.nextLine();
            if (ticketName.trim().isEmpty()) {
                System.out.println("Значение названия билета не может быть пустым и не должно содержать пробелов");
                if (inScript) {
                    return null;
                }
            } else {
                return ticketName;
            }
        }
    }
    private Double askCoordinateX() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.print("Введите координату X (в качестве разделителя используется точка, " +
                            "максимальное значение - 606): ");
                }
                double ticketCoordinateX = Double.parseDouble(scanner.nextLine());
                if (ticketCoordinateX > 606) {
                    throw new NumberFormatException();
                } else {
                    return ticketCoordinateX;
                }
            } catch (NumberFormatException e) {
                System.out.println("Значение координаты X должно быть числом не больше 606 и не содержать пробелов, " +
                        "в качестве разделителя используется точка");
                if (inScript) {
                    return null;
                }
            }
        }
    }
    private Double askCoordinateY() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.print("Введите координату Y (в качестве разделителя используется точка, " +
                            "максимальное значение - 483): ");
                }
                double ticketCoordinateY = Double.parseDouble(scanner.nextLine());
                if (ticketCoordinateY > 483) {
                    throw new NumberFormatException();
                } else {
                    return ticketCoordinateY;
                }
            } catch (NumberFormatException e) {
                System.out.println("Значение координаты X должно быть числом не больше 483 и не содержать пробелов, " +
                        "в качестве разделителя используется точка");
                if (inScript) {
                    return null;
                }
            }
        }
    }
    private double askPrice() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.print("Введите цену билета (в качестве разделителя используется точка, " +
                            "значение должно быть больше нуля): ");
                }
                double ticketPrice = Double.parseDouble(scanner.nextLine());
                if (ticketPrice <= 0) {
                    throw new NumberFormatException();
                } else {
                    return ticketPrice;
                }
            } catch (NumberFormatException e) {
                System.out.println("Значение цены должно быть числом больше нуля и не содержать пробелов, " +
                        "в качестве разделителя используется точка");
                if (inScript) {
                    return 0;
                }
            }
        }
    }
    private TicketType askTicketType() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.println("Возможные варианты: " + TicketType.valuesList());
                    System.out.print("Введите тип билета: ");
                }
                return TicketType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Значение \"тип билета\" должно соответствовать одному из допустимых типов " +
                        "и не содержать пробелов");
                if (inScript) {
                    return null;
                }
            }
        }
    }
    private String askEventName() {
        while (true) {
            if (!inScript) {
                System.out.print("Введите название события: ");
            }
            String eventName = scanner.nextLine();
            if (eventName.trim().isEmpty()) {
                System.out.println("Значение названия события не может быть пустым и не должно содержать пробелов");
                if (inScript) {
                    return null;
                }
            } else {
                return eventName;
            }
        }
    }
    private LocalDateTime askEventTime() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.print("Введите дату в формате ЧЧ:ММ:СС ДД.ММ.ГГГГ: ");
                }
                return LocalDateTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Значение даты должно строго соответствовать формату даты и не содержать пробелов");
                if (inScript) {
                    return null;
                }
            }
        }
    }
    private EventType askEventType() {
        while (true) {
            try {
                if (!inScript) {
                    System.out.println("Возможные варианты: " + EventType.valuesList());
                    System.out.print("Введите тип события: ");
                }
                return EventType.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Значение \"тип события\" должно соответствовать одному из допустимых типов " +
                        "и не содержать пробелов");
                if (inScript) {
                    return null;
                }
            }
        }
    }
}
