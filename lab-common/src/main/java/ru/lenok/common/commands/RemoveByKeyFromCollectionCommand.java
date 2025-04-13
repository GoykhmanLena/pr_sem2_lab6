package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

import static ru.lenok.common.commands.CommandDefinition.remove_key;

public class RemoveByKeyFromCollectionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public RemoveByKeyFromCollectionCommand(LabWorkService labWorkService) {
        super(remove_key, "Аргумент - ключ. Удалить элемент из коллекции по его ключу");
        this.labWorkService = labWorkService;
    }
    @Override
    public String execute(String key) {
        labWorkService.remove(key);
        return EMPTY_RESULT;
    }
}
