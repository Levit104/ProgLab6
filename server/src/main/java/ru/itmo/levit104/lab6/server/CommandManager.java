package ru.itmo.levit104.lab6.server;

import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.EmptyCollectionException;
import ru.itmo.levit104.lab6.common.exceptions.IllegalCommandArgumentException;
import ru.itmo.levit104.lab6.common.exceptions.NotUniqueValueException;
import ru.itmo.levit104.lab6.common.messages.*;
import ru.itmo.levit104.lab6.server.commands.Command;

import java.util.Arrays;

public class CommandManager {
    private final Command[] commands;
    public CommandManager(Command[] commands) {
        this.commands = commands;
    }
    public void executeCommand(RequestMessage request, ResponseMessage response) {
        String commandName = request.getCommandName();
        String commandArgument = request.getCommandArgument();
        Ticket commandTicket = request.getTicket();
        boolean wasFound = false;
        String result;

        ServerLogger.logger.info("Выполняется команда {} {}", commandName, commandArgument);
        try {
            if (commandName.equals("help")) {
                wasFound = true;
                if (incorrectArgument(commandArgument, false)) {
                    throw new IllegalCommandArgumentException("у данной команды команды нет аргументов");
                }
                result = executeHelp();
                response.setMessage(result + "\n");
                response.setStatus(MessageStatus.OK);
                ServerLogger.logger.info("Команда успешно выполнена");
            } else if (commandName.equals("exit")) {
                wasFound = true;
                if (incorrectArgument(commandArgument, false)) {
                    throw new IllegalCommandArgumentException("у данной команды команды нет аргументов");
                }
                response.setMessage("Отключение клиента\n");
                response.setStatus(MessageStatus.EXIT);
                ServerLogger.logger.info("Команда успешно выполнена");
            } else {
                for (Command command : commands) {
                    if (commandName.equals(command.getName())) {
                        wasFound = true;
                        if (incorrectArgument(commandArgument, command.hasArgument())) {
                            result = command.hasArgument() ?
                                    "у данной команды только один аргумент" : "у данной команды нет аргументов";
                            throw new IllegalCommandArgumentException(result);
                        }
                        result = command.execute(commandArgument, commandTicket);
                        response.setMessage(result + "\n");
                        response.setStatus(MessageStatus.OK);
                        ServerLogger.logger.info("Команда успешно выполнена");
                    }
                }
            }
        } catch (IllegalCommandArgumentException | NotUniqueValueException | EmptyCollectionException e) {
            ServerLogger.logger.error("При выполнении команды произошла ошибка: {}", e.getMessage());
            response.setMessage(e.getMessage() + "\n");
            response.setStatus(MessageStatus.ERROR);
        } catch (NumberFormatException e) { //Его бросают команды, которые используют метод Integer.parseInt()
            result = "значение аргумента должно быть целым числом и не содержать пробелов";
            ServerLogger.logger.error("При выполнении команды произошла ошибка: {}", result);
            response.setMessage(result + "\n");
            response.setStatus(MessageStatus.ERROR);
        }

        if (!wasFound) {
            ServerLogger.logger.error("При выполнении команды произошла ошибка: несуществующая команда");
            response.setMessage("несуществующая команда\n");
            response.setStatus(MessageStatus.ERROR);
        }
    }
    private boolean incorrectArgument(String argument, boolean hasArgument) {
        return hasArgument ? !(argument.split("\\s+").length == 1) : !argument.isEmpty();
    }
    private String executeHelp() {
        StringBuilder help = new StringBuilder("Все доступные команды: " +
                "\nhelp : вывести справку по доступным командам " +
                "\nexit : завершить работу клиента " +
                "\nexecute_script <file_name> : считать и исполнить скрипт из указанного файла");
        Arrays.asList(commands).forEach(c -> help.append("\n").append(c.getName()).append(c.getDescription()));
        return help.toString();
    }
}