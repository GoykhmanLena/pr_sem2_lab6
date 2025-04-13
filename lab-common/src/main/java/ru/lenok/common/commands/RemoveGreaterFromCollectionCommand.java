package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import static ru.lenok.common.commands.CommandDefinition.remove_greater;

public class RemoveGreaterFromCollectionCommand extends AbstractCommand{
    LabWorkService labWorkService;

    public RemoveGreaterFromCollectionCommand(LabWorkService labWorkService) {
        super(remove_greater, "Элемент. Удалить из коллекции все элементы, превышающие заданный");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(LabWork element) {
        labWorkService.removeGreater(element);
        return EMPTY_RESULT;
    }
}
