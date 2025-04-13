package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static ru.lenok.common.commands.CommandDefinition.save;

public class SaveToFileCommand extends AbstractCommand {
    LabWorkService labWorkService;

    public SaveToFileCommand(LabWorkService labWorkService) {
        super(save, "сохранить коллекцию в файл");
        this.labWorkService = labWorkService;
    }

    @Override
    public String execute() throws IOException {
        String json = labWorkService.getCollectionAsJson();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(labWorkService.getFileName()))) {
            writer.write(json);
        }
        return EMPTY_RESULT;
    }
}
