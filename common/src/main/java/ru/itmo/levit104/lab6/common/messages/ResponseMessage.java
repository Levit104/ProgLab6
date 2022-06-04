package ru.itmo.levit104.lab6.common.messages;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseMessage implements Serializable {
    private static final long serialVersionUID = -4476349750750215299L;
    private String message;
    private MessageStatus status;
}
