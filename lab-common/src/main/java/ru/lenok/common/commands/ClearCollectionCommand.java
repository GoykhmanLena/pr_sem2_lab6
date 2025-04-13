package ru.lenok.common.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.LabWorkService;
import ru.lenok.common.util.IdCounterService;

import static ru.lenok.common.commands.CommandDefinition.clear;


public class ClearCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ClearCollectionCommand(LabWorkService labWorkService) {
        super(clear, "очистить коллекцию");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(String arg) {
        labWorkService.clear_collection();
        IdCounterService.setId(0);
        return new CommandResponse(EMPTY_RESULT);
    }
}
