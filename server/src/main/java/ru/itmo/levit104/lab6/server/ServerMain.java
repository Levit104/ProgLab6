package ru.itmo.levit104.lab6.server;

import ru.itmo.levit104.lab6.server.commands.*;

public class ServerMain {
    public static void main(String[] args) {
        try {
            if (args.length > 2) throw new ArrayIndexOutOfBoundsException();
            int port = Integer.parseInt(args[0]);
            String filePath = args[1];

            FileManager fileManager = new FileManager(filePath);
            CollectionManager collectionManager = new CollectionManager(fileManager);

            Command[] commands = {
                    new ClearCommand(collectionManager),
                    new FilterCommand(collectionManager),
                    new InfoCommand(collectionManager),
                    new InsertCommand(collectionManager),
                    new PrintAscendingCommand(collectionManager),
                    new PrintFieldDescendingCommand(collectionManager),
                    new RemoveCommand(collectionManager),
                    new RemoveLowerKeyCommand(collectionManager),
                    new ReplaceIfGreaterCommand(collectionManager),
                    new ReplaceIfLowerCommand(collectionManager),
                    new ShowCommand(collectionManager),
                    new UpdateCommand(collectionManager)
            };

            CommandManager commandManager = new CommandManager(commands);
            new Server(port, commandManager);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Для запуска сервера нужно ввести порт и путь до файла " +
                    "(при вводе лишнего сервер также не будет запущен");
        } catch (NumberFormatException e) {
            System.out.println("Порт должен быть числом");
        }
    }
}