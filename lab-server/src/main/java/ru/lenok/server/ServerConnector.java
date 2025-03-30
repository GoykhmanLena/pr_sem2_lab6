package ru.lenok.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.commands.CommandRegistry;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


@Data
@AllArgsConstructor
public class ServerConnector {
    private final int port;
    private final RequestHandler requestHandler;
    public void listen(){
        byte[] buffer = new byte[10000];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP сервер запущен на порту " + port);
            while (true) {
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                System.out.println("Жду сообщения");
                socket.receive(packetFromClient);

                Object dataFromClient = deserialize(packetFromClient.getData());
                System.out.println("Получено: " + dataFromClient);
                Object response = requestHandler.onReceive(dataFromClient);

                byte[] responseData = serialize(response);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, packetFromClient.getAddress(), packetFromClient.getPort());

                socket.send(responsePacket);
                System.out.println("Отправлены данные: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Object obj){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
             ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(obj);
            oos.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object deserialize(byte[] data){
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
           // System.out.println("Объект десериализован: " + obj);
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
