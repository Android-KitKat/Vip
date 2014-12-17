package com.it5z.vip.listener;

import com.it5z.vip.manager.VipManager;
import com.it5z.vip.model.VipUser;
import com.it5z.vip.util.VipUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Administrator on 2014/12/11.
 */
public class PlayerListener implements Listener {
    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer(); //获取玩家
        String name = player.getName(); //获取玩家名称
        if(player.hasPermission("vip.check.ignore")) return; //如果拥有权限则忽略检查
        VipUser user = VipManager.getUser(name); //获取用户信息模型
        VipUtil.checkUser(user); //检查用户
    }
}
