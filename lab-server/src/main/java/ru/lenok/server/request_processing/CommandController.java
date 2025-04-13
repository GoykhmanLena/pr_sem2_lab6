package ru.lenok.server.request_processing;

import lombok.AllArgsConstructor;
import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.CommandWithArgument;
import ru.lenok.common.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
public class CommandController {
    private final CommandRegistry commandRegistry;
 /**   private final static Map<HandlerKey, Function> handlerMap = new HashMap();
    static {
        handlerMap.put(new HandlerKey(false, false), AbstractCommand::execute);
    }
*/
    public CommandResponse handle(CommandRequest request) {
        CommandWithArgument commandWithArgument = request.getCommandWithArgument();
        CommandDefinition commandDefinition = commandWithArgument.getCommandDefinition();
        CommandResponse commandResponse;
        try {
            String executionResult = null;
            AbstractCommand command = commandRegistry.getCommand(commandDefinition);
            HandlerKey handlerKey = command instanceof HistoryCommand ? new HandlerKey(true, false) : new HandlerKey(command);
            switch (handlerKey.hashCode()) {
                case 0:
                    executionResult = command.execute();
                    break;
                case 1:
                    String argument = command instanceof HistoryCommand ? request.getClientID() : commandWithArgument.getArgument();
                    executionResult = command.execute(argument);
                    break;
                case 2:
                    executionResult = command.execute(request.getElement());
                    break;
                case 3:
                    executionResult = command.execute(commandWithArgument.getArgument(), request.getElement());
                    break;
            }
            commandResponse = new CommandResponse(executionResult);
        } catch (Exception e) {
            commandResponse = new CommandResponse(e);
        }
        return commandResponse;
    }
    @AllArgsConstructor
    private static class HandlerKey{
        boolean hasArgument;
        boolean hasElement;
        public HandlerKey(AbstractCommand abstractCommand){
            hasArgument = abstractCommand.hasArg();
            hasElement = abstractCommand.hasElement();
        }

        @Override
        public int hashCode() {
            int result = 0;
            result += hasArgument ? 1: 0;
            result += hasElement ? 2: 0;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HandlerKey that = (HandlerKey) o;
            return hasElement == that.hasElement && hasArgument == that.hasArgument;
        }
    }
}
