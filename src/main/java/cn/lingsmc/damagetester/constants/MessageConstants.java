package cn.lingsmc.damagetester.constants;

import cn.lingsmc.damagetester.DamageTester;
import lombok.Getter;

import static cn.lingsmc.damagetester.constants.CommandConstants.*;

/**
 * 全部玩家可见文案（全中文；配色遵循泠氏色板）。
 *
 * @author Crsuh2er0, 16870
 * @since 2023/1/18
 */
public class MessageConstants {
    /** 品牌色板（美学规范：标题 §3 / 强调 §b / 成功 §a / 错误 §c / 警告 §e / 次要 §7） */
    public static final String COLOR_PRIMARY = "§3";
    public static final String COLOR_ACCENT = "§b";
    public static final String COLOR_SUCCESS = "§a";
    public static final String COLOR_ERROR = "§c";
    public static final String COLOR_WARNING = "§e";
    public static final String COLOR_SECONDARY = "§7";
    /** 动作条伤害文案的固定部分 */
    public static final String DAMAGE_LABEL = "§7伤害";
    public static final String DAMAGE_CAUSE_FORMAT = " §7(%s)";

    public static final String RELOAD_SUCCESS = "§a重载成功.";
    public static final String UNKNOWN_COMMAND = "§c未知命令.";
    public static final String NO_PERMISSION = "§c你没有执行该命令的权限.";
    public static final String CONSOLE = "§c该命令必须由玩家执行.";

    public static final String MODE_ENABLED = "§a测试模式已开启，受到伤害后将自动回满血.";
    public static final String MODE_DISABLED = "§a测试模式已关闭.";
    public static final String ALREADY_IN_TEST_MODE = "§e你已经在测试模式中.";
    public static final String NOT_IN_TEST_MODE = "§e你当前不在测试模式中.";
    public static final String STATUS_ON = "§3测试模式: §b开启中";
    public static final String STATUS_OFF = "§3测试模式: §7关闭";

    static DamageTester plugin = DamageTester.getInstance();
    @Getter
    protected static final String[] ROOT_MESSAGE = new String[]{
            String.format("%s此服务器正在运行 %s%s %s%s by %s", COLOR_PRIMARY, COLOR_ACCENT, plugin.getName(),
                    plugin.getDescription().getVersion(), COLOR_PRIMARY, COLOR_ACCENT + "16870"),
            String.format("%s命令列表: %s/%s %s", COLOR_PRIMARY, COLOR_ACCENT, ALIAS, HELP),
    };
    @Getter
    protected static final String[] HELP_MESSAGE = new String[]
            {
                    String.format("%s§l----- %s指令 -----", COLOR_PRIMARY, plugin.getName()),
                    String.format("%s/%s %s %s- %s进入测试模式", COLOR_ACCENT, ALIAS, ON, COLOR_PRIMARY, COLOR_SUCCESS),
                    String.format("%s/%s %s %s- %s退出测试模式", COLOR_ACCENT, ALIAS, OFF, COLOR_PRIMARY, COLOR_SUCCESS),
                    String.format("%s/%s %s %s- %s切换测试模式", COLOR_ACCENT, ALIAS, TOGGLE, COLOR_PRIMARY, COLOR_SUCCESS),
                    String.format("%s/%s %s %s- %s查看测试模式状态", COLOR_ACCENT, ALIAS, STATUS, COLOR_PRIMARY, COLOR_SUCCESS),
                    String.format("%s/%s %s %s- %s重载本插件", COLOR_ACCENT, ALIAS, RELOAD, COLOR_PRIMARY, COLOR_SUCCESS),
            };

    private MessageConstants() {
    }
}
