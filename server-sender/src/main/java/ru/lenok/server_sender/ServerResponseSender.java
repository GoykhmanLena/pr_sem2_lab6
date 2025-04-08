package ru.lenok.server_sender;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ru.lenok.common.util.SerializationUtils;

@Data
public class ServerResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(ServerResponseSender.class);
    private final DatagramSocket socket;

    public ServerResponseSender(DatagramSocket datagramSocket) {
        this.socket = datagramSocket;
    }

    public void sendMessageToClient(Object response, InetAddress clientIp, int clientPort) throws IOException {
        byte[] responseData = SerializationUtils.INSTANCE.serialize(response);
        DatagramPacket responsePacket = new DatagramPacket(
                responseData, responseData.length, clientIp, clientPort);

        socket.send(responsePacket);
        logger.debug("Отправлены данные: " + response);
    }
}
