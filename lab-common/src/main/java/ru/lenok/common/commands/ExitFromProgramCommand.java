package ru.lenok.common.commands;

import ru.lenok.common.CommandResponse;

public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super(CommandDefinition.exit, "завершить программу (без сохранения в файл)");
    }

    @Override
    public CommandResponse execute() {
        return new CommandResponse(EMPTY_RESULT);
    }
}
