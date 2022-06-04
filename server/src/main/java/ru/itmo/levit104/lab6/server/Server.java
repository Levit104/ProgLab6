package ru.itmo.levit104.lab6.server;

import org.apache.commons.lang3.SerializationUtils;
import ru.itmo.levit104.lab6.common.exceptions.ClientServerStartException;
import ru.itmo.levit104.lab6.common.exceptions.ConnectionException;
import ru.itmo.levit104.lab6.common.exceptions.ReadingDataException;
import ru.itmo.levit104.lab6.common.exceptions.WritingDataException;
import ru.itmo.levit104.lab6.common.messages.RequestMessage;
import ru.itmo.levit104.lab6.common.messages.ResponseMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private final int PORT;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private SocketChannel clientChannel;
    private final CommandManager commandManager;
    public Server(int PORT, CommandManager commandManager) {
        this.PORT = PORT;
        this.commandManager = commandManager;
        run();
    }
    private void run() {
        try {
            startServer();
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        connect(serverChannel, selector);
                    }
                    if (key.isReadable()) {
                        try {
                            RequestMessage request = readMessage(key);
                            ResponseMessage response = new ResponseMessage();
                            commandManager.executeCommand(request, response);
                            writeMessage(response);
                        } catch (ReadingDataException e) {
                            if (e.getCause() == null) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ServerLogger.logger.info("Отключен клиент {}",
                                        clientChannel.socket().getRemoteSocketAddress());
                                clientChannel.close();
                            } else {
                                ServerLogger.logger.error("Ошибка при получении данных.");
                            }
                        } catch (WritingDataException e) {
                            ServerLogger.logger.error("Ошибка при отправке данных.");
                        }
                    }
                }
            }
        } catch (IOException e) {
            ServerLogger.logger.error("Ошибка при отключении от клиента");
        } catch (ClientServerStartException e) {
            ServerLogger.logger.error("Ошибка при запуске сервера");
        } catch (ConnectionException e) {
            ServerLogger.logger.error("Ошибка при подключении к клиенту {}",
                    clientChannel.socket().getRemoteSocketAddress());
        }
    }
    private void startServer() throws ClientServerStartException {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            ServerLogger.logger.info("Сервер запущен");
        } catch (IOException e) {
            throw new ClientServerStartException(e);
        }

    }
    private void connect(ServerSocketChannel serverChannel, Selector selector) throws ConnectionException {
        try {
            SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            ServerLogger.logger.info("Подключен клиент {}", clientChannel.socket().getRemoteSocketAddress());
        } catch (IOException e) {
            throw new ConnectionException(e);
        }

    }
    private RequestMessage readMessage(SelectionKey key) throws ReadingDataException {
        try {
            clientChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);
            ((Buffer) byteBuffer).clear();

            int data = clientChannel.read(byteBuffer);
            if (data == -1) {
                key.cancel();
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
    private void writeMessage(ResponseMessage response) throws WritingDataException {
        try {
            byte[] bytes = SerializationUtils.serialize(response);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            while (byteBuffer.hasRemaining()) {
                clientChannel.write(byteBuffer);
            }
            ((Buffer) byteBuffer).clear();
        } catch (IOException e) {
            throw new WritingDataException(e);
        }
    }

}