package me.gsqfi.fititle.fititle.data.playerdata;

public class SqlPlayerData implements IPlayerData{

    public SqlPlayerData(){

    }

    @Override
    public String[] getPlayerTitles(String playerName) {
        return new String[0];
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        return "";
    }

    @Override
    public void setPlayerTitles(String playerName, String[] titles) {

    }

    @Override
    public void setNowPlayerTitle(String playerName, String string) {

    }

    @Override
    public void save() {

    }

    @Override
    public void release() {

    }
}
