package ru.lenok.client;

import lombok.AllArgsConstructor;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.commands.CommandName;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Map;

@AllArgsConstructor
public class ClientConnector {
    public static final String HELLO = "HELLO";
    private final InetAddress ip;
    private final int port;
    private static final int RETRY_COUNT = 3;
    private static final int WAIT_TIMEOUT = 1000*1000;
    private final String id = "lenok"; //TODO get UUID
    private Object sendData(Object obj) throws IOException {
        int retryCount = RETRY_COUNT;
        while (retryCount > 0){
            try (DatagramSocket socket = new DatagramSocket();) {
                byte[] data = serialize(obj);
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                System.out.println("передаю данные: " + obj);
                socket.send(packet);

                byte[] buffer = new byte[10000]; //TODO
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(WAIT_TIMEOUT);
                socket.receive(responsePacket);
                Object response = deserialize(responsePacket.getData());
                System.out.println("Ответ от сервера: " + response);
                return response;
            } catch (SocketTimeoutException e) {
                retryCount --;
                e.printStackTrace();
                if (retryCount == 0){
                    throw e;
                }
                System.err.println("Ошибка, повторяю попытку отправить, попытка: " + (RETRY_COUNT - retryCount + 1));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return null;
    }

    private String getHelloMessage(){
       // return HELLO + id;
        return id;
    }
    public Map<CommandName, CommandDefinition> sendHello() throws Exception {
        Object commandDefinitions = sendData(getHelloMessage());
        if (commandDefinitions instanceof Map){
            return (Map<CommandName, CommandDefinition>) commandDefinitions;
        }
        throw new IllegalArgumentException();//TODO
    }

    public CommandResponse sendCommand(CommandRequest commandRequest) throws Exception {
        Object commandResponse = sendData(commandRequest);
        if (commandResponse instanceof CommandResponse){
            return (CommandResponse) commandResponse;
        }
        throw new IllegalArgumentException();//TODO
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

    private byte[] serialize(Object obj){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
             ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(obj);
            oos.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
