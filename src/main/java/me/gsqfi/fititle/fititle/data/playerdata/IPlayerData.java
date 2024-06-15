package me.gsqfi.fititle.fititle.data.playerdata;

import java.util.List;

public interface IPlayerData {
    List<String> getPlayerTitles(String playerName);

    String getNowPlayerTitle(String playerName);

    void setPlayerTitles(String playerName, String[] titles);

    void setNowPlayerTitle(String playerName, String title);

    void save();

    void release();
}
