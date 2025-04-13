package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import static ru.lenok.common.commands.CommandDefinition.replace_if_greater;


public class ReplaceIfGreaterInCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public ReplaceIfGreaterInCollectionCommand(LabWorkService labWorkService) {
        super(replace_if_greater, "Аргумент - ключ. Элемент. Заменить значение по ключу, если новое значение больше старого");
        this.labWorkService = labWorkService;
    }


    @Override
    public String execute(String key, LabWork element) {
        labWorkService.replaceIfGreater(key, element);
        return EMPTY_RESULT;
    }
}
