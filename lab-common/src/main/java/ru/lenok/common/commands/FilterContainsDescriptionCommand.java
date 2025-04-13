package ru.lenok.common.commands;

import ru.lenok.common.CommandResponse;
import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import java.util.Map;

import static ru.lenok.common.LabWorkService.sortMapAndStringify;
import static ru.lenok.common.commands.CommandDefinition.filter_contains_description;

public class FilterContainsDescriptionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public FilterContainsDescriptionCommand(LabWorkService labWorkService) {
        super(filter_contains_description, "Аргумент - description. Вывести элементы, значение поля description которых содержит заданную подстроку");
        this.labWorkService = labWorkService;
    }

    @Override
    public CommandResponse execute(String arg) {
        Map<String, LabWork> filteredMap = labWorkService.filterWithDescription(arg);
        return new CommandResponse(sortMapAndStringify(filteredMap));
    }
}
