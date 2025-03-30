package ru.lenok.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CommandDefinition implements Serializable {
    private final CommandName commandName;
    private final boolean hasArg;
    private final boolean hasElement;
    private final boolean preprocess;
}
