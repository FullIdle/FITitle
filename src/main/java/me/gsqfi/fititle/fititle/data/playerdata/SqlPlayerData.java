package me.gsqfi.fititle.fititle.data.playerdata;

import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SqlPlayerData implements IPlayerData {
    public final String url;
    public final String user;
    public final String password;

    public SqlPlayerData() {
        ConfigurationSection sql = CacheData.plugin.getConfig().getConfigurationSection("sql");
        this.url = sql.getString("url");
        this.user = sql.getString("user");
        this.password = sql.getString("password");
        this.verify();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    private void verify() {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String sql = "CREATE TABLE IF NOT EXISTS player_titles ( " +
                    "player_name VARCHAR(255) NOT NULL, " +
                    "player_title VARCHAR(255) NOT NULL, " +
                    "UNIQUE (player_name, player_title))";
            String sql2 = "CREATE TABLE IF NOT EXISTS player_now_title ( " +
                    "player_name VARCHAR(255) NOT NULL, " +
                    "player_title VARCHAR(255) NOT NULL, " +
                    "UNIQUE KEY unique_name (player_name))";
            try (
                    Statement statement = conn.createStatement()
            ) {
                statement.executeUpdate(sql);
                statement.executeUpdate(sql2);
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getPlayerTitles(String playerName) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT player_title FROM player_titles WHERE player_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement prepared = conn.prepareStatement(sql)) {
            prepared.setString(1, playerName);
            try (ResultSet rs = prepared.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("player_title"));
                }
            }
        } catch (SQLException e) {
            // 更合适的异常处理，比如记录日志
            e.printStackTrace();
        }
        if (!list.contains(CacheData.defaultTitle)) {
            list.add(CacheData.defaultTitle);
        }
        return list;
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        String sql = "SELECT player_title FROM player_now_title WHERE player_name = ?";
        String title = null;
        try (
                Connection conn = getConnection();
                PreparedStatement prepared = conn.prepareStatement(sql)
        ) {
            prepared.setString(1, playerName);
            try (ResultSet rs = prepared.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString("player_title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title == null ? CacheData.defaultTitle : title;
    }

    @Override
    public void setPlayerTitles(String playerName, List<String> titles) {
        String deleteSql = "DELETE FROM player_titles WHERE player_name = ?";
        titles.remove(CacheData.defaultTitle);
        titles = new ArrayList<>(new HashSet<>(titles));
        String insetSql = "INSERT INTO player_titles " +
                "(player_name, player_title) " +
                "VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "player_name = VALUES(player_name), " +
                "player_title = VALUES(player_title)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement delete = conn.prepareStatement(deleteSql);
                    PreparedStatement inset = conn.prepareStatement(insetSql)
            ) {
                delete.setString(1, playerName);
                delete.executeUpdate();
                for (String title : titles) {
                    inset.setString(1, playerName);
                    inset.setString(2, title);
                    inset.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void setNowPlayerTitle(String playerName, String title) {
        if (CacheData.defaultTitle.equals(title)){
            String sql = "DELETE FROM player_now_title WHERE player_name = ?";
            try (
                    Connection conn = getConnection();
                    PreparedStatement prepared = conn.prepareStatement(sql)
            ){
                prepared.setString(1,playerName);
                prepared.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        String insetSql = "INSERT INTO player_now_title " +
                "(player_name, player_title) " +
                "VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "player_name = VALUES(player_name), " +
                "player_title = VALUES(player_title)";
        try (Connection conn = getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement prepared = conn.prepareStatement(insetSql)){
                prepared.setString(1,playerName);
                prepared.setString(2,title);
                prepared.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> titles = getPlayerTitles(playerName);
        titles.add(title);
        setPlayerTitles(playerName,titles);
    }

    @Override
    public void save() {
    }
}
