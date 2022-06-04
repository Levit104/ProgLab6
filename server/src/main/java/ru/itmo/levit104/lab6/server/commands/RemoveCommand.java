package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, удаляющая элемент из коллекции
 */
public class RemoveCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "remove_key";
    }
    @Override
    public String getDescription() {
        return " <key> : удалить элемент из коллекции по его ключу";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");

        Integer key = Integer.parseInt(argument);
        boolean removedElement = collectionManager.removeBy(key);
        if (removedElement) {
            return String.format("Элемент с ключом %d успешно удалён", key);
        } else {
            return String.format("Элемента с ключом %d не существует. Все существующие ключи: %s",
                    key, collectionManager.getKeys());
        }
    }
}
