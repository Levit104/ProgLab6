package ru.itmo.levit104.lab6.server.commands;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.common.exceptions.NotUniqueValueException;

/**
 * Интерфейс, описывающий поведение всех команд
 */

public interface Command {
    /**
     * Устанавливает есть ли у команды аргумент
     *
     * @return {@code true} если аргумент есть, иначе {@code false}
     */
    boolean hasArgument();

    /**
     * Возвращает имя команды
     *
     * @return имя команды
     */
    String getName();

    /**
     * Возвращает описание команды
     *
     * @return описание команды
     */
    String getDescription();

    /**
     * Запускает выполнение команды
     *
     * @param argument аргумент команды (если есть)
     */
    String execute(String argument, Ticket newTicket) throws EmptyCollectionException, NotUniqueValueException;
}
