package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда-фильтр, выводит значение поля "тип билета" элементов в порядке убывания
 */
public class PrintFieldDescendingCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public PrintFieldDescendingCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return false;
    }
    @Override
    public String getName() {
        return "print_field_descending_type";
    }
    @Override
    public String getDescription() {
        return " : вывести значения поля type всех элементов в порядке убывания";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");
        return "Значения поля type всех элементов в порядке убывания: " + collectionManager.fieldDescending();
    }
}
