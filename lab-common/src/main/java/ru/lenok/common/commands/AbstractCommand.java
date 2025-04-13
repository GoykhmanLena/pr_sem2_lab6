package ru.lenok.common.commands;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.lenok.common.models.LabWork;

import java.io.IOException;

@Data
@AllArgsConstructor
public abstract class AbstractCommand {
    public static final String EMPTY_RESULT = "";
    private final CommandDefinition commandDefinition;
    private final String description;

    public String execute(String arg) throws IOException{
        throw new UnsupportedOperationException();
    }
    public String execute(String argument, LabWork element) throws IOException{
        throw new UnsupportedOperationException();
    }
    public String execute() throws IOException{
        throw new UnsupportedOperationException();
    }
    public String execute(LabWork element) throws IOException{
        throw new UnsupportedOperationException();
    }

    public boolean hasElement() {
        return commandDefinition.hasElement();
    }
    public boolean hasArg() {
        return commandDefinition.getArgType() != null;
    }
    public boolean isClientCommand() {
        return commandDefinition.isClient();
    }
    public CommandDefinition getCommandDefinition(){
        return commandDefinition;
    }

}
