package ru.lenok.server_sender;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import ru.lenok.common.util.SerializationUtils;

@Data
public class ServerResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(ServerResponseSender.class);
    private final SerializationUtils serializer = SerializationUtils.INSTANCE;
    private final DatagramSocket socket;

    public ServerResponseSender(DatagramSocket datagramSocket) {
        this.socket = datagramSocket;
    }

    public void sendMessageToClient(Object response, InetAddress clientIp, int clientPort) throws IOException, InterruptedException {
        List<byte[]> chunks = serializer.serializeAndSplitToChunks(response);
        int chunkSize = chunks.size();
        byte[] chunksSizeSerialized = serializer.serialize(new Integer(chunkSize));

        DatagramPacket responsePacket = new DatagramPacket(chunksSizeSerialized, chunksSizeSerialized.length, clientIp, clientPort);
        socket.send(responsePacket);
        logger.debug("Отправлено количество чанков: " + chunkSize);
        int i = 0;
        for (byte[] responseDataChunk : chunks) {
            responsePacket = new DatagramPacket(responseDataChunk, responseDataChunk.length, clientIp, clientPort);
            socket.send(responsePacket);
            i++;
            Thread.sleep(50);
            logger.debug("Отправлен чанк " + i + " из " + chunkSize);
        }
        if (chunkSize == 1) {
            logger.debug("Отправлены данные: " + response);
        }
    }
}
