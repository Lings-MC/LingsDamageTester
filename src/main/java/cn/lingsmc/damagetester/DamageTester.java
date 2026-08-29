package cn.lingsmc.damagetester;

import cn.lingsmc.damagetester.commands.Commands;
import cn.lingsmc.damagetester.listener.AnyListener;
import cn.lingsmc.damagetester.utils.ConfigUtils;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Crsuh2er0
 * @since 2023/1/16
 */
public final class DamageTester extends JavaPlugin {
    @Getter
    private static DamageTester instance;

    private static void initInstance() {
        instance = JavaPlugin.getPlugin(DamageTester.class);
    }

    @Override
    public void onLoad() {
        initInstance();
        ConfigUtils.initialize();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // init commands
        final PluginCommand command = this.getCommand(instance.getName());
        assert command != null;
        Commands commands = new Commands();
        command.setExecutor(commands);
        command.setTabCompleter(commands);
        // init listeners
        AnyListener.initialize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
