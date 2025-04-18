package ru.lenok.server.commands;

import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandBehavior;
import ru.lenok.server.collection.LabWorkService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.lenok.server.commands.CommandName.*;

public class CommandRegistry {
    public Map<CommandName, AbstractCommand> commands = new HashMap<>();
    public Collection<CommandName> commandDefinitions;
    public Map<String, CommandBehavior> clientCommandDefinitions;

    public CommandRegistry(LabWorkService labWorkService, IHistoryProvider historyProvider) {
        commands.put(insert, new InsertToCollectionCommand(labWorkService));
        commands.put(exit, new ExitFromProgramCommand());
        commands.put(show, new ShowCollectionCommand(labWorkService));
        commands.put(save, new SaveToFileCommand(labWorkService));
        commands.put(remove_key, new RemoveByKeyFromCollectionCommand(labWorkService));
        commands.put(update_id, new UpdateByIdInCollectionCommand(labWorkService));
        commands.put(print_ascending, new PrintAscendingCommand(labWorkService));
        commands.put(remove_greater, new RemoveGreaterFromCollectionCommand(labWorkService));
        commands.put(replace_if_greater, new ReplaceIfGreaterInCollectionCommand(labWorkService));
        commands.put(filter_contains_description, new FilterContainsDescriptionCommand(labWorkService));
        commands.put(filter_starts_with_name, new FilterStartsWithNameCommand(labWorkService));
        commands.put(help, new HelpCommand(this));
        commands.put(info, new InfoAboutCollectionCommand(labWorkService));
        commands.put(clear, new ClearCollectionCommand(labWorkService));
        commands.put(execute_script, new ExecuteScriptCommand(labWorkService));
        commands.put(history, new HistoryCommand(historyProvider));
        commandDefinitions = commands.keySet();

        clientCommandDefinitions = commands.keySet().stream()
                .filter(key -> key != save)
                .collect(Collectors.toMap(commandName -> commandName.name(), commandName -> commandName.getBehavior()));
    }

    public Map<String, CommandBehavior> getClientCommandDefinitions() {
        return clientCommandDefinitions;
    }

    public AbstractCommand getCommand(CommandName commandName) throws IllegalArgumentException {
        return commands.get(commandName);
    }

    public String getCommandDescription(CommandName commandName) {
        AbstractCommand command = getCommand(commandName);
        return commandName.name() + ": " + command.getDescription();
    }

    public String getCommandDescription(String commandNameStr) {
        CommandName commandName = valueOf(commandNameStr);
        return getCommandDescription(commandName);
    }

}
