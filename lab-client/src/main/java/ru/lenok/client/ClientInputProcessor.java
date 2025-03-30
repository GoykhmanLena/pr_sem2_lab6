package ru.lenok.client;

import lombok.Data;
import ru.lenok.common.*;
import ru.lenok.common.commands.CommandDefinition;
import ru.lenok.common.commands.CommandName;
import ru.lenok.common.input.AbstractInput;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Data

public class ClientInputProcessor {
    public static boolean debug = true;
    private Map<CommandName, CommandDefinition> commandDefinitions;
    private Stack<String> scriptExecutionContext;
    private ClientConnector clientConnector;
    private final String clientID;

    public ClientInputProcessor(Map<CommandName, CommandDefinition> commandDefinitions, ClientConnector clientConnector, String clientID) {
        this.scriptExecutionContext = new Stack<>();
        this.commandDefinitions = commandDefinitions;
        this.clientConnector = clientConnector;
        this.clientID = clientID;
    }

    public void processInput(AbstractInput input, boolean interactive) throws Exception {
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
                    commandRequest = new CommandRequest(commandWithArgument, labWorkItemAssembler.getLabWorkElement(), clientID, null);
                    CommandResponse commandResponse = clientConnector.sendCommand(commandRequest);
                    processResponse(commandResponse);
                    labWorkItemAssembler = null;
                }
                continue;
            }
            try{
                commandWithArgument = parseLineAsCommand(line);
            } catch (Exception e) {
                handleException(interactive, e);
                continue;
            }
            if (commandWithArgument.getCommandDefinition().isHasElement()) {
                labWorkItemAssembler = new LabWorkItemAssembler(interactive);
            } else {
                Map <String, List<String>>filesMap = null;
                if (commandWithArgument.getCommandDefinition().getCommandName() == CommandName.execute_script){
                    filesMap = new HashMap<>();
                    String filePath = commandWithArgument.getArgument();
                    List<String> fileContent = Files.readAllLines(Paths.get(filePath));
                    filesMap.put(filePath, fileContent);
                }
                commandRequest = new CommandRequest(commandWithArgument, null, clientID, filesMap);
                CommandResponse commandResponse = clientConnector.sendCommand(commandRequest);
                processResponse(commandResponse);
            }
        }
        if (labWorkItemAssembler != null) {
            if (labWorkItemAssembler.isFinished()) {
                commandRequest = new CommandRequest(commandWithArgument, labWorkItemAssembler.getLabWorkElement(), clientID, null);
                CommandResponse commandResponse = clientConnector.sendCommand(commandRequest);
                processResponse(commandResponse);
            } else {
                System.err.println("Внимание! У вас в файле есть невыполненная последняя команда - недостаточно полей введено");
            }
        }
    }

    private void handleException(boolean interactive, Exception e) throws Exception {
        if (!interactive) {
            throw e;
        }
        displayCommonError(e);
    }

    private void processResponse(CommandResponse commandResponse) {
        if (commandResponse.getError() == null) {
            System.out.println(commandResponse.getOutput());
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
        CommandDefinition commandDefinition = commandDefinitions.get(commandName);
        if (!commandDefinition.isHasArg() && splittedLine.length >= 2) {
            throw new IllegalArgumentException("Слишком много аргументов, ожидалось 0: " + line);
        } else if (commandDefinition.isHasArg() && (splittedLine.length == 1 || splittedLine.length > 2)) {
            throw new IllegalArgumentException("Неправильное колличество аргументов, ожидался 1: " + line);
        }
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
        if (debug) {
            e.printStackTrace();
        } else {
            System.err.println(e.getMessage());
        }
    }

    /*
    все команды создаются при инициализации - только один раз, при этом в конструктор передается LabWorkService (сервисный слой, который работает со storage - моя коллекция + crud)
    view - InputProcessor должен сформировать CommandRequest, отправить на контроллер CommandController
    CommandRequest состоит из имени команды, ее аргумента и полностью собранного LabWork элемента
    контроллер должен вызвать метод команды Command.execute и передать туда аргумент и LabWork
    Результат работы команды оборачивается в CommandResponse - строка или ошибка, и возвращается в InputProcessor

     */
}
