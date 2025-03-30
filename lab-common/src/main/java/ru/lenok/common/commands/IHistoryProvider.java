package ru.lenok.common.commands;

import ru.lenok.common.util.HistoryList;

public interface IHistoryProvider {
    HistoryList getHistoryByClientID(String clientID);
}
