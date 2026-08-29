package cn.lingsmc.lingsdamagetester.constants;

/**
 * 配置文件键与默认值常量。
 *
 * @author 16870
 * @since 2026/8/29
 */
public class ConfigConstants {
    /** 伤害提示是否显示来源类型 */
    public static final String SHOW_CAUSE = "show-cause";
    /** show-cause 默认开启 */
    public static final boolean DEFAULT_SHOW_CAUSE = true;
    /** 是否写入伤害测试日志 */
    public static final String DAMAGE_LOG = "damage-log";
    /** damage-log 默认开启 */
    public static final boolean DEFAULT_DAMAGE_LOG = true;

    private ConfigConstants() {
    }
}
