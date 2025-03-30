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

    public String execute(String arg, Map<String, List<String>> fileNameToContent, String clientID) throws IOException {
        InputProcessor inpPr = inputProcessorProvider.getInputProcessor();
        File file = new File(arg);
        System.out.println("-------------------- Начало выполнения файла: " + file.getCanonicalPath() + " ---------------------------------------------------------------------");
        if (inpPr.checkContext(file.getCanonicalPath())) {
            throw new IllegalArgumentException("Обнаружен ЦИКЛ, файл: " + file + " не будет открыт");
        }
        inpPr.setScriptExecutionContext(file.getCanonicalPath());
        try (AbstractInput memoryInput = new MemoryInput(fileNameToContent.get(arg))) {
            inpPr.processInput(memoryInput, false, clientID);
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении файла, проверьте что он существует");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Произошла ошибка,принудительное завершение чтения файла", e);
        } finally {
            inpPr.exitContext();
            System.out.println("-------------------- Конец выполнения файла: " + file.getCanonicalPath() + " ---------------------------------------------------------------------");
        }
        return EMPTY_RESULT;
    }

    @Override
    public String execute(String arg) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasArg() {
        return true;
    }
}
