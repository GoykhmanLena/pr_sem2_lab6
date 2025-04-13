package ru.lenok.common.commands;

import static ru.lenok.common.commands.CommandDefinition.history;

public class HistoryCommand extends AbstractCommand {
    private final IHistoryProvider historyProvider;

    public HistoryCommand(IHistoryProvider historyProvider) {
        super(history, "вывести последние 15 команд (без их аргументов)");
        this.historyProvider = historyProvider;
    }

    @Override
    public String execute(String clientID) {
        String lastNCommands = historyProvider.getHistoryByClientID(clientID).getLastNCommands(15);
        return "История клиента с ID: " + clientID + "\n" + lastNCommands;
    }
}
