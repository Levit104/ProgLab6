package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, заменяющая элемент, если цена нового элемента больше цены старого
 */
public class ReplaceIfGreaterCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public ReplaceIfGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "replace_if_greater";
    }
    @Override
    public String getDescription() {
        return " <key> : заменить значение по ключу, если новое значение больше старого";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");

        Integer key = Integer.parseInt(argument);
        Ticket oldTicket = collectionManager.getByKey(key);

        if (oldTicket == null) {
            return String.format("Элемента с ключом %d не существует", key);
        }

        if (newTicket.compareTo(oldTicket) > 0) {
            collectionManager.replace(key, oldTicket, newTicket);
            return String.format("Элемент с ключом %d был успешно заменён", key);
        } else {
            return String.format("Элемент с ключом %d не был заменён, т.к новое значение цены меньше старого", key);
        }
    }
}
