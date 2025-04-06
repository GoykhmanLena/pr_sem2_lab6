package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import java.util.Map;

import static ru.lenok.common.LabWorkService.sortMapAndStringify;

public class FilterContainsDescriptionCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public FilterContainsDescriptionCommand(LabWorkService labWorkService) {
        super("filter_contains_description description", "вывести элементы, значение поля description которых содержит заданную подстроку");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute(String arg) {
        Map<String, LabWork> filteredMap = labWorkService.filterWithDescription(arg);
        return sortMapAndStringify(filteredMap);
    }


    @Override
    public boolean hasArg() {
        return true;
    }
}
