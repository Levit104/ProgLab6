package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.NotUniqueValueException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, добавляющая элемент в коллекцию
 */
public class InsertCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public InsertCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "insert";
    }
    @Override
    public String getDescription() {
        return " <key> : добавить новый элемент с заданным ключом";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws NotUniqueValueException {
        Integer key = Integer.parseInt(argument);

        if (!CollectionManager.uniqueTicketKey(key, collectionManager.getCollection())) {
            throw new NotUniqueValueException(String.format("элемент с ключом %s уже существует", key));
        }

        while (!CollectionManager.uniqueTicketID(newTicket.getId(), collectionManager.getCollection())) {
            newTicket.setId(newTicket.getId() + 1);
        }

        while (!CollectionManager.uniqueEventID(newTicket.getEvent().getId(), collectionManager.getCollection())) {
            newTicket.getEvent().setId(newTicket.getEvent().getId() + 1);
        }

        collectionManager.add(key, newTicket);
        return String.format("Элемент с ключом %d успешно добавлен", key);
    }
}