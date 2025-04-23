package ru.lenok.server.commands;

import ru.lenok.common.CommandRequest;
import ru.lenok.common.CommandResponse;
import ru.lenok.common.commands.AbstractCommand;
import ru.lenok.common.commands.Executable;

import java.io.IOException;

public class TimeLoggingCommandWrapper implements Executable {
    private final AbstractCommand delegate;

    public TimeLoggingCommandWrapper(AbstractCommand delegate) {
        this.delegate = delegate;
    }

    @Override
    public CommandResponse execute(CommandRequest req) throws IOException {
        long start = System.nanoTime();
        try {
            return delegate.execute(req);
        } finally {
            long end = System.nanoTime();
            System.out.println(delegate.getClass().getSimpleName() + " executed in " + (end - start) + " ms");
        }
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }
}
