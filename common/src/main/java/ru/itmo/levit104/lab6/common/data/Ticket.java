package ru.itmo.levit104.lab6.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Ticket implements Comparable<Ticket>, Serializable {
    private static final long serialVersionUID = -8223591582968681221L;
    private static int uniqueID = 1;
    private Integer key;
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private double price;
    private TicketType type;
    private Event event;

    public Ticket(Integer key, String name, Coordinates coordinates, double price, TicketType type, Event event) {
        this.key = key;
        this.id = uniqueID;
        uniqueID++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date(System.currentTimeMillis());
        this.price = price;
        this.type = type;
        this.event = event;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                key, id, name, coordinates, new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(creationDate),
                price, type, event);
    }

    @Override
    public int compareTo(Ticket ticket) {
        return Double.compare(this.price, ticket.price);
    }
}
