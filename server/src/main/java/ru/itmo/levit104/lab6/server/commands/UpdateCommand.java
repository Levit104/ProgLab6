package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Команда, заменяющая элемент по ID
 */

public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "update";
    }
    @Override
    public String getDescription() {
        return " <id> : обновить значение элемента коллекции, id которого равен заданному";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");

        Integer ID = Integer.parseInt(argument);
        Ticket oldTicket = collectionManager.getByID(ID);

        if (oldTicket == null) {
            Set<Integer> idSet = collectionManager.getValues()
                    .stream()
                    .map(Ticket::getId)
                    .collect(Collectors.toSet());
            return String.format("Элемент с ID %d не обновлён, т.к. его не существует. Все существующие ID: %s", ID, idSet);
        }

        newTicket.setKey(oldTicket.getKey());
        newTicket.setId(oldTicket.getId());
        newTicket.setCreationDate(oldTicket.getCreationDate());
        collectionManager.replace(oldTicket.getKey(), oldTicket, newTicket);

        return String.format("Элемент с ID %d был успешно обновлён", ID);
    }
}
