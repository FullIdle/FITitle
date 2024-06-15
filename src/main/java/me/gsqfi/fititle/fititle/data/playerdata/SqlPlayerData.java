package me.gsqfi.fititle.fititle.data.playerdata;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import me.gsqfi.fititle.fititle.data.CacheData;
import me.gsqfi.fititle.fititle.events.PlayerTitleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlPlayerData implements IPlayerData {
    private static final Gson gson = new Gson();

    private final Connection connection;

    @SneakyThrows
    public SqlPlayerData() {
        FileConfiguration config = CacheData.plugin.getConfig();
        String url = config.getString("sql.url");
        String user = config.getString("sql.user");
        String password = config.getString("sql.password");
        CacheData.plugin.getLogger().info("§aConnecting to the database...");
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            CacheData.plugin.getLogger().info("§cFailed to connect to the database!");
            throw new RuntimeException(e);
        }
        CacheData.plugin.getLogger().info("§aThe database connection is successful!");
        Statement statement = this.connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS player_data (name VARCHAR(50),value TEXT);");
        statement.close();
    }

    @SneakyThrows
    public JsonObject getPlayerJsonData(String playerName) {
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT value FROM player_data WHERE name = '" + playerName + "'");
        JsonObject jsonObject = null;
        if (resultSet.next()) {
            jsonObject = gson.fromJson(resultSet.getString("value"), JsonObject.class);
        }
        resultSet.close();
        statement.close();
        return jsonObject;
    }

    @Override
    public List<String> getPlayerTitles(String playerName) {
        ArrayList<String> list = new ArrayList<>();
        JsonObject jsonData = getPlayerJsonData(playerName);
        if (jsonData != null && jsonData.has("titles")) {
            for (JsonElement title : jsonData.get("titles").getAsJsonArray()) {
                list.add(title.getAsString());
            }
        }
        if (!list.contains(CacheData.defaultTitle)) {
            list.add(CacheData.defaultTitle);
        }
        return list;
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        JsonObject jsonData = getPlayerJsonData(playerName);
        if (jsonData == null || !jsonData.has("now_title")) {
            return CacheData.defaultTitle;
        }
        return jsonData.get("now_title").getAsString();
    }

    @SneakyThrows
    @Override
    public void setPlayerTitles(String playerName, String[] titles) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.ALL,playerName, getNowPlayerTitle(playerName), titles);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        titles = event.getNewTitle();
        ArrayList<String> list = Lists.newArrayList(titles);
        list.remove(CacheData.defaultTitle);
        JsonObject jsonData = getPlayerJsonData(playerName);
        boolean b = jsonData == null;
        if (b) {
            jsonData = new JsonObject();
        }
        String sql = null;
        if (list.isEmpty()) {
            if (jsonData.has("titles")) {
                jsonData.remove("titles");
            }else if (!jsonData.has("now_title")){
                sql = "DELETE FROM player_data WHERE name = '" + playerName + "';";
            }
        } else {
            JsonArray jsonArray = new JsonArray();
            for (String s : list) {
                jsonArray.add(s);
            }
            jsonData.add("titles", jsonArray);
            String json = gson.toJson(jsonData);
            sql = b ? "INSERT INTO player_data (name,value) VALUES ('" + playerName + "', '" + json + "');"
                    : "UPDATE player_data SET value = '" + json + "' WHERE name = '" + playerName + "'";
        }
        if (sql != null){
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        }
    }

    @SneakyThrows
    @Override
    public void setNowPlayerTitle(String playerName, String title) {
        PlayerTitleChangeEvent event = new PlayerTitleChangeEvent(PlayerTitleChangeEvent.Type.NOW,playerName, getNowPlayerTitle(playerName), title);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        title = event.getNewTitle()[0];

        if (title.equals(CacheData.defaultTitle)) title = null;

        JsonObject jsonData = getPlayerJsonData(playerName);
        boolean b = jsonData == null;
        String sql = null;
        if (b){
            if (title == null) {
                return;
            }
            jsonData = new JsonObject();
            jsonData.addProperty("now_title",title);
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(title);
            jsonData.add("titles",jsonArray);
            sql = "INSERT INTO player_data (name,value) VALUES ('" + playerName + "', '" + gson.toJson(jsonData) + "');";
        }else{
            if (title == null){
                jsonData.remove("now_title");
            }else{
                jsonData.addProperty("now_title",title);
                JsonArray jsonArray = new JsonArray();
                if (jsonData.has("titles")) {
                    jsonArray.addAll(jsonData.get("titles").getAsJsonArray());
                }
                jsonArray.add(title);
                jsonData.add("titles",jsonArray);
            }
            //判断是否还有数据
            String json = gson.toJson(jsonData);
            if ((!jsonData.has("now_title") && !jsonData.has("titles"))
                || (!jsonData.has("now_title") && jsonData.get("titles").getAsJsonArray().size() == 0)){
                sql = "DELETE FROM player_data WHERE name = '" + playerName + "';";
            }else{
                sql = "UPDATE player_data SET value = '" + json + "' WHERE name = '" + playerName + "'";
            }
        }
        if (sql != null){
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        }
    }

    @Override
    public void save() {
    }

    @SneakyThrows
    @Override
    public void release() {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }
}
