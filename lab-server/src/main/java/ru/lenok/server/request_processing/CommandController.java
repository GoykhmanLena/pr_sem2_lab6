package ru.lenok.server.request_processing;

import lombok.AllArgsConstructor;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.CommandWithArgument;
import ru.lenok.common.commands.*;

@AllArgsConstructor
public class CommandController {
    private final CommandRegistry commandRegistry;

    public CommandResponse handle(CommandRequest request) {
        CommandWithArgument commandWithArgument = request.getCommandWithArgument();
        CommandDefinition commandDefinition = commandWithArgument.getCommandDefinition();
        CommandResponse commandResponse;
        try {
            String executionResult;
            AbstractCommand command = commandRegistry.getCommand(commandDefinition.getCommandName());
            if (command instanceof CommandWithElement) {
                CommandWithElement commandWithElement = (CommandWithElement) command;
                executionResult = commandWithElement.execute(commandWithArgument.getArgument(), request.getElement());
            } else if (command instanceof HistoryCommand) {
                HistoryCommand historyCommand = (HistoryCommand) command;
                executionResult = historyCommand.execute(request.getClientID());
            } else {
                executionResult = command.execute(commandWithArgument.getArgument());
            }
            commandResponse = new CommandResponse(executionResult);
        } catch (Exception e) {
            commandResponse = new CommandResponse(e);
        }
        return commandResponse;
    }
}
