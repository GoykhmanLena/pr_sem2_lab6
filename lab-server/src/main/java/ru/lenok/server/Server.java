package ru.lenok.server;

import java.io.IOException;


public final class Server {

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException {
        ServerApplication app = new ServerApplication(args); // TODO 2 аргумента файл + порт
        app.start();

    }
}

