package me.gsqfi.fititle.fititle.data.playerdata;

import lombok.SneakyThrows;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.List;

public class SqlPlayerData implements IPlayerData {
    public final String url;
    public final String user;
    public final String password;
    private final YamlPlayerData yamlData;

    @SneakyThrows
    public SqlPlayerData() {
        FileConfiguration config = CacheData.plugin.getConfig();
        this.url = config.getString("sql.url");
        this.user = config.getString("sql.user");
        this.password = config.getString("sql.password");
        Connection connection = getConnection();
        PreparedStatement prepared = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_data (value TEXT)");
        PreparedStatement prepared1 = connection.prepareStatement("SELECT value FROM player_data LIMIT 1");
        prepared.executeUpdate();
        ResultSet resultSet = prepared1.executeQuery();
        String data = resultSet.next()?resultSet.getString("value"):"";
        this.yamlData = new YamlPlayerData(data);
        prepared1.close();
        prepared.close();
        connection.close();
    }

    @SneakyThrows
    private Connection getConnection(){
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    @Override
    public List<String> getPlayerTitles(String playerName) {
        return this.yamlData.getPlayerTitles(playerName);
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        return this.yamlData.getNowPlayerTitle(playerName);
    }

    @Override
    public void setPlayerTitles(String playerName, String[] titles) {
        this.yamlData.setPlayerTitles(playerName,titles);
    }

    @Override
    public void setNowPlayerTitle(String playerName, String title) {
        this.yamlData.setNowPlayerTitle(playerName,title);
    }

    @SneakyThrows
    @Override
    public void save() {
        Connection connection = getConnection();
        String data = this.yamlData.saveToString();

        PreparedStatement prepared = connection.prepareStatement("TRUNCATE TABLE player_data");
        PreparedStatement prepared1 = connection.prepareStatement("INSERT INTO player_data (value) VALUES (?)");
        prepared1.setString(1,data);

        prepared.executeUpdate();
        prepared1.executeUpdate();

        prepared.close();
        prepared1.close();
        connection.close();
    }
}
