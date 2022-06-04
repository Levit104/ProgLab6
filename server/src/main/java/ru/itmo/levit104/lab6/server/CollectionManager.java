package ru.itmo.levit104.lab6.server;

import lombok.Getter;
import ru.itmo.levit104.lab6.common.data.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    @Getter
    private final Map<Integer, Ticket> collection;
    private final String initDate;
    private final FileManager fileManager;

    public static final String CSV_STRING = "key,id,name,coordinates/x,coordinates/y,creationDate,price," +
            "type,event/id,event/name,event/date,event/eventType";

    public CollectionManager(FileManager fileManager) {
        this.collection = new HashMap<>();
        this.initDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
        this.fileManager = fileManager;
        this.collection.putAll(fileManager.getTicketMap());
    }

    public void add(Integer key, Ticket ticket) {
        collection.put(key, ticket);
    }
    public void replace(Integer key, Ticket oldTicket, Ticket newTicket) {
        collection.replace(key, oldTicket, newTicket);
    }
    public void clearCollection() {
        collection.clear();
    }
    public boolean isEmpty() {
        return collection.isEmpty();
    }
    public Collection<Ticket> getValues() {
        return collection.values();
    }
    public Set<Integer> getKeys() {
        return collection.keySet();
    }
    public Ticket getByKey(Integer key) {
        return collection.get(key);
    }
    public Ticket getByID(Integer id) {
        return collection.values().stream().filter(t -> id.equals(t.getId())).findAny().orElse(null);
    }
    public String filterBy(String name) {
        return collection.values().stream()
                .filter(t -> t.getName().startsWith(name))
                .map(Ticket::toString)
                .collect(Collectors.joining("\n"));
    }
    public String info() {
        return String.format("Информация о коллекции:" +
                        "\nКоллекция типа HashMap, хранящая объекты класса Ticket" +
                        "\nДата инициализации: %s" +
                        "\nКол-во элементов: %d", initDate, collection.size());
    }
    public String ascending() {
        return collection.values().stream()
                .sorted()
                .map(Ticket::toString)
                .collect(Collectors.joining("\n"));
    }
    public String fieldDescending() {
        return collection.values().stream()
                .map(t -> t.getType().toString())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(", "));
    }
    public boolean removeBy(Integer key) {
        return collection.entrySet().removeIf(e -> e.getKey().equals(key));
    }
    public boolean removeLower(Integer key) {
        return collection.entrySet().removeIf(e -> (e.getKey().compareTo(key) < 0));
    }
    protected void saveCollection(String file) {
        fileManager.saveCollection(file, this);
    }
    public String show() {
        return "Все элементы коллекции: \n" + this;
    }
    public static boolean uniqueTicketKey(Integer key, Map<Integer, Ticket> collection) {
        return !collection.containsKey(key);
    }
    public static boolean uniqueTicketID(Integer ID, Map<Integer, Ticket> collection) {
        return collection.values().stream().noneMatch(t -> t.getId().equals(ID));
    }
    public static boolean uniqueEventID(Long ID, Map<Integer, Ticket> collection) {
        return collection.values().stream().noneMatch(t -> t.getEvent().getId().equals(ID));
    }
    @Override
    public String toString() {
        return collection.values().stream().map(Ticket::toString).collect(Collectors.joining("\n"));
    }
}
