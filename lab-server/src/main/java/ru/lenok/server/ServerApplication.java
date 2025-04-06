package ru.lenok.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lenok.common.*;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.CommandName;
import ru.lenok.common.commands.CommandRegistry;
import ru.lenok.common.commands.IHistoryProvider;
import ru.lenok.common.models.LabWork;
import ru.lenok.common.util.HistoryList;
import ru.lenok.common.util.IdCounterService;
import ru.lenok.common.util.JsonReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import static java.lang.Math.max;

public class ServerApplication implements IInputProcessorProvider, IHistoryProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);
    private final String filename;
    private LabWorkService labWorkService;
    private CommandRegistry commandRegistry;
    private InputProcessor inputProcessor;
    private RequestHandler requestHandler;
    private final int port;

    public ServerApplication(String file, int port) {
        this.filename = file;
        this.port = port;
        init();
    }

    public void start() throws IOException {
        logger.info("Сервер работает");
        ServerConnector serverConnector = new ServerConnector(port, requestHandler);
        serverConnector.listen();
    }

    private void init() {
        initStorage();
        this.commandRegistry = new CommandRegistry(labWorkService, this, this);
        requestHandler = new RequestHandler(commandRegistry);
        inputProcessor = new InputProcessor(labWorkService, commandRegistry, requestHandler, requestHandler.getCommandController());

        handleSaveOnTerminate();
    }

    private void handleSaveOnTerminate() {
        // Регистрируем Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Сервер завершает работу, коллекция сохраняется. Обрабатываем событие Ctrl + C.");
            CommandWithArgument commandWithArgument = new CommandWithArgument(commandRegistry.getCommandDefinition(CommandName.save), "");
            CommandRequest commandRequest = new CommandRequest(commandWithArgument, null, null);
            requestHandler.getCommandController().handle(commandRequest);
        }));
    }

    private void initStorage() {
        JsonReader jsonReader = new JsonReader();
        Hashtable<String, LabWork> map = new Hashtable<>();
        HashSet<Long> setOfId = new HashSet<>();
        try {
            map = jsonReader.loadFromJson(filename);
            logger.info("Файл успешно загружен: {}", filename);
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла: {}", e.getMessage());
            logger.error("Программа завершается");
            System.exit(1);
        }
        for (LabWork labWork : map.values()) {
            IdCounterService.setId(max(labWork.getId(), IdCounterService.getId()));
            setOfId.add(labWork.getId());
            if (!labWork.validate()) {
                logger.warn("Некорректные данные в файле — коллекция будет очищена");
                map.clear();
                IdCounterService.setId(0);
                break;
            }
        }
        if (setOfId.size() < map.size()) {
            logger.warn("В файле есть повторяющиеся id — коллекция будет очищена");
            map.clear();
            IdCounterService.setId(0);
        }
        labWorkService = new LabWorkService(map, filename);
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
