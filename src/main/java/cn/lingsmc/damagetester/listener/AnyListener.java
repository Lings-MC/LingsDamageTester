package cn.lingsmc.damagetester.listener;

import cn.lingsmc.damagetester.DamageTester;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author Crsuh2er0
 * @apiNote
 * @since 2023/1/31
 */
public class AnyListener implements Listener {
    static DamageTester plugin = DamageTester.getInstance();

    private AnyListener() {

    }

    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new AnyListener(), plugin);
    }
}
