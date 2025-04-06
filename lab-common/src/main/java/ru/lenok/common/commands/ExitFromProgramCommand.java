package ru.lenok.common.commands;

public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super("exit", "завершить программу (без сохранения в файл)");
    }

    @Override
    public String execute(String arg) {
        return EMPTY_RESULT;
    }

    @Override
    public boolean isClientCommand() {
        return true;
    }
}
