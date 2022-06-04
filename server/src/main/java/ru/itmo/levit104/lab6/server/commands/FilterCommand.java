package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда-фильтр, выводит элементы, имя которых начинается с введённой пользователем подстроки
 */
public class FilterCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public FilterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return true;
    }
    @Override
    public String getName() {
        return "filter_starts_with_name";
    }
    @Override
    public String getDescription() {
        return " <name> : вывести элементы, значение поля name которых начинается с заданной подстроки";
    }
    @Override
    public String execute(String argument, Ticket newTicket) throws EmptyCollectionException {
        if (collectionManager.isEmpty()) throw new EmptyCollectionException("коллекция пустая");

        String ticketList = collectionManager.filterBy(argument);
        if (ticketList.length() == 0) {
            return String.format("В коллекции нет элементов, название которых начинается на %s", argument);
        } else {
            return String.format("Элементы, название которых начинается на %s:\n%s", argument, ticketList);
        }
    }
}
