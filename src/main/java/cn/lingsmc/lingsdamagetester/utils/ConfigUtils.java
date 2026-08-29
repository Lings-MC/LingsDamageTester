package cn.lingsmc.lingsdamagetester.utils;

import cn.lingsmc.lingsdamagetester.LingsDamageTester;
import cn.lingsmc.lingsdamagetester.constants.ConfigConstants;

/**
 * 配置文件初始化与读取。
 *
 * @author Crsuh2er0, 16870
 * @since 2023/1/16
 */
public class ConfigUtils {

    private ConfigUtils() {
    }

    public static void initialize() {
        LingsDamageTester plugin = LingsDamageTester.getInstance();
        // 初始化配置文件
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        plugin.getConfig().options().copyDefaults(true);
    }

    /**
     * @return 伤害提示是否显示来源类型（reload 后即时生效）
     */
    public static boolean showCause() {
        return LingsDamageTester.getInstance().getConfig()
                .getBoolean(ConfigConstants.SHOW_CAUSE, ConfigConstants.DEFAULT_SHOW_CAUSE);
    }

    /**
     * @return 是否写入伤害测试日志（reload 后即时生效）
     */
    public static boolean damageLog() {
        return LingsDamageTester.getInstance().getConfig()
                .getBoolean(ConfigConstants.DAMAGE_LOG, ConfigConstants.DEFAULT_DAMAGE_LOG);
    }
}
