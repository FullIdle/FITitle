package me.gsqfi.fititle.fititle.data.playerdata;

public interface IPlayerData {
    String[] getPlayerTitles(String playerName);

    String getNowPlayerTitle(String playerName);

    void setPlayerTitles(String playerName, String[] titles);

    void setNowPlayerTitle(String playerName, String title);

    void save();

    void release();
}
