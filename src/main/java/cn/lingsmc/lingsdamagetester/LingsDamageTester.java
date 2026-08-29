package cn.lingsmc.lingsdamagetester;

import cn.lingsmc.lingsdamagetester.commands.Commands;
import cn.lingsmc.lingsdamagetester.listener.DamageListener;
import cn.lingsmc.lingsdamagetester.listener.QuitListener;
import cn.lingsmc.lingsdamagetester.utils.ConfigUtils;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Crsuh2er0
 * @since 2023/1/16
 */
public final class LingsDamageTester extends JavaPlugin {
    @Getter
    private static LingsDamageTester instance;

    private static void initInstance() {
        instance = JavaPlugin.getPlugin(LingsDamageTester.class);
    }

    @Override
    public void onLoad() {
        initInstance();
        ConfigUtils.initialize();
    }

    @Override
    public void onEnable() {
        // 初始化命令
        final PluginCommand command = this.getCommand(instance.getName());
        assert command != null;
        Commands commands = new Commands();
        command.setExecutor(commands);
        command.setTabCompleter(commands);
        // 初始化监听器
        DamageListener.initialize();
        QuitListener.initialize();
    }

    @Override
    public void onDisable() {
    }
}
