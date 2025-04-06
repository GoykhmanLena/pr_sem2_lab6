package ru.lenok.server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.commands.CommandRegistry;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import static ru.lenok.common.util.SerializationUtils.*;

@Data
@AllArgsConstructor
public class ServerConnector {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnector.class);
    private final int port;
    private final RequestHandler requestHandler;
    public void listen(){
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            logger.info("UDP сервер запущен на порту " + port);
            while (true) {
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                logger.info("Жду сообщения");
                socket.receive(packetFromClient);

                Object dataFromClient = deserialize(packetFromClient.getData());
                logger.info("Получено: " + dataFromClient);
                Object response = requestHandler.onReceive(dataFromClient);

                byte[] responseData = serialize(response);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, packetFromClient.getAddress(), packetFromClient.getPort());

                socket.send(responsePacket);
                logger.debug("Отправлены данные: " + response);
            }
        } catch (Exception e) {
            logger.error("Ошибка: ", e);
        }
    }
}
