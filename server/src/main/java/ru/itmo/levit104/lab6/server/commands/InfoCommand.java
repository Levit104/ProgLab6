package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.server.CollectionManager;

/**
 * Команда, выводящая информацию о коллекции
 */
public class InfoCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор, задающий параметры для создания объекта
     *
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public boolean hasArgument() {
        return false;
    }
    @Override
    public String getName() {
        return "info";
    }
    @Override
    public String getDescription() {
        return " : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
    @Override
    public String execute(String argument, Ticket newTicket) {
        return collectionManager.info();
    }
}
