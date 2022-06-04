package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда-фильтр, выводит элементы в порядке возрастания их цены
 */

public class PrintAscendingCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public PrintAscendingCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return false;
    }
    @Override
    public String getName() {
        return "print_ascending";
    }
    @Override
    public String getDescription() {
        return " : вывести элементы коллекции в порядке возрастания (цены)";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");
        return "Элементы коллекции в порядке возрастания цены:\n" + collectionManager.ascending();
    }
}
