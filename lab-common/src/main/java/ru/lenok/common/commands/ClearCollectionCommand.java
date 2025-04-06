package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.util.IdCounterService;



public class ClearCollectionCommand extends AbstractCommand {
    LabWorkService collection;

    public ClearCollectionCommand(LabWorkService collection) {
        super("clear", "очистить коллекцию");
        this.collection = collection;
    }

    @Override
    public String execute(String arg) {
        collection.clear_collection();
        IdCounterService.setId(0);
        return EMPTY_RESULT;
    }
}
