package ru.lenok.client;

import ru.lenok.common.InputProcessor;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.commands.CommandName;
import ru.lenok.common.input.AbstractInput;
import ru.lenok.common.input.ConsoleInput;

import java.net.InetAddress;
import java.util.Map;

public class ClientApplication {
    private final InetAddress ip;
    private final int port;
    private final String clientID = "lenok"; //TODO get UUID
    private Map<CommandName, CommandDefinition> commandDefinitions;
    public ClientApplication(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void start() throws Exception {
        ClientConnector clientConnector= new ClientConnector(ip, port);
        commandDefinitions = clientConnector.sendHello();

        try (AbstractInput input = new ConsoleInput()) {
            ClientInputProcessor inputProcessor = new ClientInputProcessor(commandDefinitions, clientConnector, clientID);
            inputProcessor.processInput(input, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
