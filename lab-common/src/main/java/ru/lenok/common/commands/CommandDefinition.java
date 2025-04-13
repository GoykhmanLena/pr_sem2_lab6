package ru.lenok.common.commands;


import static ru.lenok.common.commands.ArgType.*;

public enum CommandDefinition {
    insert(STRING, true, false),
    exit(null, false, true),
    show(null, false, false),
    save(null, false, false),
    remove_key(STRING, false, false),
    update_id(LONG, true, false),
    print_ascending(null, false, false),
    remove_greater(null, true, false),
    replace_if_greater(STRING, true, false),
    filter_contains_description(STRING, false, false),
    filter_starts_with_name(STRING, false, false),
    help(null, false, false),
    info(null, false, false),
    clear(null, false, false),
    history(null, false, false),
    execute_script(STRING, false, true);
    private final ArgType argType;
    private final boolean hasElement;
    private final boolean isClient;
    CommandDefinition(ArgType argType, boolean hasElement, boolean isClient){
        this.argType = argType;
        this.hasElement = hasElement;
        this.isClient = isClient;
    }
    public boolean hasElement(){
        return this.hasElement;
    }
    public ArgType getArgType(){
        return this.argType;
    }
    public boolean hasArg(){return this.argType != null;}
    public boolean isClient(){
        return this.isClient;
    }
}
