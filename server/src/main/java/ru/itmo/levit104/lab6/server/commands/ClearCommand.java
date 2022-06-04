package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, очищающая коллекцию
 */
public class ClearCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return false;
    }
    @Override
    public String getName() {
        return "clear";
    }
    @Override
    public String getDescription() {
        return " : очистить коллекцию";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");
        collectionManager.clearCollection();
        return "Коллекция успешно очищена";
    }
}

