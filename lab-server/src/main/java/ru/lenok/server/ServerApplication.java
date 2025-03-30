package ru.lenok.server;

import ru.lenok.common.IInputProcessorProvider;
import ru.lenok.common.InputProcessor;
import ru.lenok.common.LabWorkService;
import ru.lenok.common.commands.CommandRegistry;
import ru.lenok.common.commands.IHistoryProvider;
import ru.lenok.common.input.AbstractInput;
import ru.lenok.common.input.ConsoleInput;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.util.HistoryList;
import ru.lenok.common.util.JsonReader;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;

import static java.lang.Math.max;
import static ru.lenok.common.LabWorkService.idCounter;

public class ServerApplication implements IInputProcessorProvider, IHistoryProvider {
    public static String filename;
    String[] args;
    private LabWorkService labWorkService;
    private CommandRegistry commandRegistry;
    private InputProcessor inputProcessor;
    private RequestHandler requestHandler;

    public ServerApplication(String[] args){
        this.args = args;
        init();
    }

    public void start() throws IOException {
        ServerConnector serverConnector= new ServerConnector(1818, requestHandler);
        serverConnector.listen();
        PrintStream ps = new PrintStream(System.out, true);
        System.setErr(ps);
/*
        try (AbstractInput input = new ConsoleInput()) {
            inputProcessor = new InputProcessor(labWorkService, commandRegistry);
            inputProcessor.processInput(input, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(collection);

 */
    }

    private void init() {
        initStorage();
        this.commandRegistry = new CommandRegistry(labWorkService, this, this);
        requestHandler = new RequestHandler(commandRegistry);
        inputProcessor = new InputProcessor(labWorkService, commandRegistry, requestHandler, requestHandler.getCommandController());
    }
    private void initStorage(){
        JsonReader jsonReader = new JsonReader();
        Hashtable<String, LabWork> map = new Hashtable<>();
        HashSet<Long> setOfId = new HashSet<>();
        if (args.length > 0) {
            filename = args[0];
            try {
                map = jsonReader.loadFromJson(filename);
                System.out.println("Файл успешно загружен");
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
                System.err.println("Программа завершается");
                System.exit(0);
            }
            for (LabWork labWork : map.values()) {
                idCounter = max(labWork.getId(), idCounter);
                setOfId.add(labWork.getId());
                if (!labWork.validate()) {
                    System.err.println("В файле еть данные, которые не соответствуют необходимым критериям, поэтому коллекция будет очищена");
                    map.clear();
                    idCounter = 0;
                    break;
                }
            }
            if (setOfId.size() < map.size()) {
                System.err.println("В файле есть повторяющиеся id, содержимое коллекции будет перезаписано");
                map.clear();
                idCounter = 0;
            }
            labWorkService = new LabWorkService(map);
        } else {
            System.out.println("Не был введен файл для чтения и записи");
            System.err.println("Программа завершается");
            System.exit(0);
        }
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        return requestHandler.getHistoryByClientID(clientID);
    }
}
