package ru.lenok.common.commands;

import ru.lenok.common.IInputProcessorProvider;
import ru.lenok.common.InputProcessor;
import ru.lenok.common.LabWorkService;
import ru.lenok.common.input.AbstractInput;
import ru.lenok.common.input.MemoryInput;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ExecuteScriptCommand extends AbstractCommand {
    private final IInputProcessorProvider inputProcessorProvider;

    public ExecuteScriptCommand(LabWorkService labWorkService, IInputProcessorProvider inpPr) {
        super("execute_script file_name", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        this.inputProcessorProvider = inpPr;
    }

    public String execute(String arg) throws IOException {
        return "execute_script добавлен в историю";
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
