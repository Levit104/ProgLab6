package ru.itmo.levit104.lab6.server;

import lombok.Getter;
import ru.itmo.levit104.lab6.common.data.*;
import ru.itmo.levit104.lab6.common.exceptions.CorruptedDataException;
import ru.itmo.levit104.lab6.common.exceptions.FileFormatException;
import ru.itmo.levit104.lab6.common.exceptions.NoFileException;
import ru.itmo.levit104.lab6.common.exceptions.NotUniqueValueException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileManager {
    @Getter
    private final Map<Integer, Ticket> ticketMap;
    private int lineNumber;
    private boolean noParseErrors;
    public FileManager(String filePath) {
        ticketMap = new HashMap<>();
        lineNumber = 2;
        readFileFrom(filePath);
    }
    public void saveCollection(String file, CollectionManager collectionManager) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            //Что будет если директория, как создастся файл
            fileWriter.write(collectionManager.toString());
            ServerLogger.logger.info("Коллекция успешно сохранена в файл {}", file);
        } catch (FileNotFoundException e) {
            ServerLogger.logger.error("Не хватает прав для записи в файл {}", file);
        } catch (IOException e) {
            ServerLogger.logger.error("Не удалось записать данные в файл {}", file);
        }
    }
    private void readFileFrom(String filePath) {
        try {
            //Если не делать бросится NullPointer
            if (filePath == null) {
                throw new NoFileException();
            }

            if (!correctExtension(filePath)) {
                throw new FileFormatException();
            }

            Reader inputReader = new InputStreamReader(new FileInputStream(filePath));
            StringBuilder stringBuilder = new StringBuilder();

            int symbol;
            while ((symbol = inputReader.read()) != -1) {
                stringBuilder.append((char) symbol);
            }

            Scanner stringScanner = new Scanner(stringBuilder.toString());

            if (!stringScanner.nextLine().equals(CollectionManager.CSV_STRING)) {
                throw new CorruptedDataException();
            }

            parseFileWith(stringScanner);
        } catch (NoFileException e) {
            ServerLogger.logger.error("Не указан путь до файла.");
        } catch (FileFormatException e) {
            if (new File(filePath).isDirectory()) {
                ServerLogger.logger.error("Указанный путь является директорией");
            } else {
                ServerLogger.logger.error("Неверное расширение файла. Файл должен иметь расширение .csv");
            }
        } catch (FileNotFoundException e) {
            if (new File(filePath).isFile()) {
                ServerLogger.logger.error("Не хватает прав для чтения файла");
            } else {
                ServerLogger.logger.error("Файл не найден");
            }
        } catch (NoSuchElementException e) {
            ServerLogger.logger.error("Файл пустой");
        } catch (CorruptedDataException e) {
            ServerLogger.logger.error("Ошибка в заголовке файла/Некорректные данные");
        } catch (IOException e) {
            ServerLogger.logger.error("Ошибка при чтении файла");
        }
    }
    private void parseFileWith(Scanner stringScanner) {
        ServerLogger.logger.info("Файл успешно загружен. Проверка данных");

        while (stringScanner.hasNextLine()) {
            int index = 0;
            String line = stringScanner.nextLine();
            Scanner dataScanner = new Scanner(line);
            dataScanner.useDelimiter(",");
            noParseErrors = true;

            if (line.isEmpty()) {
                ServerLogger.logger.error("Пропуск строки {}", lineNumber);
                lineNumber++;
                continue;
            }
            //Лишние, значит полей больше чем нужно
            if (line.split(",").length != 12) {
                ServerLogger.logger.error("Некорректные данные в строке {}", lineNumber);
                ServerLogger.logger.error("Ошибка: элемент в строке {} не был добавлен", lineNumber);
                lineNumber++;
                continue;
            }
            //Первый символ - запятая, значит поле пустое
            if (line.charAt(0) == ',') {
                ServerLogger.logger.error("Значение поля key в строке {} не может быть пустым", lineNumber);
                ServerLogger.logger.error("Ошибка: элемент в строке {} не был добавлен", lineNumber);
                lineNumber++;
                continue;
            }

            Ticket ticket = new Ticket();
            Coordinates coordinates = new Coordinates();
            Event event = new Event();

            while (dataScanner.hasNext()) {
                String data = dataScanner.next();

                if (index == 0) {
                    ticket.setKey(parseKey(data));
                } else if (index == 1) {
                    ticket.setId(parseID(data));
                } else if (index == 2) {
                    ticket.setName(parseName(data, "name"));
                } else if (index == 3) {
                    coordinates.setX(parseCoordinate(data, "coordinateX"));
                } else if (index == 4) {
                    coordinates.setY(parseCoordinate(data, "coordinateY"));
                } else if (index == 5) {
                    ticket.setCreationDate(parseTicketDate(data));
                } else if (index == 6) {
                    ticket.setPrice(parsePrice(data));
                } else if (index == 7) {
                    ticket.setType(parseTicketType(data));
                } else if (index == 8) {
                    event.setId(parseEventID(data));
                } else if (index == 9) {
                    event.setName(parseName(data, "eventName"));
                } else if (index == 10) {
                    event.setDate(parseEventDate(data));
                } else if (index == 11) {
                    event.setEventType(parseEventType(data));
                }
                ticket.setCoordinates(coordinates);
                ticket.setEvent(event);

                index++;
            }

            if (line.charAt(line.length() - 1) == ',') {
                ServerLogger.logger.error("Значение поля eventType в строке {} не может быть пустым", lineNumber);
                ServerLogger.logger.error("Ошибка: элемент в строке {} не был добавлен", lineNumber);
                lineNumber++;
                continue;
            }

            if (noParseErrors) {
                ticketMap.put(ticket.getKey(), ticket);
                ServerLogger.logger.info("Элемент в строке {} был успешно добавлен", lineNumber);
            } else {
                ServerLogger.logger.error("Ошибка: элемент в строке {} не был добавлен", lineNumber);
            }

            lineNumber++;
        }
        ServerLogger.logger.info("Проверка данных завершена");
    }
    private boolean correctExtension(String file) {
        String extension = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            extension = file.substring(i + 1);
        }
        return extension.equals("csv");
    }
    private Integer parseKey(String data) {
        Integer key = null;
        try {
            key = Integer.parseInt(data);
            if (!CollectionManager.uniqueTicketKey(key, ticketMap)) {
                throw new NotUniqueValueException();
            }
        } catch (NumberFormatException e) {
            ServerLogger.logger.error("Значение поля key в строке {} должно быть числом " +
                    "и не содержать пробелов", lineNumber);
            noParseErrors = false;
        } catch (NotUniqueValueException e) {
            ServerLogger.logger.error("Значение поля key в строке {}, должно быть уникальным", lineNumber);
            noParseErrors = false;
        }
        return key;
    }
    private Integer parseID(String data) {
        Integer ID = null;
        try {
            ID = Integer.parseInt(data);
            if (ID <= 0) {
                throw new NumberFormatException();
            }
            if (!CollectionManager.uniqueTicketID(ID, ticketMap)) {
                throw new NotUniqueValueException();
            }
        } catch (NumberFormatException e) {
            ServerLogger.logger.error("Значение поля ID в строке {} должно быть числом больше нуля " +
                    "и не содержать пробелов", lineNumber);
            noParseErrors = false;
        } catch (NotUniqueValueException e) {
            ServerLogger.logger.error("Значение поля ID в строке {}, должно быть уникальным", lineNumber);
            noParseErrors = false;
        }
        return ID;
    }
    private Long parseEventID(String data) {
        Long eventID = null;
        try {
            eventID = Long.parseLong(data);
            if (eventID <= 0) {
                throw new NumberFormatException();
            }
            if (!CollectionManager.uniqueEventID(eventID, ticketMap)) {
                throw new NotUniqueValueException();
            }
        } catch (NumberFormatException e) {
            ServerLogger.logger.error("Значение поля eventID в строке {} должно быть числом больше нуля " +
                    "и не содержать пробелов", lineNumber);
            noParseErrors = false;
        } catch (NotUniqueValueException e) {
            ServerLogger.logger.error("Значение поля eventID в строке {}, должно быть уникальным", lineNumber);
            noParseErrors = false;
        }
        return eventID;
    }
    private String parseName(String data, String mode) {
        String name = null;
        try {
            if (data.trim().isEmpty()) {
                throw new IllegalArgumentException();
            }
            name = data;
        } catch (IllegalArgumentException e) {
            ServerLogger.logger.error("Значение поля {} в строке {} не может быть пустым " +
                    "и не должно содержать пробелов", mode, lineNumber);
            noParseErrors = false;
        }
        return name;
    }
    private Double parseCoordinate(String data, String mode) {
        Double coordinate = null;
        int maxValue = 0;
        try {
            coordinate = Double.parseDouble(data);
            if (mode.equals("coordinateX") && coordinate > 606) {
                maxValue = 606;
                throw new NumberFormatException();
            } else if (mode.equals("coordinateY") && coordinate > 483) {
                maxValue = 483;
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            ServerLogger.logger.error("Значение поля {} в строке {} должно быть числом не больше {} " +
                    "и не содержать пробелов, " +
                    "в качестве разделителя должна использоваться точка", mode, lineNumber, maxValue);
            noParseErrors = false;
        }
        return coordinate;
    }
    private double parsePrice(String data) {
        double price = 0;
        try {
            price = Double.parseDouble(data);
            if (price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            ServerLogger.logger.error("Значение поля price в строке {} должно быть числом больше нуля " +
                    "и не содержать пробелов " +
                    "в качестве разделителя должна использоваться точка", lineNumber);
            noParseErrors = false;
        }
        return price;
    }
    private Date parseTicketDate(String data) {
        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").parse(data);
        } catch (ParseException e) {
            ServerLogger.logger.error("Значение поля creationDate в строке {} содержит неверный формат даты " +
                    "и/или лишние пробелы", lineNumber);
            noParseErrors = false;
        }
        return date;
    }
    private LocalDateTime parseEventDate(String data) {
        LocalDateTime date = null;
        try {
            date = LocalDateTime.parse(data, DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            ServerLogger.logger.error("Значение поля eventDate в строке {} содержит неверный формат даты " +
                    "и/или лишние пробелы", lineNumber);
            noParseErrors = false;
        }
        return date;
    }
    private TicketType parseTicketType(String data) {
        TicketType ticketType = null;
        try {
            ticketType = TicketType.valueOf(data);
        } catch (IllegalArgumentException e) {
            ServerLogger.logger.error("Значение поля ticketType в строке {} содержит недопустимое значение " +
                    "и/или пробелы", lineNumber);
            noParseErrors = false;
        }
        return ticketType;
    }
    private EventType parseEventType(String data) {
        EventType eventType = null;
        try {
            eventType = EventType.valueOf(data);
        } catch (IllegalArgumentException e) {
            ServerLogger.logger.error("Значение поля eventType в строке {} содержит недопустимое значение " +
                    "и/или пробелы", lineNumber);
            noParseErrors = false;
        }
        return eventType;
    }
}
