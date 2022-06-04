package ru.itmo.levit104.lab6.client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        try {
            if (args.length > 2) throw new ArrayIndexOutOfBoundsException();
            int port = Integer.parseInt(args[0]);
            Scanner scanner = new Scanner(System.in);
            ConsoleManager consoleManager = new ConsoleManager(scanner);
            new Client(port, consoleManager);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Для запуска клиента нужно ввести порт и путь до файла " +
                    "(при вводе лишнего клиент также не будет запущен");
        }

    }
}