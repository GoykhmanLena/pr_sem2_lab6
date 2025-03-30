package ru.lenok.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws Exception {
        String hostWithPort = args[0];// TODO длинна массива
        String[] split = hostWithPort.split(":");
        String strHost = split[0]; // TODO длинна массива
        String strPort = split[1];
        int port = 0;
        InetAddress ip = null;
        try {
            port = Integer.parseInt(strPort);
        } catch (NumberFormatException e){
            System.err.println(e);
        }

        try {
            ip = InetAddress.getByName(strHost);
        } catch (UnknownHostException e){
            System.err.println(e);
        }
        ClientApplication app = new ClientApplication(ip, port);
        app.start();
    }
}
