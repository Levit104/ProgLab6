package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, удаляющая элемент из коллекции, если его ключ меньше того, что
 * введет пользователь
 */
public class RemoveLowerKeyCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveLowerKeyCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "remove_lower_key";
    }
    @Override
    public String getDescription() {
        return " <key> : удалить из коллекции все элементы, ключ которых меньше, чем заданный";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");
        Integer key = Integer.parseInt(argument);
        boolean removedElement = collectionManager.removeLower(key);
        if (removedElement) {
            return String.format("Элементы, ключ которых меньше %d успешно удалены", key);
        } else {
            return String.format("Элементов, ключ которых меньше %d не существует", key);
        }
    }
}
