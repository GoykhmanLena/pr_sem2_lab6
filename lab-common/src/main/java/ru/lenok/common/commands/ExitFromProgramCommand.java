package ru.lenok.common.commands;

public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super(CommandDefinition.exit, "завершить программу (без сохранения в файл)");
    }

    @Override
    public String execute() {
        return EMPTY_RESULT;
    }
}
