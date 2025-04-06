package ru.lenok.common;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.commands.*;
import ru.lenok.common.input.AbstractInput;

import java.util.Stack;
@Deprecated
@Data
public class InputProcessor {
    private static final Logger logger = LoggerFactory.getLogger(InputProcessor.class);
    public static boolean debug = false;
    private final LabWorkService labWorkService;
    private final CommandRegistry commandRegistry;
    private final Stack<String> scriptExecutionContext;
    private final CommandController commandController;
    private final IHistoryProvider historyProvider;

    public InputProcessor(LabWorkService labWorkService, CommandRegistry commandRegistry, IHistoryProvider historyProvider, CommandController commandController) {
        this.scriptExecutionContext = new Stack<>();
        this.labWorkService = labWorkService;
        this.commandRegistry = commandRegistry;
        this.historyProvider = historyProvider;
        this.commandController = commandController;
        init();
    }
    public void processInput(AbstractInput input, boolean interactive) throws Exception
    {
        processInput(input, interactive, null);
    }
    public void processInput(AbstractInput input, boolean interactive, String clientID) throws Exception {
        String line;
        CommandWithArgument commandWithArgument = null;
        LabWorkItemAssembler labWorkItemAssembler = null;
        CommandRequest commandRequest = null;
        while ((line = input.readLine()) != null) {
            if (labWorkItemAssembler != null) {
                try {
                    labWorkItemAssembler.addNextLine(line);
                } catch (Exception e) {
                    handleException(interactive, e);
                    continue;
                }
                if (labWorkItemAssembler.isFinished()) {
                    commandRequest = new CommandRequest(
                            commandWithArgument,
                            labWorkItemAssembler.getLabWorkElement(),
                            clientID
                    );
                    CommandResponse commandResponse = sendRequest(commandRequest);
                    processResponse(commandResponse);
                    labWorkItemAssembler = null;
                }
                continue;
            }
            try {
                commandWithArgument = parseLineAsCommand(line);
            } catch (Exception e) {
                handleException(interactive, e);
                continue;
            }
            if (commandWithArgument.getCommandDefinition().isHasElement()) {
                labWorkItemAssembler = new LabWorkItemAssembler(interactive);
            } else {
                commandRequest = new CommandRequest(
                        commandWithArgument,
                        null,
                        clientID
                );
                CommandResponse commandResponse = sendRequest(commandRequest);
                processResponse(commandResponse);
            }
        }
        if (labWorkItemAssembler != null) {
            if (labWorkItemAssembler.isFinished()) {
                commandRequest = new CommandRequest(
                        commandWithArgument,
                        labWorkItemAssembler.getLabWorkElement(),
                        clientID
                );
                CommandResponse commandResponse = sendRequest(commandRequest);
                processResponse(commandResponse);
            } else {
                logger.error("Внимание! У вас в файле есть невыполненная последняя команда - недостаточно полей введено");
            }
        }
    }

    private void handleException(boolean interactive, Exception e) throws Exception {
        if (!interactive) {
            throw e;
        }
        displayCommonError(e);
    }

    private CommandResponse sendRequest(CommandRequest commandRequest) {
        historyProvider.getHistoryByClientID(commandRequest.getClientID()).addCommand(commandRequest.getCommandWithArgument().getCommandDefinition().getCommandName());
        return commandController.handle(commandRequest);
    }

    private void processResponse(CommandResponse commandResponse) {
        if (commandResponse.getError() == null) {
            logger.info(commandResponse.getOutput());
        } else {
            displayCommonError(commandResponse.getError());
        }

    }

    private CommandWithArgument parseLineAsCommand(String line) {
        String[] splittedLine = line.trim().split("\\s+");
        CommandWithArgument result = null;
        CommandName commandName;
        try {
            commandName = CommandName.valueOf(splittedLine[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Такой команды НЕТ: " + splittedLine[0], e);
        }
        AbstractCommand command = commandRegistry.getCommand(commandName);
        if (!command.hasArg() && splittedLine.length >= 2) {
            throw new IllegalArgumentException("Слишком много аргументов, ожидалось 0: " + line);
        } else if (command.hasArg() && (splittedLine.length == 1 || splittedLine.length > 2)) {
            throw new IllegalArgumentException("Неправильное колличество аргументов, ожидался 1: " + line);
        }
        CommandDefinition commandDefinition = commandRegistry.getCommandDefinition(commandName);
        result = new CommandWithArgument(commandDefinition, splittedLine.length == 2 ? splittedLine[1] : null);
        return result;
    }

    public void setScriptExecutionContext(String path) {
        scriptExecutionContext.push(path);
    }

    public void exitContext() {
        scriptExecutionContext.pop();
    }

    public boolean checkContext(String currentFile) {
        return scriptExecutionContext.contains(currentFile);
    }

    private void displayCommonError(Exception e) {
        logger.error("Ошибка: ", e);
    }

    void init() {
    }

    /*
    все команды создаются при инициализации - только один раз, при этом в конструктор передается LabWorkService (сервисный слой, который работает со storage - моя коллекция + crud)
    view - InputProcessor должен сформировать CommandRequest, отправить на контроллер CommandController
    CommandRequest состоит из имени команды, ее аргумента и полностью собранного LabWork элемента
    контроллер должен вызвать метод команды Command.execute и передать туда аргумент и LabWork
    Результат работы команды оборачивается в CommandResponse - строка или ошибка, и возвращается в InputProcessor

     */
}
