package com.it5z.vip;

import com.it5z.vip.listener.PlayerListener;
import com.it5z.vip.manager.DatabaseManager;
import com.it5z.vip.manager.LanguageManager;
import com.it5z.vip.manager.VipManager;
import com.it5z.vip.model.VipUser;
import com.it5z.vip.util.DateUtil;
import com.it5z.vip.util.LoggerUtil;
import com.it5z.vip.util.NumberUtil;
import com.it5z.vip.util.VipUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/12/8.
 */
public class Vip extends JavaPlugin {
    private static Vip instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        initSQL();
    }

    @Override
    public void onEnable() {
        registerRepeatingTask();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        this.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("vip")) {
            if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if(sender.hasPermission("vip.help")) {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_title"));
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_help"));
                    if(sender.hasPermission("vip.add")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_add"));
                    if(sender.hasPermission("vip.del")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_del"));
                    if(sender.hasPermission("vip.setlong")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_setlong"));
                    if(sender.hasPermission("vip.dellong")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_dellong"));
                    if(sender.hasPermission("vip.list")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_list"));
                    if(sender.hasPermission("vip.listlong")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_listlong"));
                    if(sender.hasPermission("vip.info")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_info"));
                    if(sender.hasPermission("vip.reload")) LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_reload"));
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.help"));
                }
            } else if(args[0].equalsIgnoreCase("add")) {
                if(sender.hasPermission("vip.add")) {
                    if(args.length == 3 || args.length == 4) {
                        try {
                            long time = DateUtil.toTimeValue(args.length == 4 ? args[3] : "30d");
                            if(time > 0) {
                                VipUtil.addVip(args[1], args[2], time);
                                VipUtil.checkUser(VipManager.getUser(args[1]));
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("add_message").replaceAll("%player%", args[1]).replaceAll("%vip%", args[2]).replaceAll("%time%", DateUtil.toTimeString(time)));
                                Bukkit.broadcastMessage(LanguageManager.getLang("vip_broadcast").replaceAll("%player%", args[1]).replaceAll("%vip%", VipUtil.getVipName(args[2])).replaceAll("%time%", DateUtil.toTimeString(time)));
                            } else {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("time_can_not_lt_zero"));
                            }
                        } catch(IllegalArgumentException e) {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("args_invalid"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_add"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.add"));
                }
            } else if(args[0].equalsIgnoreCase("del")) {
                if(sender.hasPermission("vip.del")) {
                    if(args.length == 2 || args.length == 3) {
                        if(VipManager.isUser(args[1])) {
                            String vipName = args.length == 3 ? args[2] : VipManager.getUser(args[1]).getType();
                            if(VipUtil.delVip(args[1], vipName)) {
                                VipUtil.checkUser(VipManager.getUser(args[1]));
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("del_message").replaceAll("%player%", args[1]).replaceAll("%vip%", vipName));
                            } else {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("user_no_this_vip"));
                            }
                        } else {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_user_info"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_del"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.del"));
                }
            } else if(args[0].equalsIgnoreCase("setlong")) {
                if(sender.hasPermission("vip.setlong")) {
                    if(args.length == 3) {
                        VipUtil.setLongVip(args[1], args[2]);
                        VipUtil.checkUser(VipManager.getUser(args[1]));
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("setlong_message").replaceAll("%player%", args[1]).replaceAll("%vip%", args[2]));
                        Bukkit.broadcastMessage(LanguageManager.getLang("vip_broadcast").replaceAll("%player%", args[1]).replaceAll("%vip%", VipUtil.getVipName(args[2])).replaceAll("%time%", "永久"));
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_setlong"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.setlong"));
                }
            } else if(args[0].equalsIgnoreCase("dellong")) {
                if(sender.hasPermission("vip.dellong")) {
                    if(args.length == 2) {
                        if(VipManager.isUser(args[1])) {
                            String vipName = VipManager.getUser(args[1]).getBase();
                            if(VipUtil.delLongVip(args[1])) {
                                VipUtil.checkUser(VipManager.getUser(args[1]));
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("dellong_message").replaceAll("%player%", args[1]).replaceAll("%vip%", vipName));
                            } else {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("user_no_this_long_vip"));
                            }
                        } else {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_user_info"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_dellong"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.dellong"));
                }
            } else if(args[0].equalsIgnoreCase("list")) {
                if(sender.hasPermission("vip.list")) {
                    if(args.length == 1 || args.length == 2) {
                        try {
                            int page = args.length == 2 ? Integer.parseInt(args[1]) : 1;
                            List<VipUser> vips = VipManager.getVips(page, 9);
                            if (!vips.isEmpty()) {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("list_title").replaceAll("%page%", String.valueOf(page)).replaceAll("%count%", String.valueOf(NumberUtil.getPageCount(VipManager.getVipsCount(), 9))));
                                for (VipUser user:vips) {
                                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("list_vips").replaceAll("%player%", user.getName()).replaceAll("%vip%", user.getType()).replaceAll("%date%", DateUtil.toDateString(user.getValidTime().getTime())));
                                }
                            } else {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("this_page_no_content"));
                            }
                        } catch(NumberFormatException e) {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("number_format_error"));
                        } catch (IllegalArgumentException e) {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("args_invalid"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_list"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.list"));
                }
            } else if(args[0].equalsIgnoreCase("listlong")) {
                if(sender.hasPermission("vip.listlong")) {
                    if(args.length == 1 || args.length == 2) {
                        try {
                            int page = args.length == 2 ? Integer.parseInt(args[1]) : 1;
                            List<VipUser> vips = VipManager.getLongVips(page, 9);
                            if(!vips.isEmpty()) {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("listlong_title").replaceAll("%page%", String.valueOf(page)).replaceAll("%count%", String.valueOf(NumberUtil.getPageCount(VipManager.getLongVipsCount(), 9))));
                                for (VipUser user:vips) {
                                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("listlong_vips").replaceAll("%player%", user.getName()).replaceAll("%vip%", user.getBase()));
                                }
                            } else {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("this_page_no_content"));
                            }
                        } catch(NumberFormatException e) {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("number_format_error"));
                        } catch (IllegalArgumentException e) {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("args_invalid"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang("help_listlong"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.listlong"));
                }
            } else if(args[0].equalsIgnoreCase("info")) {
                if(sender.hasPermission(args.length == 2 && !sender.getName().equalsIgnoreCase(args[1]) ? "vip.info.others" : "vip.info")) {
                    if((sender instanceof Player) ? args.length == 1 || args.length == 2 : args.length == 2) {
                        String name = args.length == 2 ? args[1] : sender.getName();
                        if(VipManager.isUser(name)) {
                            VipUser user = VipManager.getUser(name);
                            Map<String, Long> saves = VipManager.getSaves(name);
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_title"));
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_name").replaceAll("%player%", user.getName()));
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_base").replaceAll("%vip%", user.getBase() == null || !user.isBaseValid() ? LanguageManager.getLang("none") : user.getBase()));
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_type").replaceAll("%vip%", user.getType() == null || !VipUtil.isValid(user) ? LanguageManager.getLang("none") : user.getType()));
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_saves") + (saves.isEmpty() ? " " + LanguageManager.getLang("none") : ""));
                            for(String vip:saves.keySet()) {
                                LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_saves_vips").replaceAll("%vip%", vip).replaceAll("%time%", DateUtil.toTimeString(saves.get(vip))));
                            }
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_update_time").replaceAll("%date%", DateUtil.toDateString(user.getUpdateTime().getTime())));
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("info_valid_time").replaceAll("%date%", DateUtil.toDateString(user.getValidTime().getTime())));
                        } else {
                            LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_user_info"));
                        }
                    } else {
                        LoggerUtil.sendMessage(sender, LanguageManager.getLang(args.length == 1 ? "console_must_point_player" : "help_info"));
                    }
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", args.length == 2 ? "vip.info.others" : "vip.info"));
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(sender.hasPermission("vip.reload")) {
                    saveDefaultConfig();
                    this.reloadConfig();
                    registerRepeatingTask();
                    DatabaseManager.reloadTableName();
                    initSQL();
                    LanguageManager.reloadLangConfig();
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("reload_message"));
                } else {
                    LoggerUtil.sendMessage(sender, LanguageManager.getLang("no_permission").replaceAll("%permission%", "vip.reload"));
                }
            } else {
                LoggerUtil.sendMessage(sender, LanguageManager.getLang("command_not_exist"));
            }
            return true;
        }
        return false;
    }

    public static Vip getInstance() {
        return instance;
    }

    public void initSQL() {
        String userTable = "CREATE TABLE IF NOT EXISTS " + DatabaseManager.getUserTableName() + "(name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci PRIMARY KEY NOT NULL, base VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci, type VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci, update_time TIMESTAMP NOT NULL, valid_time TIMESTAMP NOT NULL, base_valid BOOLEAN NOT NULL)";
        String tempSaveTable = "CREATE TABLE IF NOT EXISTS " + DatabaseManager.getTempSaveTableName() + "(name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, type VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, time BIGINT NOT NULL, PRIMARY KEY(name, type))";
        DatabaseManager.executeSql(userTable);
        DatabaseManager.executeSql(tempSaveTable);
    }

    public void registerRepeatingTask() {
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player p:getServer().getOnlinePlayers()) {
                    if(p.hasPermission("vip.check.ignore")) continue;
                    VipUtil.checkUser(VipManager.getUser(p.getName()));
                }
            }
        }, 0L, getConfig().getLong("interval") * 1200L);
    }
}
