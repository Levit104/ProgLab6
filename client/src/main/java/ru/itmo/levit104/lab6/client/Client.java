package ru.itmo.levit104.lab6.client;

import org.apache.commons.lang3.SerializationUtils;
import ru.itmo.levit104.lab6.common.data.Ticket;
import ru.itmo.levit104.lab6.common.exceptions.ClientServerClosingException;
import ru.itmo.levit104.lab6.common.exceptions.ClientServerStartException;
import ru.itmo.levit104.lab6.common.exceptions.ReadingDataException;
import ru.itmo.levit104.lab6.common.exceptions.WritingDataException;
import ru.itmo.levit104.lab6.common.messages.MessageStatus;
import ru.itmo.levit104.lab6.common.messages.RequestMessage;
import ru.itmo.levit104.lab6.common.messages.ResponseMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Client {
    private final String HOST;
    private final int PORT;
    private final ConsoleManager consoleManager;
    private SocketChannel socketChannel;
    private boolean connected;
    private int connectionAttempts;

    public Client(int PORT, ConsoleManager consoleManager) {
        this.HOST = "localhost";
        this.PORT = PORT;
        this.consoleManager = consoleManager;
        run();
    }
    private void run() {
        try {
            connectionAttempts++;
            startClient();
            connected = true;

            while (connected) {
                consoleManager.startInput();
                System.out.println(); //просто отступ

                String commandName = consoleManager.getCommandName();
                String commandArgument = consoleManager.getCommandArgument();

                if (commandName.equals("execute_script")) {
                    scriptMode(commandArgument);
                } else {
                    sendRequestPrintResponse(commandName, commandArgument);
                }
            }
            closeClient();
        } catch (ClientServerStartException e) {
            if (connectionAttempts == 1) {
                System.err.println("Ошибка при запуске клиента. Сервер недоступен");
            }
        } catch (WritingDataException e) {
            System.err.println("Ошибка при отправке данных.");
        } catch (ReadingDataException e) {
            connected = false;
            if (e.getCause() == null) {
                System.err.println("Потеряно соединение с сервером.");
                while (!connected) {
                    System.out.println("Попытка переподключиться...");
                    run();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            } else {
                System.err.println("Ошибка при получении данных.");
            }
        } catch (ClientServerClosingException e) {
            System.err.println("Ошибка при закрытии клиента.");
        }
    }
    private void startClient() throws ClientServerStartException {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(true);
            System.out.println("Успешное подключение к серверу");
        } catch (IOException e) {
            throw new ClientServerStartException(e);
        }
    }
    private void writeMessage(RequestMessage request) throws WritingDataException {
        try {
            byte[] bytes = SerializationUtils.serialize(request);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            socketChannel.write(byteBuffer);
            ((Buffer) byteBuffer).clear();
        } catch (IOException e) {
            throw new WritingDataException(e);
        }
    }
    private ResponseMessage readMessage() throws ReadingDataException {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);
            ((Buffer) byteBuffer).clear();

            int data = socketChannel.read(byteBuffer);
            if (data == -1) {
                throw new ReadingDataException();
            }

            byte[] bytes = new byte[data];
            ((Buffer) byteBuffer).position(0);
            byteBuffer.get(bytes);

            return SerializationUtils.deserialize(bytes);
        } catch (IOException e) {
            throw new ReadingDataException(e);
        }
    }
    private void closeClient() throws ClientServerClosingException {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new ClientServerClosingException(e);
        }
    }
    private void sendRequestPrintResponse(String commandName, String commandArgument) throws WritingDataException, ReadingDataException {
        Set<String> specialCommands =
                new HashSet<>(Arrays.asList("insert", "update", "replace_if_greater", "replace_if_lower"));

        Ticket ticket = null;
        if (specialCommands.contains(commandName)) {
            ticket = consoleManager.createTicket(commandArgument);
        }

        RequestMessage request = new RequestMessage(commandName, commandArgument, ticket);
        writeMessage(request);
        ResponseMessage response = readMessage();

        if (response.getStatus() == MessageStatus.ERROR) {
            System.out.print("При выполнении команды произошла ошибка: ");
        }

        if (response.getMessage() != null) {
            System.out.println(response.getMessage());
        }

        if (response.getStatus() == MessageStatus.EXIT) {
            connected = false;
        }
    }
    private void scriptMode(String file) throws WritingDataException, ReadingDataException {
        Scanner mainScanner = consoleManager.getScanner();
        try {
            Scanner scriptScanner = new Scanner(new File(file));
            consoleManager.setScanner(scriptScanner);
            consoleManager.setInScript(true);
            System.out.printf("Выполнение скрипта %s\n\n", file);
            while (scriptScanner.hasNextLine()) {
                String[] command = scriptScanner.nextLine().split("\\s+", 2);
                String commandName = command[0];
                String commandArgument = (command.length > 1) ? command[1] : "";

                if (commandName.equals("execute_script")) {
                    scriptMode(commandArgument);
                } else {
                    sendRequestPrintResponse(commandName, commandArgument);
                }
            }
            System.out.printf("Выполнен скрипт %s\n\n", file);
        } catch (NoSuchElementException e) {
            System.out.printf("\nОшибка при чтении скрипта %s\n\n", file);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден\n");
        } finally {
            consoleManager.setScanner(mainScanner);
            consoleManager.setInScript(false);
        }
    }

}
