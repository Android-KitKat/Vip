package com.it5z.vip.manager;

import com.it5z.vip.model.VipUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/12/10.
 */
public class VipManager {
    private VipManager() {}

    public static void addUser(VipUser user) {
        if (user == null) return;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("INSERT INTO " + DatabaseManager.getUserTableName() + "(name, base, type, update_time, valid_time, base_valid) VALUES(?, ?, ?, ?, ?, ?)");
            ps.setString(1, user.getName());
            ps.setString(2, user.getBase());
            ps.setString(3, user.getType());
            ps.setTimestamp(4, user.getUpdateTime());
            ps.setTimestamp(5, user.getValidTime());
            ps.setBoolean(6, user.isBaseValid());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
    }

    public static void updateUser(VipUser user) {
        if (!isUser(user.getName())) return;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("UPDATE " + DatabaseManager.getUserTableName() + " SET name = ?, base = ?, type = ?, update_time = ?, valid_time = ?, base_valid = ? WHERE name = ?");
            ps.setString(1, user.getName());
            ps.setString(2, user.getBase());
            ps.setString(3, user.getType());
            ps.setTimestamp(4, user.getUpdateTime());
            ps.setTimestamp(5, user.getValidTime());
            ps.setBoolean(6, user.isBaseValid());
            ps.setString(7, user.getName());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
    }

    public static VipUser getUser(String name) {
        VipUser user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + DatabaseManager.getUserTableName() + " WHERE name = ? LIMIT 1");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new VipUser(rs.getString("name"), rs.getString("base"), rs.getString("type"), rs.getTimestamp("update_time"), rs.getTimestamp("valid_time"), rs.getBoolean("base_valid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return user;
    }

    public static List<VipUser> getVips(int page, int row) {
        if(page < 1 || row < 0) throw new IllegalArgumentException("Invalid value");
        List<VipUser> vips = new ArrayList<VipUser>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + DatabaseManager.getUserTableName() + " WHERE valid_time > now() LIMIT ?, ?");
            ps.setInt(1, (page - 1) * row);
            ps.setInt(2, row);
            rs = ps.executeQuery();
            while(rs.next()) {
                vips.add(new VipUser(rs.getString("name"), rs.getString("base"), rs.getString("type"), rs.getTimestamp("update_time"), rs.getTimestamp("valid_time"), rs.getBoolean("base_valid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return vips;
    }

    public static int getVipsCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(valid_time) FROM " + DatabaseManager.getUserTableName() + " WHERE valid_time > now() LIMIT 1");
            rs = ps.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return count;
    }

    public static List<VipUser> getLongVips(int page, int row) {
        if(page < 1 || row < 0) throw new IllegalArgumentException("Invalid value");
        List<VipUser> users = new ArrayList<VipUser>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + DatabaseManager.getUserTableName() + " WHERE base IS NOT NULL AND base_valid = TRUE LIMIT ?, ?");
            ps.setInt(1, (page - 1) * row);
            ps.setInt(2, row);
            rs = ps.executeQuery();
            while(rs.next()) {
                users.add(new VipUser(rs.getString("name"), rs.getString("base"), rs.getString("type"), rs.getTimestamp("update_time"), rs.getTimestamp("valid_time"), rs.getBoolean("base_valid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return users;
    }

    public static int getLongVipsCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(base) FROM " + DatabaseManager.getUserTableName() + " WHERE base_valid = TRUE LIMIT 1");
            rs = ps.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return count;
    }

    public static boolean isUser(String userName) {
        boolean isUser = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT name FROM " + DatabaseManager.getUserTableName() + " WHERE name = ? LIMIT 1");
            ps.setString(1, userName);
            rs = ps.executeQuery();
            isUser = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return isUser;
    }

    public static void saveUser(String userName, String vipName, long time) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + DatabaseManager.getTempSaveTableName() + " WHERE name = ? AND type = ? LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, userName);
            ps.setString(2, vipName);
            rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateLong("time", rs.getLong("time") + time);
                rs.updateRow();
            } else {
                DatabaseManager.close(ps);
                ps = conn.prepareStatement("INSERT INTO " + DatabaseManager.getTempSaveTableName() + "(name, type, time) VALUES(?, ?, ?)");
                ps.setString(1, userName);
                ps.setString(2, vipName);
                ps.setLong(3, time);
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
    }

    public static Map<String, Long> getSaves(String userName) {
        Map<String, Long> saves = new HashMap<String, Long>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + DatabaseManager.getTempSaveTableName() + " WHERE name = ?");
            ps.setString(1, userName);
            rs = ps.executeQuery();
            while(rs.next()) {
                saves.put(rs.getString("type"), rs.getLong("time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return saves;
    }

    public static boolean isSave(String userName, String vipName) {
        boolean isSave = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("SELECT name FROM " + DatabaseManager.getTempSaveTableName() + " WHERE name = ? AND type = ? LIMIT 1");
            ps.setString(1, userName);
            ps.setString(2, vipName);
            rs = ps.executeQuery();
            isSave = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(rs);
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
        return isSave;
    }

    public static void removeSave(String userName, String vipName) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseManager.getConnection();
            ps = conn.prepareStatement("DELETE FROM " + DatabaseManager.getTempSaveTableName() + " WHERE name = ? AND type = ?");
            ps.setString(1, userName);
            ps.setString(2, vipName);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close(ps);
            DatabaseManager.close(conn);
        }
    }
}
