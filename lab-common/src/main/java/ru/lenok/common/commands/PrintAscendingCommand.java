package ru.lenok.common.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.LabWorkService;


public class PrintAscendingCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public PrintAscendingCommand(LabWorkService labWorkService) {
        super(CommandDefinition.print_ascending, "вывести элементы коллекции в порядке возрастания");
        this.labWorkService = labWorkService;
    }
    @Override
    public CommandResponse execute(String arg) {
        return new CommandResponse(labWorkService.sortedByNameCollection());
    }
}
