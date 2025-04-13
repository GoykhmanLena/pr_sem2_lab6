package ru.lenok.common.commands;


import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.util.IdCounterService;

import static ru.lenok.common.commands.CommandDefinition.insert;


public class InsertToCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;
    CommandDefinition commandDefinition;


    public InsertToCollectionCommand(LabWorkService labWorkService, CommandDefinition commandDefinition) {
        super(insert, "Аргумент - ключ; Элемент; Добавить новый элемент с заданным ключом");
        this.labWorkService = labWorkService;
        this.commandDefinition = commandDefinition;
    }

    @Override
    public String execute(String key, LabWork element) {
        element.setId(IdCounterService.getNextId());
        String warning = labWorkService.put(key, element);
        return warning == null ? EMPTY_RESULT : warning;
    }
}
