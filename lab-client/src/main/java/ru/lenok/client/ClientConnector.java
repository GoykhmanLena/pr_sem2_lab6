package ru.lenok.client;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.commands.CommandName;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

import static ru.lenok.client.ClientApplication.CLIENT_ID;
import static ru.lenok.common.util.SerializationUtils.*;

@AllArgsConstructor
public class ClientConnector {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnector.class);
    public static final int INTER_WAIT_SLEEP_TIMEOUT = 50;
    private final InetAddress ip;
    private final int port;
    private static final int RETRY_COUNT = 10;
    private static final int WAIT_TIMEOUT = 1000 * 10;
    InetSocketAddress serverAddress;

    public ClientConnector(InetAddress ip, int port) {
        serverAddress = new InetSocketAddress(ip, port);
        this.ip = ip;
        this.port = port;
    }

    /*  private Object sendDataOld(Object obj) throws IOException {
        int retryCount = RETRY_COUNT;
        while (retryCount > 0) {
            try (DatagramSocket socket = new DatagramSocket();) {
                byte[] data = serialize(obj);
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                logger.debug("передаю данные: " + obj);
                socket.send(packet);

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(WAIT_TIMEOUT);
                socket.receive(responsePacket);
                Object response = deserialize(responsePacket.getData());
                logger.debug("Ответ от сервера: " + response);
                return response;
            } catch (SocketTimeoutException e) {
                retryCount--;
                e.printStackTrace();
                if (retryCount == 0) {
                    throw e;
                }
                logger.error("Ошибка, повторяю попытку отправить, попытка: " + (RETRY_COUNT - retryCount + 1));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return null;
    }
*/
    private Object sendData(Object obj) {
        int retryCount = RETRY_COUNT;
        while (retryCount > 0) {
            retryCount--;
            try (DatagramChannel clientChannel = DatagramChannel.open()) {
                clientChannel.configureBlocking(false);

                byte[] data = serialize(obj);
                ByteBuffer buffer = ByteBuffer.wrap(data);

                clientChannel.send(buffer, serverAddress);
                logger.debug("Данные отправлены серверу: " + obj);

                ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

                long startTime = System.currentTimeMillis();
                InetSocketAddress sourceAddress = null;

                while ((System.currentTimeMillis() - startTime) < WAIT_TIMEOUT) {
                    receiveBuffer.clear();
                    sourceAddress = (InetSocketAddress) clientChannel.receive(receiveBuffer);

                    if (sourceAddress != null) {
                        // Данные получены — выходим из цикла
                        break;
                    }

                    // Пауза, чтобы не нагружать процессор в холостую
                    Thread.sleep(INTER_WAIT_SLEEP_TIMEOUT);
                }

                if (sourceAddress != null) {
                    receiveBuffer.flip();
                    Object response = deserialize(receiveBuffer.array());
                    logger.debug("Ответ от сервера: " + response);
                    return response;
                } else {
                    if (retryCount > 0) {
                        logger.warn("Сервер не ответил, повторяю попытку отправить, попытка: " + (RETRY_COUNT - retryCount + 1) + " из " + RETRY_COUNT);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Сервер недоступен");
    }

    private String getHelloMessage() {
        return CLIENT_ID;
    }

    public Map<CommandName, CommandDefinition> sendHello() {
        Object commandDefinitions = sendData(getHelloMessage());
        if (commandDefinitions instanceof Map) {
            return (Map<CommandName, CommandDefinition>) commandDefinitions;
        }
        throw new IllegalArgumentException("Неверный ответ от сервера на команду приветствия: " + commandDefinitions);
    }

    public CommandResponse sendCommand(CommandRequest commandRequest) {
        Object commandResponse = sendData(commandRequest);
        if (commandResponse instanceof CommandResponse) {
            return (CommandResponse) commandResponse;
        }
        throw new IllegalArgumentException("Неверный ответ от сервера на команду: " + commandResponse);
    }


}
