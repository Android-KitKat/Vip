package com.it5z.vip.util;

import com.it5z.vip.manager.LanguageManager;
import org.bukkit.command.CommandSender;

/**
 * Created by Administrator on 2014/12/12.
 */
public class LoggerUtil {
    private LoggerUtil() {}

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(LanguageManager.getLang("prefix") + message);
    }
}
