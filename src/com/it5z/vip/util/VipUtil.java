package com.it5z.vip.util;

import com.it5z.vip.Vip;
import com.it5z.vip.manager.VipManager;
import com.it5z.vip.model.VipUser;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/12/10.
 */
public class VipUtil {
    private VipUtil() {}

    public static void applyVip(String name, String vip) {
        applyVip(name, vip, false);
    }

    public static void applyVip(String name, String vip, boolean revoke) {
        List<String> groups = Vip.getInstance().getConfig().getStringList("vips." + vip + ".groups");
        if(groups == null) return;
        String[] data;
        for(String line:groups) {
            data = line.split(":", 3);
            updateGroup(data[0], name, data[revoke ? 2 : 1]);
        }
    }

    private static boolean inGroup(String worldName, String playerName, String groupName) {
        boolean inGroup = false;
        OverloadedWorldHolder handler = getGroupHandler(worldName);
        if(handler != null) {
            inGroup = handler.getUser(playerName).getGroupName().equalsIgnoreCase(groupName);
        }
        return inGroup;
    }

    private static void updateGroup(String worldName, String playerName, String groupName) {
        if(inGroup(worldName, playerName, groupName)) return;
        OverloadedWorldHolder handler = getGroupHandler(worldName);
        if(handler != null && handler.groupExists(groupName)) {
            handler.getUser(playerName).setGroup(handler.getGroup(groupName));
        }
    }

    private static OverloadedWorldHolder getGroupHandler(String worldName) {
        OverloadedWorldHolder handler = null;
        GroupManager gm = (GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager");
        if(gm != null && gm.isEnabled()) {
            handler = gm.getWorldsHolder().getWorldData(worldName);
        }
        return handler;
    }

    public static void checkUser(VipUser user) {
        if(user == null) return;
        String name = user.getName();
        Map<String, Long> saves = VipManager.getSaves(name);
        String vip = isValid(user) ? user.getType() : null;
        for(String type:saves.keySet()) {
            if(vip == null || getVipRank(type) > getVipRank(vip)) {
                vip = type;
            }
        }
        if(vip != null && saves.containsKey(vip)) {
            if(vip.equalsIgnoreCase(user.getType()) && isValid(user)) {
                addTime(user, saves.get(vip));
            } else {
                if(isValid(user)) {
                    VipManager.saveUser(name, user.getType(), user.getValidTime().getTime() - System.currentTimeMillis());
                }
                user.setType(vip);
                user.setValidTime(new Timestamp(System.currentTimeMillis() + saves.get(vip)));
                VipManager.removeSave(name, vip);
            }
            VipManager.updateUser(user);
        }
        String base = user.getBase();
        String type = user.getType();
        if(isValid(user)) {
            applyVip(name, getVipRank(base) > getVipRank(type) ? (user.isBaseValid() ? base : type) : type);
        } else if(user.isBaseValid()) {
            applyVip(name, base);
        } else {
            applyVip(name, getVipRank(base) > getVipRank(type) ? base : type, true);
        }
    }

    public static boolean isValid(VipUser user) {
        if(user == null) return false;
        Timestamp date = user.getValidTime();
        return date != null && date.getTime() > System.currentTimeMillis();
    }

    public static void addTime(VipUser user, long time) {
        user.setValidTime(new Timestamp(user.getValidTime().getTime() + time));
    }

    public static int getVipRank(String vipName) {
        return vipName != null ? Vip.getInstance().getConfig().getInt("vips." + vipName + ".rank") : 0;
    }

    public static String getVipName(String vipName) {
        String path = "vips." + vipName + ".name";
        FileConfiguration config = Vip.getInstance().getConfig();
        return config.isString(path) ? config.getString(path) : vipName;
    }

    public static void addVip(String userName, String vipName, long time) {
        if(userName == null || userName.isEmpty() || vipName == null || vipName.isEmpty()) return;
        if(VipManager.isUser(userName)) {
            VipUser user = VipManager.getUser(userName);
            if(vipName.equalsIgnoreCase(user.getType())) {
                user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                addTime(user, time);
                VipManager.updateUser(user);
            } else if(isValid(user) && getVipRank(user.getType()) > getVipRank(vipName)) {
                VipManager.saveUser(userName, vipName, time);
            } else {
                if(user.getType() != null && isValid(user)) {
                    VipManager.saveUser(user.getName(), user.getType(), user.getValidTime().getTime() - System.currentTimeMillis());
                }
                user.setType(vipName);
                user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                user.setValidTime(new Timestamp(System.currentTimeMillis() + time));
                VipManager.updateUser(user);
            }
        } else {
            VipUser user = new VipUser(userName, null, vipName, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + time), false);
            VipManager.addUser(user);
        }
    }

    public static boolean delVip(String userName, String vipName) {
        VipUser user = VipManager.getUser(userName);
        if(user != null && user.getType().equalsIgnoreCase(vipName) && isValid(user)) {
            user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            user.setValidTime(new Timestamp(System.currentTimeMillis()));
            VipManager.updateUser(user);
        } else if(VipManager.isSave(userName, vipName)) {
            VipManager.removeSave(userName, vipName);
        } else {
            return false;
        }
        return true;
    }

    public static void setLongVip(String userName, String vipName) {
        if(VipManager.isUser(userName)) {
            VipUser user = VipManager.getUser(userName);
            user.setBase(vipName);
            user.setBaseValid(true);
            VipManager.updateUser(user);
        } else {
            VipUser user = new VipUser(userName, vipName, null, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), true);
            VipManager.addUser(user);
        }
    }

    public static boolean delLongVip(String userName) {
        VipUser user = VipManager.getUser(userName);
        if(user != null && user.getBase() != null && user.isBaseValid()) {
            user.setBaseValid(false);
            VipManager.updateUser(user);
            return true;
        }
        return false;
    }
}
