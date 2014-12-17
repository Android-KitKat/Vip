package com.it5z.vip.manager;

import com.it5z.vip.Vip;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

/**
 * Created by Administrator on 2014/12/8.
 */
public class DatabaseManager {
    private static String userTableName;
    private static String tempSaveTableName;

    static {
        reloadTableName();
    }

    private DatabaseManager() {}

    public static Connection getConnection() {
        Connection conn = null;
        try {
            FileConfiguration config = Vip.getInstance().getConfig();
            String database = "jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(database, config.getString("mysql.user"), config.getString("mysql.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn) {
        if(conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(PreparedStatement ps) {
        if(ps == null) return;
        try {
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs) {
        if(rs == null) return;
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeSql(String sql) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(ps);
            close(conn);
        }
    }

    public static String getUserTableName() {
        return userTableName;
    }

    public static String getTempSaveTableName() {
        return tempSaveTableName;
    }

    public static void reloadTableName() {
        String prefix = Vip.getInstance().getConfig().getString("mysql.prefix");
        userTableName = prefix + "user";
        tempSaveTableName = prefix + "tempsave";
    }
}
