package ru.lenok.request_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.command_processor.CommandController;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.commands.CommandRegistry;
import ru.lenok.common.commands.IHistoryProvider;
import ru.lenok.common.util.HistoryList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler implements IHistoryProvider {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
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
            CommandDefinition commandDefinition = commandRequest.getCommandWithArgument().getCommandDefinition();
            if (commandDefinition.isClient()){
                return null;
            }
            String clientID = commandRequest.getClientID();
            HistoryList historyList = historyByClients.get(clientID);
            if (historyList == null){
                logger.warn("клиент с таким id не зарегистрирован, регистрирую " + clientID);
                historyList = new HistoryList();
                historyByClients.put(clientID, historyList);
            }
            historyList.addCommand(commandDefinition.getCommandName());
            return commandController.handle(commandRequest);
        } else if (inputData instanceof String) {
            historyByClients.put((String) inputData, new HistoryList());
            return commandRegistry.getClientCommandDefinitions();
        }
        throw new IllegalArgumentException("Вы передали какую-то чепуху!!!" + inputData);
    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        return historyByClients.get(clientID);
    }
}
