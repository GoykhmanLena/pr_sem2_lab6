package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;
import ru.lenok.common.models.LabWork;

import java.util.Map;

import static ru.lenok.common.LabWorkService.sortMapAndStringify;


public class FilterStartsWithNameCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public FilterStartsWithNameCommand(LabWorkService labWorkService) {
        super("filter_starts_with_name name ", "вывести элементы, значение поля name которых начинается с заданной подстроки");
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
