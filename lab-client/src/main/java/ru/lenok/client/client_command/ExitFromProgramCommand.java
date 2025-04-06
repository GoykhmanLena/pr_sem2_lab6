package ru.lenok.client.client_command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.client.ClientApplication;
import ru.lenok.common.commands.AbstractCommand;

public class ExitFromProgramCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ExitFromProgramCommand.class);
    public ExitFromProgramCommand() {
        super("exit", "завершить программу клиента (без сохранения в файл)");
    }

    @Override
    public String execute(String arg) {
        logger.info("Завершаю программу клиента");
        System.exit(0);
        return EMPTY_RESULT;
    }
}
