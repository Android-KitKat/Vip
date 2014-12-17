package com.it5z.vip.manager;

import com.it5z.vip.Vip;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Administrator on 2014/12/12.
 */
public class LanguageManager {
    private static FileConfiguration langConfig;

    static {
        reloadLangConfig();
    }

    private LanguageManager() {}

    public static void reloadLangConfig() {
        Vip instance = Vip.getInstance();
        File langFolder = new File(instance.getDataFolder(), "lang");
        if(!langFolder.exists()) langFolder.mkdir();
        File langFile = new File(langFolder, instance.getConfig().getString("language") + ".yml");
        if(!langFile.exists()) langFile = new File(langFolder, "zh_cn.yml");
        if(!langFile.exists()) {
            try {
                Files.copy(instance.getResource("zh_cn.yml"), langFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public static String getLang(String path) {
        return langConfig != null ? langConfig.getString(path) : null;
    }
}
