package ru.lenok.common.commands;

import ru.lenok.common.LabWorkService;

import java.io.IOException;


public class ExecuteScriptCommand extends AbstractCommand {

    public ExecuteScriptCommand(LabWorkService labWorkService) {
        super("execute_script file_name", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    public String execute(String arg) throws IOException {
        return "execute_script добавлен в историю";
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
