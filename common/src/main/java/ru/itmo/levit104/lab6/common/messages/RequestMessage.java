package ru.itmo.levit104.lab6.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itmo.levit104.lab6.common.data.Ticket;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class RequestMessage implements Serializable {
    private static final long serialVersionUID = -815194389938723777L;
    private String commandName;
    private String commandArgument;
    private Ticket ticket;
}
