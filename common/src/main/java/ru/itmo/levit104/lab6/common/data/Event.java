package ru.itmo.levit104.lab6.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class Event implements Serializable {
    private static final long serialVersionUID = -1316017101341459452L;
    private static long uniqueID = 1L;
    private Long id;
    private String name;
    private LocalDateTime date;
    private EventType eventType;

    public Event(String name, LocalDateTime date, EventType eventType) {
        this.id = uniqueID;
        uniqueID++;
        this.name = name;
        this.date = date;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", id, name, date.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")), eventType);
    }
}
