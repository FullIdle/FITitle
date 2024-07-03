package me.gsqfi.fititle.fititle.data.playerdata;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import lombok.SneakyThrows;
import me.gsqfi.fititle.fititle.data.CacheData;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.PooledConnection;
import java.sql.*;
import java.util.*;

public class SqlPlayerData implements IPlayerData {
    public final MysqlConnectionPoolDataSource pool = new MysqlConnectionPoolDataSource();
    public PooledConnection pooledConnection;
    public final Map<String,String> player_now_title = new HashMap<>();
    public final Map<String,List<String>> player_titles = new HashMap<>();

    @SneakyThrows
    public SqlPlayerData() {
        ConfigurationSection sql = CacheData.plugin.getConfig().getConfigurationSection("sql");
        pool.setURL(sql.getString("url"));
        pool.setUser(sql.getString("user"));
        pool.setPassword(sql.getString("password"));
        pool.setUseUnicode(true);
        pool.setCharacterEncoding("utf-8");
        pool.setAutoReconnect(true);
        pooledConnection = pool.getPooledConnection();
        this.verify();
    }

    private Connection getConnection() throws SQLException {
        return this.pooledConnection.getConnection();
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
        if (!this.player_titles.containsKey(playerName)){
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
            this.player_titles.put(playerName,list);
        }else{
            list = this.player_titles.get(playerName);
        }
        return list;
    }

    @Override
    public String getNowPlayerTitle(String playerName) {
        String title = null;
        if (!this.player_now_title.containsKey(playerName)) {
            String sql = "SELECT player_title FROM player_now_title WHERE player_name = ?";
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
            title = title == null ? CacheData.defaultTitle : title;
            this.player_now_title.put(playerName,title);
        }else{
            title = this.player_now_title.get(playerName);
        }
        return title;
    }

    @Override
    public void setPlayerTitles(String playerName, List<String> titles) {
        this.player_titles.remove(playerName);
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
        this.player_now_title.remove(playerName);
        if (CacheData.defaultTitle.equals(title)||title == null){
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
        }else{
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
        }

        List<String> titles = getPlayerTitles(playerName);
        if (title != null){
            titles.add(title);
        }
        setPlayerTitles(playerName,titles);
    }

    @Override
    public void save() {
    }

    @SneakyThrows
    @Override
    public void release() {
        this.pooledConnection.close();
    }
}
