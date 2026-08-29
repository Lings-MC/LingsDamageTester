package cn.lingsmc.lingsdamagetester.listener;

import cn.lingsmc.lingsdamagetester.LingsDamageTester;
import cn.lingsmc.lingsdamagetester.manager.TestModeManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 玩家退出时清理测试模式状态，避免集合残留。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class QuitListener implements Listener {

    /**
     * 注册监听器（模板静态注册模式）。
     */
    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new QuitListener(), LingsDamageTester.getInstance());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TestModeManager.clear(event.getPlayer().getUniqueId());
    }
}
