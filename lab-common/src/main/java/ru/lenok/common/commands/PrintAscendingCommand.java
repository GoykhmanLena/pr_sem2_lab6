package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;


public class PrintAscendingCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public PrintAscendingCommand(LabWorkService labWorkService) {
        super(CommandDefinition.print_ascending, "вывести элементы коллекции в порядке возрастания");
        this.labWorkService = labWorkService;
    }

    public String execute(String arg) {
        return labWorkService.sortedByNameCollection();
    }
}
