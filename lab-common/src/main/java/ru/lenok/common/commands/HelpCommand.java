package ru.lenok.common.commands;

import java.util.stream.Collectors;

public class HelpCommand extends AbstractCommand {
    private final CommandRegistry commandRegistry;

    public HelpCommand(CommandRegistry inv) {
        super("help", "вывести справку по доступным командам");
        this.commandRegistry = inv;
    }

    @Override
    public String execute(String arg) {
        return commandRegistry.getClientCommandDefinitions().entrySet().stream()
                .map(entry -> commandRegistry.getCommandDescription(entry.getKey()))
                .collect(Collectors.joining("\n"));
    }
}
