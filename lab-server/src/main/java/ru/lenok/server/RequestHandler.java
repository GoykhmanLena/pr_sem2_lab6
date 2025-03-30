package ru.lenok.server;

import ru.lenok.common.CommandController;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.commands.CommandRegistry;
import ru.lenok.common.commands.IHistoryProvider;
import ru.lenok.common.util.HistoryList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler implements IHistoryProvider {
    private final CommandController commandController;
    private final CommandRegistry commandRegistry;
    private Map<String, HistoryList> historyByClients = new ConcurrentHashMap();

    public CommandController getCommandController() {
        return commandController;
    }

    public RequestHandler(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
        this.commandController = new CommandController(commandRegistry);
    }

    public Object onReceive(Object inputData){
        if (inputData instanceof CommandRequest){
            CommandRequest commandRequest = (CommandRequest) inputData;
            HistoryList historyList = historyByClients.get(commandRequest.getClientID());
            historyList.addCommand(commandRequest.getCommandWithArgument().getCommandDefinition().getCommandName());
            return commandController.handle(commandRequest);
        } else if (inputData instanceof String) {
            historyByClients.put((String) inputData, new HistoryList());
            return commandRegistry.getCommandDefinitions();
        }
        throw new IllegalArgumentException();//todo
    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        return historyByClients.get(clientID);
    }
}
